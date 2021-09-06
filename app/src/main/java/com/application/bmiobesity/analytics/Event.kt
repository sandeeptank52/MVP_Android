package com.application.bmiobesity.analytics

object AnalyticsEvent {
    const val ERROR = "api_error"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val RESTORE = "restore_user"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password"
    const val GET_PROFILE = "get_profile"
    const val GET_USER_PROFILE = "get_user_profile"
    const val GET_LOAD_FIRST_TIMESTAMP = "get_load_first_timestamp"
    const val PATCH_PROFILE = "patch_profile"
    const val UPDATE_DASHBOARD = "update_dashboard"
    const val DELETE_PROFILE = "delete_profile"
    const val PATCH_AVATAR = "patch_avatar"
    const val GET_FAVORITE = "get_favorite"
    const val GET_ANALYZE = "get_analyze"
    const val GET_RECOMMENDATION = "get_recommendation"
    const val GET_MED_CARD = "get_recommendation"


}
object EventParam {
    const val LOGIN_SUCCESS = "login_success"
    const val LOGIN_ERROR = "login_error"
    const val REGISTER_SUCCESS = "register_success"
    const val REGISTER_ERROR = "register_error"
    const val FORGOT_PASSWORD_SUCCESS = "forgot_password_success"
    const val FORGOT_PASSWORD_ERROR = "register_error"
    const val RESET_PASSWORD_SUCCESS = "forgot_password_success"
    const val RESET_PASSWORD_ERROR = "register_error"
    const val ERROR_TYPE = "error_type"
    const val USER = "user"

}

object EventValue {
    const val REGISTER_EMAIL_ERROR = "register_email_error"
    const val CHECK_EMAIL_ERROR = "check_email_error"
}