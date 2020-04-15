package com.github.hexindai.s3harding.mybatis

import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import javax.sql.DataSource

abstract class BaseDataTest {

    companion object {

        const val NewVFundIO_DDL = "com/github/hexindai/s3harding/mybatis/databases/newvfundio/newvfundio-mysql-schema.sql"
        const val NewVFundIO_DML = "com/github/hexindai/s3harding/mybatis/databases/newvfundio/newvfundio-mysql-dataload.sql"

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