package com.application.bmiantiobesity.retrofit

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import com.application.bmiantiobesity.BuildConfig
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.application.bmiantiobesity.ui.main.MainViewModel
import com.application.bmiantiobesity.utilits.getDevice
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


// Обработчик ошибок
data class ErrorMessage(val detail:String)
data class ErrorResult(val isKnowError:Boolean, val message:String)

fun showErrorIfNeed(context: Context, error: Throwable) {
    val errorResult = errorAnalyzer(
        context,
        parserError(error)
    )
    if (errorResult.isKnowError) Toast.makeText(context, errorResult.message, Toast.LENGTH_SHORT).show()
    else if (BuildConfig.DEBUG) Toast.makeText(context, errorResult.message, Toast.LENGTH_SHORT).show()
}

fun errorAnalyzer(context: Context?, errorMessage: ErrorMessage): ErrorResult {
    var errorResult = ErrorResult(
        false,
        errorMessage.detail
    )
    context?.let{
        errorResult =
            when (errorMessage.detail){
                "No active account found with the given credentials" -> ErrorResult(
                    true,
                    it.getString(R.string.error_unauthorized)
                )
                "UnknownHostException" -> ErrorResult(
                    true,
                    it.getString(R.string.error_connection)
                )
                "SocketTimeoutException" -> ErrorResult(
                    true,
                    it.getString(R.string.error_socket_timeout)
                )
                "Not found." -> ErrorResult(
                    true,
                    it.getString(R.string.error_not_found)
                )
                "Token is invalid or expired" -> ErrorResult(
                    true,
                    it.getString(R.string.error_token_invalid)
                )
                "Given token not valid for any token type" -> ErrorResult(
                    true,
                    it.getString(R.string.error_token_invalid)
                )
                else -> errorResult //context.getString(R.string.error_unknown)
            }
    }
    return errorResult
}

fun parserError(error: Throwable): ErrorMessage =
    when (error) {
        is HttpException -> {
            val messageJson = error.response()?.errorBody()?.string() ?: ""

            if (messageJson.isNotEmpty()) {
                try {
                    val mJson = JsonParser().parse(messageJson)
                    Gson().fromJson(mJson, ErrorMessage::class.java)
                } catch (ex: IOException) {
                    ErrorMessage(
                        messageJson
                    )
                    //ErrorMessage(ex.message ?: "")
                }
            }
            else ErrorMessage("")
        }
        is UnknownHostException -> ErrorMessage(
            "UnknownHostException"
        )
        is SocketTimeoutException -> ErrorMessage(
            "SocketTimeoutException"
        )
        else -> ErrorMessage(
            error.message ?: ""
        )
    }

// Finish Activity if Token not valid
fun finishActivityIfTokenNotValid(activity: FragmentActivity, error: Throwable) {
    if (error is HttpException) if (error.code() == 403) activity.finish()
}

// Обновление токена если это нужно
fun updateTokenIfItNeed(context: Context, viewModel: MainViewModel, error: Throwable){
    if (error is HttpException)
        if (error.code() == 403) {

            var disposableRefresh: Disposable? = null
            // Восстановление имени пользователя и пароля
            val sharedPreferences = context.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
            //Refresh
            sharedPreferences?.let {
                val refresh = Refresh(
                    it.getString(
                        LoginViewModel.REFRESH_TOKEN,
                        ""
                    ) ?: ""
                )
                if (refresh.refresh != "") {
                    val sendRefresh =
                        SendRefresh(
                            refresh,
                            getDevice(context)
                        )
                    // Проверка на сервере. сюда прилетает
                    disposableRefresh = viewModel.refreshToken(sendRefresh)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->

                            // Refresh
                            Log.d("Response refresh -", result.toString())
                            MainViewModel.userToken = result
                            // Сохранение refresh token
                            sharedPreferences.edit {putString(LoginViewModel.REFRESH_TOKEN, result.refresh) }

                            disposableRefresh?.let {disposable ->  if (!disposable.isDisposed) disposable.dispose()}
                        },
                            { error ->
                                showErrorIfNeed(
                                    context,
                                    error
                                )
                                disposableRefresh?.let {disposable ->  if (!disposable.isDisposed) disposable.dispose()}})
                }
            }
        }
}