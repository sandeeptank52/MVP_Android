package com.application.bmiobesity.model.retrofit

import com.application.bmiobesity.model.db.paramSettings.entities.Profile

class RemoteRepo private constructor(){

    private val intimeApi: InTimeDigitalApi = NetworkService.getNetworkService().getApi()

    suspend fun getCountries(locale: CurrentLocale) = safeApiCall { mGetCountries(locale) }
    private suspend fun mGetCountries(locale: CurrentLocale): RetrofitResult<ResultListCountries>{
        val result = intimeApi.getCountriesAsync(locale).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun getGenders(locale: CurrentLocale) = safeApiCall { mGetGenders(locale) }
    private suspend fun mGetGenders(locale: CurrentLocale): RetrofitResult<ResultListGenders>{
        val result = intimeApi.getGendersAsync(locale).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun getPolicy(locale: String) = safeApiCall { mGetPolicy(locale) }
    private suspend fun mGetPolicy(locale: String): RetrofitResult<ResultPolicy>{
        val result = intimeApi.getPolicyAsync(locale).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun isMailExist(mail: SendEmail) = safeApiCall { mIsMailExist(mail) }
    private suspend fun mIsMailExist(mail: SendEmail): RetrofitResult<ResultExist>{
        val result = intimeApi.isEmailExistAsync(mail).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun getToken(login: SendLogin) = safeApiCall { mGetToken(login) }
    private suspend fun mGetToken(login: SendLogin): RetrofitResult<ResultToken>{
        val result = intimeApi.getTokenAsync(login).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun signUp(login: SendLogin) = safeApiCall { mSignUp(login) }
    private suspend fun mSignUp(login: SendLogin): RetrofitResult<ResultToken>{
        val result = intimeApi.signUpAsync(login).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun getProfile(access: String) = safeApiCall { mGetProfile(access) }
    private suspend fun mGetProfile(access: String): RetrofitResult<ResultProfile>{
        val result = intimeApi.getProfileAsync(access).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }
    suspend fun patchProfile(access: String, profile: Profile) = safeApiCall { mPatchProfile(access, profile) }
    private suspend fun mPatchProfile(access: String, profile: Profile): RetrofitResult<ResultProfile>{
        val result = intimeApi.patchProfileAsync(access, profile).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun passwordReset(mail: SendEmail) = safeApiCall { mPasswordReset(mail) }
    private suspend fun mPasswordReset(mail: SendEmail): RetrofitResult<String>{
        val result = intimeApi.passwordResetAsync(mail).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.message(), result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }
    suspend fun passwordResetConfirm(confirm: SendConfirmResetPass) = safeApiCall { mPasswordResetConfirm(confirm) }
    private suspend fun mPasswordResetConfirm(confirm: SendConfirmResetPass): RetrofitResult<String>{
        val result = intimeApi.passwordResetConfirmAsync(confirm).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.message(), result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun getMedCard(access: String) = safeApiCall { mGetMedCard(access) }
    private suspend fun mGetMedCard(access: String): RetrofitResult<ResultMedCard>{
        val result = intimeApi.getMedCardAsync(access).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun getUserProfile(access: String) = safeApiCall { mGetUserProfile(access) }
    private suspend fun mGetUserProfile(access: String): RetrofitResult<ResultUserProfile>{
        val result = intimeApi.getUserProfileAsync(access).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }

    suspend fun getFavorites(access: String, locale: String) = safeApiCall { mGetFavorites(access, locale) }
    private suspend fun mGetFavorites(access: String, locale: String): RetrofitResult<ResultFavorites>{
        val result = intimeApi.getFavoritesAsync(access, locale).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }


    // Testing API
    /*suspend fun getTokenFromGoogle(login: SendGoogleTokenId) = safeApiCall { mGetTokenFromGoogle(login) }
    private suspend fun mGetTokenFromGoogle(login: SendGoogleTokenId): RetrofitResult<ResultTokenFromGoogle>{
        val result = intimeApi.getGoogleAuthAsync(login).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code())
    }*/


    companion object{
        @Volatile
        private var INSTANCE: RemoteRepo? = null

        fun getRemoteRepo(): RemoteRepo{
            return INSTANCE ?: synchronized(this){
                val instance = RemoteRepo()
                INSTANCE = instance
                instance
            }
        }
    }
}