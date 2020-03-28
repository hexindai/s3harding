@file:JvmName("ShardingInterceptor")

package com.github.hexindai.s3harding.mybatis

import com.github.hexindai.s3harding.core.MurmurHashSharding
import com.github.hexindai.s3harding.core.Sharding
import com.github.hexindai.s3harding.core.annotation.S3harding
import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter
import net.sf.jsqlparser.expression.operators.relational.EqualsTo
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.util.deparser.SelectDeParser
import org.apache.ibatis.executor.statement.BaseStatementHandler
import org.apache.ibatis.executor.statement.RoutingStatementHandler
import org.apache.ibatis.executor.statement.StatementHandler
import org.apache.ibatis.mapping.BoundSql
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.plugin.Intercepts
import org.apache.ibatis.plugin.Invocation
import org.apache.ibatis.plugin.Signature
import java.lang.reflect.Method
import java.sql.Connection
import java.util.*

@Intercepts(Signature(type = StatementHandler::class, method = "prepare", args = [Connection::class, Integer::class]))
class ShardingInterceptor: Interceptor {

    private lateinit var sharding: Sharding

    @Throws(Exception::class)
    override fun intercept(invocation: Invocation): Any {
        val target = invocation.target
        var statementHandler = target as StatementHandler
        if (target is RoutingStatementHandler) {
            statementHandler = target.getDeclaredMemberProperty("delegate")!!
        }
        if (statementHandler is BaseStatementHandler) {
            val mappedStatement: MappedStatement = statementHandler.getDeclaredMemberProperty( "mappedStatement")!!
            val boundSql = statementHandler.boundSql

            val method = getMapperMethodByMappedStatementId(mappedStatement.id) ?: return invocation.proceed()
            val s3hardingAnnotation = method.getAnnotation(S3harding::class.java)
            val tableName = s3hardingAnnotation.tableName
            val columnName = s3hardingAnnotation.columnName

            var boundSqlString = boundSql.sql

            val shardKey = generateShardKey(boundSql, columnName) ?: throw Exception(
                    "SQL `$boundSqlString` does not contain column name `$columnName`"
            )

            val shardingTableName = generateTableNameByShardKey(shardKey)

            boundSqlString = formatSql(
                    sql = boundSqlString, fromTableName = tableName, toTableName = shardingTableName
            )

            val declaredSqlField = BoundSql::class.java.getDeclaredField("sql")
            declaredSqlField.isAccessible = true
            declaredSqlField.set(boundSql, boundSqlString)
        }
        return invocation.proceed()
    }

    override fun setProperties(properties: Properties?) {
        val prop = properties ?: Properties()
        val shardingClass =  (prop["shardingClass"] ?: IllegalArgumentException("shardingClass is empty")) as String
        val sharding = Class.forName(shardingClass).getConstructor().newInstance() as Sharding
        sharding.setProperties(prop)
        this.sharding = sharding
    }

    private fun generateShardKey(boundSql: BoundSql, columnName: String): String? {
        val boundSqlString = boundSql.sql
        var shardKey = getColumnValueFromSql(boundSqlString, columnName = columnName)
        // if sql with parameter placeholder
        if (shardKey == "?") {
            for (parameterMapping in boundSql.parameterMappings) {
                if (parameterMapping.property == columnName) {
                    val parameterObject = boundSql.parameterObject
                    shardKey = if (parameterObject::class.javaPrimitiveType != null) {
                        parameterObject.toString()
                    } else {
                        parameterObject.getDeclaredMemberProperty<Any>(columnName)?.toString()
                    }
                    break
                }
            }
        }
        return shardKey
    }

    private fun generateTableNameByShardKey(shardKey: String): String {
        return sharding.getShardingTableName(shardKey)
    }

    private fun getColumnValueFromSql(sql: String, columnName: String): String? {
        var value: String? = null
        val stmt = CCJSqlParserUtil.parse(sql) as Select
        (stmt.selectBody as PlainSelect).where.accept(object : ExpressionVisitorAdapter() {
            override fun visit(expr: EqualsTo) {
                super.visit(expr)
                if (expr.leftExpression.toString() == columnName) {
                    value = expr.rightExpression.toString()
                }
            }
        })
        return value
    }

    private fun formatSql(sql: String, fromTableName: String, toTableName: String): String {
        val select = CCJSqlParserUtil.parse(sql) as Select
        val selectDeParser = object : SelectDeParser() {
            override fun visit(table: Table) {
                when(table.name) {
                    fromTableName -> {
                        table.name = toTableName
                        table.alias = table.alias ?: Alias(fromTableName)
                    }
                }
            }
        }
        select.selectBody.accept(selectDeParser)
        return select.toString()
    }

    private fun getMapperMethodByMappedStatementId(mapId: String): Method? {
        val className = mapId.substringBeforeLast(".")
        val methodName = mapId.substringAfterLast(".")

        try {
            val clazz = Class.forName(className)
            val methods = clazz.methods
            for (method in methods) {
                if (method.name == methodName) {
                    return method
                }
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }

    private inline fun <reified R: Any> Any.getDeclaredMemberProperty(propertyName: String): R? {

        var clazz = this.javaClass as Class<in Any>
        while (clazz != Any::class.java) {
            try {
               val declaredField = clazz.getDeclaredField(propertyName)
                declaredField.isAccessible = true
                return declaredField.get(this) as R
            } catch (_: NoSuchFieldException) {
                clazz = clazz.superclass
            }
        }
        return null
    }

}