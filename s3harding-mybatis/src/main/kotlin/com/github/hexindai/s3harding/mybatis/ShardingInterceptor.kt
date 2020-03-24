@file:JvmName("ShardingInterceptor")

package com.github.hexindai.s3harding.mybatis

import com.github.hexindai.s3harding.core.MurmurHashSharding
import com.github.hexindai.s3harding.core.Sharding
import com.github.hexindai.s3harding.core.annotation.S3harding
import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
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
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.sql.Connection

@Intercepts(Signature(type = StatementHandler::class, method = "prepare", args = [Connection::class, Integer::class]))
class ShardingInterceptor: Interceptor {

    override fun intercept(invocation: Invocation): Any {
        val target = invocation.target
        var statementHandler: StatementHandler = target as StatementHandler
        if (target is RoutingStatementHandler) {
            statementHandler = target.getDeclaredMemberProperty("delegate")
        }
        if (statementHandler is BaseStatementHandler) {
            val mappedStatement: MappedStatement = statementHandler.getDeclaredMemberProperty( "mappedStatement")
            val boundSql: BoundSql = statementHandler.boundSql

            val method: Method = getMapperMethodByMappedStatementId(mappedStatement.id) ?: return invocation.proceed()
            val annotation: S3harding = method.getAnnotation(S3harding::class.java)
            val shardingKey = annotation.shardingKey
            val tableName = annotation.tableName

            val sharding: Sharding = MurmurHashSharding("New_V_FundIO_")
            val shardingTableName = sharding.getShardingTableName(shardingKey)

            var boundSqlString = boundSql.sql
            boundSqlString = replaceTableNameWithShardingTableNameInSql(boundSqlString, tableName, shardingTableName)
            val declaredSqlField = BoundSql::class.java.getDeclaredField("sql")
            declaredSqlField.isAccessible = true
            declaredSqlField.set(boundSql, boundSqlString)
        }
        return invocation.proceed()
    }

    private fun replaceTableNameWithShardingTableNameInSql(sql: String, tableName: String, shardingTableName: String): String {
        val select = CCJSqlParserUtil.parse(sql) as Select
        val selectDeParser = object : SelectDeParser() {
            override fun visit(table: Table) {
                when(table.name) {
                    tableName -> {
                        table.name = shardingTableName
                        table.alias = table.alias ?: Alias(tableName)
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
        val clazz: Class<*>
        return try {
            clazz = Class.forName(className)
            clazz.getMethod(methodName)
        } catch (e: Exception) {
            null
        }
    }

    private inline fun <reified R: Any> Any.getDeclaredMemberProperty(propertyName: String): R {

        var clazz = this.javaClass as Class<in Any>

        var declaredField: Field? = null

        while (declaredField == null && clazz != Any::class.java) {
            try {
                declaredField = clazz.getDeclaredField(propertyName)
            } catch (var4: NoSuchFieldException) {
                clazz = clazz.superclass
            }
        }
        declaredField!!.isAccessible = true
        return declaredField.get(this) as R
    }
}