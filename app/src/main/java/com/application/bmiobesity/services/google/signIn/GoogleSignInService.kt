package com.application.bmiobesity.services.google.signIn

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class GoogleSignInService private constructor(){

    private val serverClientId = "403561407577-tbsv2nomg2981hd1b7cd2dt7bek43267.apps.googleusercontent.com"

    private val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Scope(Scopes.EMAIL), Scope(Scopes.PROFILE))
        .requestServerAuthCode(serverClientId)
        .requestEmail()
        .build()

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInAccount: GoogleSignInAccount

    fun initClient(context: Context){
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    companion object{
        @Volatile
        var INSTANCE : GoogleSignInService? = null

        fun getGoogleSignInService(): GoogleSignInService{
            return INSTANCE ?: synchronized(this){
                val instance = GoogleSignInService()
                INSTANCE = instance
                instance
            }
        }
    }
}