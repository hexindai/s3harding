package com.github.hexindai.s3harding.mybatis

import java.math.BigDecimal

data class InsertData(
        val id: Int? = null,
        val num: BigDecimal,
        val fromUserId: Int
)