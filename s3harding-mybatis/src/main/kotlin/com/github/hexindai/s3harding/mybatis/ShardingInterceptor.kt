@file:JvmName("ShardingInterceptor")

package com.github.hexindai.s3harding.mybatis

import com.github.hexindai.s3harding.core.ConfigurableSharding
import com.github.hexindai.s3harding.core.Sharding
import com.github.hexindai.s3harding.core.annotation.S3harding
import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter
import net.sf.jsqlparser.expression.operators.relational.EqualsTo
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.update.Update
import net.sf.jsqlparser.util.deparser.ExpressionDeParser
import net.sf.jsqlparser.util.deparser.SelectDeParser
import org.apache.ibatis.executor.statement.BaseStatementHandler
import org.apache.ibatis.executor.statement.RoutingStatementHandler
import org.apache.ibatis.executor.statement.StatementHandler
import org.apache.ibatis.mapping.BoundSql
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.mapping.SqlCommandType
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.plugin.Intercepts
import org.apache.ibatis.plugin.Invocation
import org.apache.ibatis.plugin.Signature
import java.lang.reflect.Method
import java.sql.Connection
import java.util.*

@Intercepts(Signature(type = StatementHandler::class, method = "prepare", args = [Connection::class, Integer::class]))
class ShardingInterceptor : Interceptor {

    private lateinit var sharding: Sharding

    @Throws(Exception::class)
    override fun intercept(invocation: Invocation): Any {
        var statementHandler = invocation.target as StatementHandler
        if (statementHandler is RoutingStatementHandler) {
            statementHandler = statementHandler.getDeclaredMemberProperty("delegate")!!
        }
        if (statementHandler is BaseStatementHandler) {
            val mappedStatement: MappedStatement = statementHandler.getDeclaredMemberProperty("mappedStatement")!!
            val boundSql = statementHandler.boundSql

            val method = getMapperMethodByMappedStatementId(mappedStatement.id) ?: return invocation.proceed()
            val s3hardingAnnotation = method.getAnnotation(S3harding::class.java) ?: return invocation.proceed()

            val tableName = s3hardingAnnotation.tableName
            val columnName = s3hardingAnnotation.columnName
            val paramName = s3hardingAnnotation.paramName

            var boundSqlString = boundSql.sql

            val shardKey = generateShardKey(mappedStatement, boundSql, columnName, paramName) ?: throw Exception(
                    "SQL `$boundSqlString` does not contain column name `$columnName` " +
                            "or param name `$paramName` does not have value"
            )

            val shardingTableName = generateTableNameByShardKey(shardKey)

            boundSqlString = formatSql(
                    sql = boundSqlString,
                    fromTableName = tableName,
                    toTableName = shardingTableName,
                    sqlCommandType = mappedStatement.sqlCommandType
            )

            BoundSql::class.java.getDeclaredField("sql").apply {
                isAccessible = true
                set(boundSql, boundSqlString)
            }
        }
        return invocation.proceed()
    }

    override fun setProperties(properties: Properties?) {
        val prop = properties ?: Properties()
        val shardingClass = (prop["shardingClass"] ?: IllegalArgumentException("shardingClass is empty")) as String
        val sharding = Class.forName(shardingClass).getConstructor().newInstance() as ConfigurableSharding
        sharding.setProperties(prop)
        this.sharding = sharding
    }

    private fun generateShardKey(
            mappedStatement: MappedStatement,
            boundSql: BoundSql,
            columnName: String,
            paramName: String
    ): String? {
        val boundSqlString = boundSql.sql
        val parameterObject = boundSql.parameterObject
        val sqlCommandType = mappedStatement.sqlCommandType

        var shardKey: String? = getColumnValueFromSql(boundSqlString, columnName, sqlCommandType) ?: return null
        // if sql with parameter placeholder
        if (shardKey == "?") {
            shardKey = null // null means shardKey have not been generated or generated failed
            for (parameterMapping in boundSql.parameterMappings) {
                val propertyName = parameterMapping.property
                if (propertyName == paramName) {
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        shardKey = boundSql.getAdditionalParameter(propertyName).toString()
                        break
                    }
                    val configuration = mappedStatement.configuration
                    val typeHandlerRegistry = configuration.typeHandlerRegistry
                    if (typeHandlerRegistry.hasTypeHandler(parameterObject.javaClass)) {
                        shardKey = parameterObject.toString()
                        break
                    }
                    val metaObject = configuration.newMetaObject(parameterObject)
                    shardKey = metaObject.getValue(propertyName).toString()
                    break
                }
            }
        }
        return shardKey
    }

    private fun generateTableNameByShardKey(shardKey: String): String {
        return sharding.getShardingTableName(shardKey)
    }

    private fun getColumnValueFromSql(sql: String, columnName: String, sqlCommandType: SqlCommandType): String? {
        var value: String? = null
        val stmt = CCJSqlParserUtil.parse(sql)
        if (sqlCommandType == SqlCommandType.SELECT) {
            val selectStmt = stmt as Select
            (selectStmt.selectBody as PlainSelect).where.accept(object : ExpressionVisitorAdapter() {
                override fun visit(expr: EqualsTo) {
                    super.visit(expr)
                    if (expr.leftExpression.toString() == columnName) {
                        value = expr.rightExpression.toString()
                    }
                }
            })
        } else if (sqlCommandType == SqlCommandType.INSERT) {
            val insertStmt = stmt as Insert
            for ((index, column) in insertStmt.columns.withIndex()) {
                if (column.columnName == columnName) {
                    value = (insertStmt.itemsList as ExpressionList).expressions[index].toString()
                    break
                }
            }
        } else if (sqlCommandType == SqlCommandType.UPDATE) {
            val updateStmt = stmt as Update
            updateStmt.where.accept(object : ExpressionDeParser() {
                override fun visit(equalsTo: EqualsTo) {
                    super.visit(equalsTo)
                    if (equalsTo.leftExpression.toString() == columnName) {
                        value = equalsTo.rightExpression.toString()
                    }
                }
            })
        } else if (sqlCommandType == SqlCommandType.DELETE) {
            val deleteStmt = stmt as Delete
            deleteStmt.where.accept(object : ExpressionDeParser() {
                override fun visit(equalsTo: EqualsTo) {
                    super.visit(equalsTo)
                    if (equalsTo.leftExpression.toString() == columnName) {
                        value = equalsTo.rightExpression.toString()
                    }
                }
            })
        }
        return value
    }

    private fun formatSql(sql: String, fromTableName: String, toTableName: String, sqlCommandType: SqlCommandType): String {
        val stmt = CCJSqlParserUtil.parse(sql)
        if (sqlCommandType == SqlCommandType.SELECT) {
            val selectStmt = stmt as Select
            selectStmt.selectBody.accept(object : SelectDeParser() {
                override fun visit(table: Table) {
                    when (table.name) {
                        fromTableName -> {
                            table.name = toTableName
                            table.alias = table.alias ?: Alias(fromTableName)
                        }
                    }
                }
            })
        } else if (sqlCommandType == SqlCommandType.INSERT) {
            val insertStmt = stmt as Insert
            if (insertStmt.table.name == fromTableName) {
                insertStmt.table.name = toTableName
            }
        } else if (sqlCommandType == SqlCommandType.UPDATE) {
            val updateStmt = stmt as Update
            if (updateStmt.table.name == fromTableName) {
                updateStmt.table.name = toTableName
            }
        } else if (sqlCommandType == SqlCommandType.DELETE) {
            val deleteStmt = stmt as Delete
            if (deleteStmt.table.name == fromTableName) {
                deleteStmt.table.name = toTableName
            }
        }
        return stmt.toString()
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
        } catch (_: Exception) {
            return null
        }
    }

    private inline fun <reified R : Any> Any.getDeclaredMemberProperty(propertyName: String): R? {

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