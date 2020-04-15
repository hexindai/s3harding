package com.github.hexindai.s3harding.mybatis

import com.github.hexindai.s3harding.core.annotation.S3harding
import org.apache.ibatis.annotations.Delete

interface DeleteMapper {

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id", paramName = "fromUserId")
    @Delete("delete from New_V_FundIO where from_user_id = #{fromUserId} and num = 22.34")
    fun deleteNewVFundIOByFromUserId(fromUserId: Int): Int

}