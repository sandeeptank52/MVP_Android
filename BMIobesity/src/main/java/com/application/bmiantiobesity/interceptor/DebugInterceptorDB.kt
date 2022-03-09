package com.application.bmiantiobesity.interceptor


import android.util.Log
import com.application.bmiantiobesity.InTimeApplication
import com.application.bmiantiobesity.db.restinterceptor.RequestResponse
import com.application.bmiantiobesity.db.restinterceptor.RequestResponseRepo
import com.application.bmiantiobesity.utilits.createInterceptorNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DebugInterceptorDB : Interceptor{ //val context: Context?) : Interceptor {

    //private val mRequestRepo = RequestResponseRepo.getRepo()
    @Inject
    lateinit var  mRequestRepo : RequestResponseRepo

    init {
        InTimeApplication.component?.getInterceptorRepo()?.let { mRequestRepo = it }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val mRequestBody: RequestBody? = request.body
        val mRequestContentBuffer = Buffer()
        mRequestBody?.writeTo(mRequestContentBuffer)

        val requestResponse = RequestResponse()

        requestResponse.requestMethod = request.method
        requestResponse.requestUrl = request.url.toString()
        requestResponse.requestHeaders = request.headers.toString()
        requestResponse.requestBody = mRequestContentBuffer.readUtf8()
        requestResponse.requestContentType = mRequestBody?.contentType().toString()
        requestResponse.requestContentLength = mRequestBody?.contentLength()

        val dateFormat = SimpleDateFormat("dd.MM.yyyy_HH:mm:ss_z", Locale.getDefault())
        requestResponse.requestDate = dateFormat.format(Date())

        GlobalScope.launch(Dispatchers.IO) {
            requestResponse.id = mRequestRepo.insertReq(requestResponse)
            Log.d("DebugInterceptorDB", mRequestRepo.toString())
        }

        createInterceptorNotification(InTimeApplication.APPLICATION, requestResponse.requestUrl, 1)

        val t1 = System.nanoTime()
        val response = chain.proceed(request)
        val t2 = System.nanoTime()

        val responseBodyCopy: ResponseBody = response.peekBody(Long.MAX_VALUE)

        requestResponse.responseTime = TimeUnit.NANOSECONDS.toMillis((t2 - t1))
        requestResponse.responseCode = response.code
        requestResponse.responseProtocol = response.protocol.name
        requestResponse.responseHeaders = response.headers.toString()
        requestResponse.responseMessage = response.message
        requestResponse.responseBody = "Content Type: ${response.body?.contentType()
            .toString()}\n ${responseBodyCopy.string()}"

        GlobalScope.launch(Dispatchers.IO) {
            mRequestRepo.updateReq(requestResponse)
        }
        return response
    }
}