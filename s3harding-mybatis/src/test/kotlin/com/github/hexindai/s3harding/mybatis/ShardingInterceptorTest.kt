package com.github.hexindai.s3harding.mybatis

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShardingInterceptorTest : BaseDataTest() {

    private lateinit var sqlSessionFactory: SqlSessionFactory

    @BeforeAll
    fun setUp() {

        Resources.getResourceAsReader("com/github/hexindai/s3harding/mybatis/mybatis-config.xml").use { reader ->
            sqlSessionFactory = SqlSessionFactoryBuilder().build(reader)
        }

        runScript(sqlSessionFactory.configuration.environment.dataSource, NewVFundIO_DDL)
        runScript(sqlSessionFactory.configuration.environment.dataSource, NewVFundIO_DML)
    }

    @Test
    fun `SelectMapper - getOneNewVFundIO`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(SelectMapper::class.java)
        val io = mapper.getOneNewVFundIO()!!
        assertEquals(NewVFundIO(1, BigDecimal("12.34")), io)
    }

    @Test
    fun `SelectMapper - getOneNewVFundIOWithoutS3harding`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(SelectMapper::class.java)
        val io = mapper.getOneNewVFundIOWithoutS3harding()!!
        assertEquals(NewVFundIO(1, BigDecimal("12.34")), io)
    }

    @Test
    fun `SelectMapper - getOneNewFundIOByData`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(SelectMapper::class.java)
        val data = SearchData(148407, BigDecimal("12.34"), 148407)
        val io = mapper.getOneNewVFundIOByData(data)
        assertEquals(NewVFundIO(1, BigDecimal("12.34")), io)
    }

    @Test
    fun `SelectMapper - getOneNewFundIOByFromUserId`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(SelectMapper::class.java)
        val io = mapper.getOneNewVFundIOByFromUserId(148407)
        assertEquals(NewVFundIO(1, BigDecimal("12.34")), io)
    }

    @Test
    fun `SelectMapper - getOneNewFundIOByFromUserIdWithParam`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(SelectMapper::class.java)
        val io = mapper.getOneNewVFundIOByFromUserIdWithParam(148407)
        assertEquals(NewVFundIO(1, BigDecimal("12.34")), io)
    }

    @Test
    fun `SelectMapper - getOneNewVFundIOByFromUserIdWithParamAndColumnName`() {
        val openSession = sqlSessionFactory.openSession()
        val mapper = openSession.getMapper(SelectMapper::class.java)
        val io = mapper.getOneNewVFundIOByFromUserIdWithParamAndColumnName(148407)
        assertEquals(NewVFundIO(1, BigDecimal("12.34")), io)
    }

    @Test
    fun `InsertMapper - insertAndGetNewVFundIO`() {
        val openSession = sqlSessionFactory.openSession()
        val insertMapper = openSession.getMapper(InsertMapper::class.java)
        val insertData = InsertData(num = BigDecimal("22.22"), fromUserId = 148407)
        insertMapper.insertAndGetNewVFundIO(insertData)
        assertEquals(3, insertData.id)
    }

}