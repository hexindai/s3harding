package com.github.hexindai.s3harding.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class MurmurHashShardingTest {

    private lateinit var sharding: ConfigurableSharding

    @BeforeEach
    private fun setUp() {
        sharding = MurmurHashSharding()
        sharding.setProperties(Properties().apply{
            this["tableNamePrefix"] = "New_V_FundIO_"
            this["shardingCount"] = "512"
            this["seed"] = "1234ABCD"
            this["numOfNodesPerTable"] = "10"
        })
    }

    @Test
    fun `test numberOfTable and `() {

        val actualNumberOfTable = sharding.numberOfTable
        val actualNumberOfNode = sharding.numberOfNode

        assertEquals(512, actualNumberOfTable)
        assertEquals(512 * 10, actualNumberOfNode)

    }

    @Test
    fun getShardingTableName() {

        val actual = sharding.getShardingTableName(shardKey = "148407")
        assertEquals("New_V_FundIO_449", actual)

    }

    @Test
    fun `getShardingTableName with bad arguments`() {
        assertThrows(AssertionError::class.java) {
            MurmurHashSharding().setProperties(Properties().apply{
                this["tableNamePrefix"] = "New_V_FundIO_"
                this["shardingCount"] = "-1"
                this["seed"] = "1234ABCD"
                this["numOfNodesPerTable"] = "10"
            })
        }
    }
}