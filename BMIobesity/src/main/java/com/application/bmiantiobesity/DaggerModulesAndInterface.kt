package com.application.bmiantiobesity


import android.content.Context
import androidx.lifecycle.ViewModel
import com.application.bmiantiobesity.db.restinterceptor.RequestResponseDAO
import com.application.bmiantiobesity.db.restinterceptor.RequestResponseDB
import com.application.bmiantiobesity.db.restinterceptor.RequestResponseRepo

import com.application.bmiantiobesity.db.usersettings.ConfigDAO
import com.application.bmiantiobesity.db.usersettings.DBConfig
import com.application.bmiantiobesity.interceptor.RequestViewModel
import com.application.bmiantiobesity.retrofit.InTimeDigitalApi
import com.application.bmiantiobesity.retrofit.MyRetrofitQuery
import com.application.bmiantiobesity.utilits.CryptoApi
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.application.bmiantiobesity.ui.main.MainViewModel
import com.application.bmiantiobesity.ui.settings.SettingsViewModel
import dagger.*
import javax.inject.Singleton

@Component(modules = [RestApiModule::class, CryptoApiModule::class, RequestResponseRepoModule::class])
@Singleton
interface AppComponent{
    //DatabaseHelper getDatabaseHelper();

    /*@Component.Builder
    interface Builder{
        @BindsInstance
        fun withApplication(application: MultiDexApplication):Builder
        fun restApiModule(restApiModule: RestApiModule):Builder
        fun build():AppComponent
    }
    fun viewModelSubComponentBuilder():ViewModelSubComponent.Builder
    fun fragmentSubComponentBuilder():FragmentSubComponent.Builder*/

    //fun getInterceptorDAO(): RequestResponseDAO
    fun getInterceptorRepo(): RequestResponseRepo

    //fun <T: ViewModel> injectToViewModel(t:T)

    fun injectToViewModel(mainViewModel: MainViewModel)
    fun injectToViewModel(loginViewModel: LoginViewModel)
    fun injectToViewModel(settingsViewModel: SettingsViewModel)
    fun injectToViewModel(requestViewModel: RequestViewModel)
}


@Module
class ContextModule(var mContext: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return mContext
    }
}

@Module
class RestApiModule {
    // Подеключение к InTime
    @Provides
    @Singleton
    internal fun provideRetrofit(): InTimeDigitalApi {

        val baseUrl = "https://intime.digital/"
        return MyRetrofitQuery.getInstanceInTimeJson(baseUrl)
    }
}

@Module(includes = [ContextModule::class])
class CryptoApiModule {
    @Provides
    @Singleton
    fun provideCryptoApi(context: Context): CryptoApi {
        return CryptoApi(context)
    }
}

@Module(includes = [ContextModule::class])
class StorageModule {
    // Подеключение к БД SQL Lite (Room)
    @Provides
    @Singleton
    internal fun provideConfigRoomDatabase(context: Context): ConfigDAO {
        val dbName = "MyConfigDataBase"
        return DBConfig.getInstance(context, dbName).configDao()
    }

    @Provides
    @Singleton
    internal fun provideRequestResponseRoomDatabase(context: Context): RequestResponseDAO {
        val dbName = "request_response_db"
        return  RequestResponseDB.getDataBase(context, dbName).getDao()
    }
}

@Module(includes = [StorageModule::class])
class RequestResponseRepoModule{
    @Provides
    @Singleton
    fun provideRequestResponseRepo(requestResponseDAO: RequestResponseDAO): RequestResponseRepo{
        return RequestResponseRepo(requestResponseDAO)
    }
}

// Всё для ViewModel
/*@Subcomponent
interface  ViewModelSubComponent{
    @Subcomponent.Builder
    interface Builder{
        fun build():ViewModelSubComponent
    }

    fun inject(loginViewModel: LoginViewModel)
    fun inject(mainViewModel: GoogleFitApiModel)
}

@Subcomponent(modules = [ViewModelApiModule::class])
interface FragmentSubComponent{
    @Subcomponent.Builder
    interface Builder{
        @BindsInstance
        fun with(activity: FragmentActivity):Builder
        fun build():FragmentSubComponent
    }

    fun inject(loginActivity: LoginActivity)
    fun inject(LoginFragment:Fragment)
    fun inject(mainActivity: MainActivity)
}

@Module
class ViewModelApiModule {
    @Provides
    internal fun provideLoginViewModel(activity: FragmentActivity): LoginViewModel {
        return ViewModelProviders.of(activity).get(LoginViewModel::class.java)
    }

    @Provides
    internal fun provideMainViewModel(activity: FragmentActivity): GoogleFitApiModel {
        return ViewModelProviders.of(activity).get(GoogleFitApiModel::class.java)
    }
}*/

// Всё для ViewModel End
