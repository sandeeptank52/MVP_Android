package com.application.bmiobesity.model.retrofit

class RemoteRepo private constructor(){

    private val intimeApi: InTimeDigitalApi = NetworkService.getNetworkService().getApi()

    suspend fun getCountries(locale: CurrentLocale) = safeApiCall { mGetCountries(locale) }
    private suspend fun mGetCountries(locale: CurrentLocale): RetrofitResult<ResultListCountries>{
        val result = intimeApi.getCountriesAsync(locale).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getGenders(locale: CurrentLocale) = safeApiCall { mGetGenders(locale) }
    private suspend fun mGetGenders(locale: CurrentLocale): RetrofitResult<ResultListGenders>{
        val result = intimeApi.getGendersAsync(locale).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getPolicy(locale: String) = safeApiCall { mGetPolicy(locale) }
    private suspend fun mGetPolicy(locale: String): RetrofitResult<ResultPolicy>{
        val result = intimeApi.getPolicyAsync(locale).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun isMailExist(mail: SendEmail) = safeApiCall { mIsMailExist(mail) }
    private suspend fun mIsMailExist(mail: SendEmail): RetrofitResult<ResultExist>{
        val result = intimeApi.isEmailExistAsync(mail).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getToken(login: SendLogin) = safeApiCall { mGetToken(login) }
    private suspend fun mGetToken(login: SendLogin): RetrofitResult<ResultToken>{
        val result = intimeApi.getTokenAsync(login).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun refreshToken(refresh: SendRefreshToken) = safeApiCall { mRefreshToken(refresh) }
    private suspend fun mRefreshToken(refresh: SendRefreshToken): RetrofitResult<ResultToken>{
        val result = intimeApi.refreshTokenAsync(refresh).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getTokenFromGoogle(code: SendGoogleTokenId) = safeApiCall { mGetTokenFromGoogle(code) }
    private suspend fun mGetTokenFromGoogle(code: SendGoogleTokenId): RetrofitResult<ResultTokenFromGoogle>{
        val result = intimeApi.getGoogleAuthAsync(code).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun signUp(login: SendLogin) = safeApiCall { mSignUp(login) }
    private suspend fun mSignUp(login: SendLogin): RetrofitResult<ResultToken>{
        val result = intimeApi.signUpAsync(login).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getProfile() = safeApiCall { mGetProfile() }
    private suspend fun mGetProfile(): RetrofitResult<ResultProfile>{
        val result = intimeApi.getProfileAsync("").await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }
    suspend fun patchProfile(profile: SendProfile) = safeApiCall { mPatchProfile(profile) }
    private suspend fun mPatchProfile(profile: SendProfile): RetrofitResult<ResultProfile>{
        val result = intimeApi.patchProfileAsync("", profile).await()
        if (result.isSuccessful)
            return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun passwordReset(mail: SendEmail) = safeApiCall { mPasswordReset(mail) }
    private suspend fun mPasswordReset(mail: SendEmail): RetrofitResult<String>{
        val result = intimeApi.passwordResetAsync(mail).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.message(), result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }
    suspend fun passwordResetConfirm(confirm: SendConfirmResetPass) = safeApiCall { mPasswordResetConfirm(confirm) }
    private suspend fun mPasswordResetConfirm(confirm: SendConfirmResetPass): RetrofitResult<String>{
        val result = intimeApi.passwordResetConfirmAsync(confirm).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.message(), result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getMedCard() = safeApiCall { mGetMedCard() }
    private suspend fun mGetMedCard(): RetrofitResult<ResultMedCard>{
        val result = intimeApi.getMedCardAsync("").await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getUserProfile() = safeApiCall { mGetUserProfile() }
    private suspend fun mGetUserProfile(): RetrofitResult<ResultUserProfile>{
        val result = intimeApi.getUserProfileAsync("").await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getFavorites(locale: String) = safeApiCall { mGetFavorites(locale) }
    private suspend fun mGetFavorites(locale: String): RetrofitResult<ResultFavorites>{
        val result = intimeApi.getFavoritesAsync("", locale).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getResultAnalyze(locale: String) = safeApiCall { mGetResultAnalyze(locale) }
    private suspend fun mGetResultAnalyze(locale: String): RetrofitResult<ResultAnalyze>{
        val result = intimeApi.getResultAnalyzeAsync("", locale).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

    suspend fun getRecommendations(locale: String) = safeApiCall { mGetRecommendations(locale) }
    private suspend fun mGetRecommendations(locale: String): RetrofitResult<List<ResultRecommendation>>{
        val result = intimeApi.getRecommendationsAsync("", locale).await()
        if (result.isSuccessful) return RetrofitResult.Success(result.body()!!, result.code(), result.message())
        return RetrofitResult.Error(result.message(), result.code(), result.errorBody())
    }

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