package com.github.hexindai.s3harding.mybatis

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource
import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import javax.sql.DataSource

abstract class BaseDataTest {

    companion object {

        private const val NewVFundIOProperties = "com/github/hexindai/s3harding/mybatis/databases/newvfundio/newvfundio-hsqldb.properties"
        const val NewVFundIO_DDL = "com/github/hexindai/s3harding/mybatis/databases/newvfundio/newvfundio-hsqldb-schema.sql"
        const val NewVFundIO_DML = "com/github/hexindai/s3harding/mybatis/databases/newvfundio/newvfundio-hsqldb-dataload.sql"

        fun createNewVFundIODataSource(): DataSource {
            val ds = createUnpooledDataSource(NewVFundIOProperties)
            runScript(ds, NewVFundIO_DDL)
            runScript(ds, NewVFundIO_DML)
            return ds
        }

        private fun createUnpooledDataSource(resource: String): UnpooledDataSource {
            val properties = Resources.getResourceAsProperties(resource)
            val ds = UnpooledDataSource()
            ds.driver = properties.getProperty("driver")
            ds.url = properties.getProperty("url")
            ds.username = properties.getProperty("username")
            ds.password = properties.getProperty("password")
            return ds
        }

        fun runScript(ds: DataSource, resource: String) {
            val connection = ds.connection
            val runner = ScriptRunner(connection)
            runner.setAutoCommit(true)
            runner.setStopOnError(true)
            runner.setLogWriter(null)
            runner.setErrorLogWriter(null)
            runScript(runner, resource)
        }

        private fun runScript(scriptRunner: ScriptRunner, resource: String) {
            val reader = Resources.getResourceAsReader(resource)
            scriptRunner.runScript(reader)
        }
    }

}