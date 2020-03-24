package com.github.hexindai.s3harding.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MurmurHashShardingTest {

    @Test
    fun `test numberOfTable and `() {

        val sharding: Sharding = MurmurHashSharding("New_V_FundIO_", 100, 5)
        val actualNumberOfTable = sharding.numberOfTable
        val actualNumberOfNode = sharding.numberOfNode

        assertEquals(100, actualNumberOfTable)
        assertEquals(100 * 5, actualNumberOfNode)

    }

    @Test
    fun getShardingTableName() {

        val sharding: Sharding = MurmurHashSharding("New_V_FundIO_")
        val actual = sharding.getShardingTableName(shardingKey = "148407")
        assertEquals("New_V_FundIO_449", actual)

    }

    @Test
    fun `getShardingTableName with bad arguments`() {

        assertThrows(AssertionError::class.java) {
            MurmurHashSharding("New_V_FundIO_", -1, 10)
        }
    }
}