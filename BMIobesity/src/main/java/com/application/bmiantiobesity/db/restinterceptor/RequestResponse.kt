package com.application.bmiantiobesity.db.restinterceptor

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.application.bmiantiobesity.db.restinterceptor.forSearch.RequestResponseFTS4

@Entity(tableName = "list_of_request")
data class RequestResponse(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    // Request
    @ColumnInfo(name = "request_date")
    var requestDate: String = "",

    @ColumnInfo(name = "request_method")
    var requestMethod: String = "",

    @ColumnInfo(name = "request_url")
    var requestUrl: String = "",

    @ColumnInfo(name = "request_headers")
    var requestHeaders: String = "",

    @ColumnInfo(name = "request_body")
    var requestBody: String? = null,

    @ColumnInfo(name = "request_content_type")
    var requestContentType: String = "",

    @ColumnInfo(name = "request_content_length")
    var requestContentLength: Long? = 0L,

    // Response
    @ColumnInfo(name = "response_time")
    var responseTime: Long = 0L,

    @ColumnInfo(name = "response_headers")
    var responseHeaders: String = "",

    @ColumnInfo(name = "response_code")
    var responseCode: Int = 0,

    @ColumnInfo(name = "response_message")
    var responseMessage: String = "",

    @ColumnInfo(name = "response_protocol")
    var responseProtocol: String = "",

    @ColumnInfo(name = "response_body")
    var responseBody: String = ""
) {
    override fun toString(): String {
        return "${id}\n" + "${requestDate}\n" + "${requestMethod}\n" + requestUrl
    }
}


@Dao
interface RequestResponseDAO {

    @Query("SELECT * FROM list_of_request ORDER BY id DESC")
    fun getAll(): LiveData<List<RequestResponse>>

    @Query("DELETE FROM list_of_request")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: RequestResponse): Long

    @Update
    fun update(item: RequestResponse)

    @Delete
    fun delete(item: RequestResponse)

    @Query("SELECT * FROM list_of_request JOIN body_fts ON list_of_request.id == body_fts.rowid WHERE body_fts.response_body MATCH :body ORDER BY id")
    fun searchBody(body: String): List<RequestResponse>
}

@Database(
    entities = [RequestResponse::class, RequestResponseFTS4::class],
    version = 1,
    exportSchema = false
)
abstract class RequestResponseDB : RoomDatabase() {

    abstract fun getDao(): RequestResponseDAO

    companion object {
        @Volatile
        private var INSTANCE: RequestResponseDB? = null

        fun getDataBase(context: Context, name:String): RequestResponseDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RequestResponseDB::class.java,
                    name
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}