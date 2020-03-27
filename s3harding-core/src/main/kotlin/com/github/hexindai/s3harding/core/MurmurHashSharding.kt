package com.github.hexindai.s3harding.core

import org.apache.commons.codec.digest.MurmurHash2
import java.util.*

class MurmurHashSharding(
        tableNamePrefix: String,
        shardingCount: Int = 512,
        numOfNodesPerTable: Int = 10
) : Sharding {

    private val seed = 0x1234ABCD

    private val nodes: SortedMap<Long, String> = TreeMap()

    private val tableNames = mutableListOf<String>()

    override val numberOfTable: Int
        get() = tableNames.size

    override val numberOfNode: Int
        get() = nodes.size

    init {
        assert(shardingCount > 0 && numOfNodesPerTable > 0) {
            "Parameter shardingCount and numOfNodes may not less then zero"
        }
        for (i in 0 until shardingCount) {
            val tableName = "$tableNamePrefix${i+1}"
            tableNames.add(tableName)
            for (n in 0 until numOfNodesPerTable) {
                nodes[this.hash("${tableName.toLowerCase()}*$n")] = tableName
            }
        }
    }

    private fun hash(shardingKey: String): Long {
        val bytes = shardingKey.toByteArray(Charsets.UTF_8)
        return MurmurHash2.hash64(bytes, bytes.size, seed)
    }

    override fun getShardingTableName(shardKey: String): String {
        val hashShardKey = this.hash(shardKey)
        val sortedMap = this.nodes.tailMap(hashShardKey)
        if (sortedMap.isEmpty()) {
            return nodes[nodes.firstKey()]!!
        }
        return nodes[sortedMap.firstKey()]!!
    }

}
