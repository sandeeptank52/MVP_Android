package com.application.bmiantiobesity.retrofit

import android.content.Context
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.application.bmiantiobesity.utilits.writeLogToFile
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

// Собственный перехватчик для Rest
class DebugWriteFileInterceptor(val context: Context?): Interceptor {
    companion object {
        private const val TAG_LOG = "LOG_REST_API"
        var id = 0
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        val invocation = request.tag(Invocation::class.java)
        val strRequest = "${request.method} - ${request.url} on ${chain.connection()} \n ${request.headers} \n ${invocation?.arguments() ?: "-"}"
        //Log.d(TAG_LOG, strRequest)
        if (LoginViewModel.isStoragePermissionGranted) context?.let { writeLogToFile(context, strRequest) }
        //context?.let { createNotification(context, strRequest, id++)}

        val response = chain.proceed(request)

        val t2 = System.nanoTime()
        val responseBodyCopy = response.peekBody(Long.MAX_VALUE)
        val strResponse = "Code - ${response.code}, received response for  ${response.request.url} in ${(t2 - t1) / 1e6}ms \n ${response.headers} \n ${responseBodyCopy.string()}"
        //Log.d(TAG_LOG, strResponse)
        if (LoginViewModel.isStoragePermissionGranted) context?.let { writeLogToFile(context, strResponse) }
        //context?.let { createNotification(context, strResponse, id++)}

        return response
    }
}