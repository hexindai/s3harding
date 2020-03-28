package com.github.hexindai.s3harding.core

import java.util.*

interface Sharding {

    val numberOfTable: Int

    val numberOfNode: Int

    fun getShardingTableName(shardKey: String): String

    fun setProperties(properties: Properties?)

}