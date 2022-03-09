package com.application.bmiantiobesity.db.restinterceptor.forSearch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import com.application.bmiantiobesity.db.restinterceptor.RequestResponse

@Fts4(contentEntity = RequestResponse::class)
@Entity(tableName = "body_fts")
data class RequestResponseFTS4(
    @ColumnInfo(name = "response_body")
    val responseBody: String
)