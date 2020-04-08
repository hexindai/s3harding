package com.github.hexindai.s3harding.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CustomShardingTest {

    private lateinit var sharding: ConfigurableSharding

    @BeforeEach
    private fun setUp() {
        sharding = CustomSharding()
        sharding.setProperties(Properties().apply{
            this["tableNamePrefix"] = "New_V_FundIO_"
            this["shardingCount"] = "512"
            this["seed"] = "1234ABCD"
            this["numOfNodesPerTable"] = "10"
        })
    }

    @Test
    fun getShardingTableName() {
        val name0 = sharding.getShardingTableName("0")
        assertEquals("New_V_FundIO_0", name0)

        val name20 = sharding.getShardingTableName("20")
        assertEquals("New_V_FundIO_20", name20)

        val name148407 = sharding.getShardingTableName("148407")
        assertEquals("New_V_FundIO_449", name148407)
    }
}