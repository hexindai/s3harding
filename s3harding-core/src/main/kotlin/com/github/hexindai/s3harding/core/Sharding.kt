package com.github.hexindai.s3harding.core

interface Sharding {

    val numberOfTable: Int

    val numberOfNode: Int

    fun getShardingTableName(shardKey: String): String

}