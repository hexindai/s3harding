package com.github.hexindai.sharding

interface Sharding {

    val numberOfTable: Int

    val numberOfNode: Int

    fun getShardingTableName(shardingKey: String): String

}