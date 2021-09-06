package com.application.bmiobesity.di

import android.content.Context
import com.application.bmiobesity.common.eventManager.EventManager
import com.application.bmiobesity.common.parameters.MedCard
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.model.db.commonSettings.CommonSettingRepo
import com.application.bmiobesity.model.db.paramSettings.ParamSettingsRepo
import com.application.bmiobesity.model.localStorage.LocalStorageRepo
import com.application.bmiobesity.model.retrofit.RefreshTokenAuthenticator
import com.application.bmiobesity.model.retrofit.RemoteRepo
import com.application.bmiobesity.viewModels.LabelViewModel
import com.application.bmiobesity.viewModels.LoginViewModel
import com.application.bmiobesity.viewModels.MainViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [RemoteRepoModule::class,
    ParamSettingsRepoModule::class,
    LocalStorageRepoModule::class,
    CommonSettingsRepoModule::class,
    AppSettingModule::class,
    FirebaseAnalyticsModule::class])
interface ApplicationComponent{

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance context: Context): ApplicationComponent
    }

    fun getRemoteRepo(): RemoteRepo
    fun getParamSettingsRepo(): ParamSettingsRepo
    fun getLocalRepo(): LocalStorageRepo
    fun getCommonSettingRepo(): CommonSettingRepo
    fun getAppSetting(): AppSettingDataStore
    fun getFirebaseAnalytics(): FirebaseAnalytics

    fun inject(viewModel: LabelViewModel)
    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: MainViewModel)
    fun inject(medCard: MedCard)
    fun inject(tokenAuthenticator: RefreshTokenAuthenticator)
    fun inject(eventManager: EventManager)

}


@Module
class RemoteRepoModule{
    @Singleton
    @Provides
    fun provideRemoteRepo(): RemoteRepo {
        return RemoteRepo.getRemoteRepo()
    }
}

@Module
class ParamSettingsRepoModule{
    @Singleton
    @Provides
    fun provideParamSettingsRepo(context: Context): ParamSettingsRepo {
        return ParamSettingsRepo.getRepo(context)
    }
}

@Module
class LocalStorageRepoModule{
    @Singleton
    @Provides
    fun provideLocalStorageRepo(context: Context): LocalStorageRepo {
        return LocalStorageRepo(context)
    }
}

@Module
class CommonSettingsRepoModule{
    @Singleton
    @Provides
    fun provideCommonSettingsRepo(context: Context): CommonSettingRepo {
        return CommonSettingRepo.getCommonSettingRepo(context)
    }
}

@Module
class AppSettingModule{
    @Singleton
    @Provides
    fun provideAppSetting(context: Context): AppSettingDataStore {
        return AppSettingDataStore.getSettingApi(context)
    }
}
@Module
class FirebaseAnalyticsModule {
    @Singleton
    @Provides
    fun provideFirebaseAnalytics(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}