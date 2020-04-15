package com.github.hexindai.s3harding.mybatis

import com.github.hexindai.s3harding.core.annotation.S3harding
import org.apache.ibatis.annotations.Update

interface UpdateMapper {

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id", paramName = "fromUserId")
    @Update("update New_V_FundIO set num = 32.34 where from_user_id = #{fromUserId} and num = 12.34")
    fun updateNewVFundIOWithFromUserID(fromUserId: Int): Int

}