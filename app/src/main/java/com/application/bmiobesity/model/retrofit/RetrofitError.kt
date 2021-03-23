package com.application.bmiobesity.model.retrofit

enum class RetrofitError {
    //code - 401, message - Unauthorized
    PASS_INCORRECT,

    //code - 404, message - Not Found
    MAIL_NOT_FOUND,

    //code - 0, message - Unable to resolve host "intime.digital": No address associated with hostname
    NO_INTERNET_CONNECTION,

    //Unknown error
    UNKNOWN_ERROR
}