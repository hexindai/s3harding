package com.github.hexindai.s3harding.mybatis

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class ShardingInterceptorTest : BaseDataTest() {

    private lateinit var sqlSessionFactory: SqlSessionFactory

    @BeforeEach
    fun setUp() {

        Resources.getResourceAsReader("com/github/hexindai/s3harding/mybatis/mybatis-config.xml").use { reader ->
            sqlSessionFactory = SqlSessionFactoryBuilder().build(reader)
        }

        runScript(sqlSessionFactory.configuration.environment.dataSource, NewVFundIO_DDL)
        runScript(sqlSessionFactory.configuration.environment.dataSource, NewVFundIO_DML)
    }

    @Test
    fun `testShardingInterceptor with getOneNewVFundIO`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(Mapper::class.java)
        val io = mapper.getOneNewVFundIO()!!
        assertEquals(NewVFundIO(148407, BigDecimal("12.34")), io)
    }

    @Test
    fun `testShardingInterceptor with getOneNewFundIOById`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(Mapper::class.java)
        val io = mapper.getOneNewVFundIOById(148407)
        assertEquals(NewVFundIO(148407, BigDecimal("12.34")), io)
    }

    @Test
    fun `testShardingInterceptor with getOneNewFundIOByData`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(Mapper::class.java)
        val data = SearchData(148407, BigDecimal("12.34"), 0)
        val io = mapper.getOneNewVFundIOByData(data)
        assertEquals(NewVFundIO(148407, BigDecimal("12.34")), io)
    }

    @Test
    fun `testShardingInterceptor with getOneNewFundIOByData2`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(Mapper::class.java)
        val data = SearchData(148407, BigDecimal("12.34"), 148407)
        val io = mapper.getOneNewVFundIOByData2(data)
        assertEquals(NewVFundIO(148407, BigDecimal("12.34")), io)
    }

    @Test
    fun `testShardingInterceptor with getOneNewFundIOByFromUserId`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(Mapper::class.java)
        val io = mapper.getOneNewVFundIOByFromUserId(148407)
        assertEquals(NewVFundIO(148407, BigDecimal("12.34")), io)
    }

    @Test
    fun `testShardingInterceptor with getOneNewFundIOByFromUserIdWithParam`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(Mapper::class.java)
        val io = mapper.getOneNewVFundIOByFromUserIdWithParam(148407)
        assertEquals(NewVFundIO(148407, BigDecimal("12.34")), io)
    }

    @Test
    fun `testShardingInterceptor with getOneNewVFundIOByFromUserIdWithParamAndColumnName`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(Mapper::class.java)
        val io = mapper.getOneNewVFundIOByFromUserIdWithParamAndColumnName(148407)
        assertEquals(NewVFundIO(148407, BigDecimal("12.34")), io)
    }

}