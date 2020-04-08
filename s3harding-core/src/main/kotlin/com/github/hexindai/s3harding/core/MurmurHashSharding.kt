package com.github.hexindai.s3harding.core

import org.apache.commons.codec.digest.MurmurHash2
import java.util.*

open class MurmurHashSharding : Sharding {

    private lateinit var tableNamePrefix: String

    private var shardingCount = 512

    private var numOfNodesPerTable = 10

    private var seed = 0x00000000

    private val nodes: SortedMap<Long, String> = TreeMap()

    private val tableNames = mutableListOf<String>()

    override val numberOfTable: Int
        get() = tableNames.size

    override val numberOfNode: Int
        get() = nodes.size

    private fun initThis() {
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

    override fun setProperties(properties: Properties?) {
        if (properties == null) throw IllegalArgumentException("Properties for MurmurHashSharing is empty")
        tableNamePrefix = properties["tableNamePrefix"] as String? ?: throw IllegalArgumentException("Property tableNamePrefix is empty")
        shardingCount = (properties["shardingCount"] as String?)?.toIntOrNull() ?: throw IllegalArgumentException("Property shardingCount is empty")
        seed = (properties["seed"] as String?)?.toIntOrNull(16) ?: throw IllegalArgumentException("Property seed is empty")
        numOfNodesPerTable = (properties["numOfNodesPerTable"] as String?)?.toIntOrNull() ?: throw IllegalArgumentException("Property numOfNodesPerTable is empty")
        initThis()
    }
}
