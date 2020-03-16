package com.github.hexindai.sharding

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MurmurHashShardingTest {

    @Test
    fun getShardingTableName() {
        val sharding: Sharding = MurmurHashSharding("New_V_FundIO_")
        val actual = sharding.getShardingTableName(shardingKey = "148407")
        assertEquals("New_V_FundIO_449", actual)
    }
}