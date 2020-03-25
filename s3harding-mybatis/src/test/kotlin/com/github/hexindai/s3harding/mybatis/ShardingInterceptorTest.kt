package com.github.hexindai.s3harding.mybatis

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class ShardingInterceptorTest : BaseDataTest() {

    private var sqlSessionFactory: SqlSessionFactory? = null

    @BeforeEach
    fun setUp() {

        Resources.getResourceAsReader("com/github/hexindai/s3harding/mybatis/mybatis-config.xml").use { reader ->
            sqlSessionFactory = SqlSessionFactoryBuilder().build(reader)
        }

        runScript(sqlSessionFactory!!.configuration.environment.dataSource, NewVFundIO_DDL)
        runScript(sqlSessionFactory!!.configuration.environment.dataSource, NewVFundIO_DML)
    }

    @Test
    fun testShardingInterceptor() {
        val openSession = sqlSessionFactory!!.openSession()
        val mapper = openSession.getMapper(Mapper::class.java)
        val io = mapper.getOneNewVFundIO()
        assertEquals(NewVFundIO(148407, BigDecimal("12.34")), io)
    }

}