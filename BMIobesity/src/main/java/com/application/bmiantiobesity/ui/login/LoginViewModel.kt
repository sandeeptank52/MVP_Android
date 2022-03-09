package com.application.bmiantiobesity.ui.login

import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.animations.ShakeError
import com.application.bmiantiobesity.retrofit.*
import com.application.bmiantiobesity.utilits.CryptoApi
import com.application.bmiantiobesity.utilits.RxSearchObservable
import com.github.pwittchen.rxbiometric.library.RxBiometric
import com.github.pwittchen.rxbiometric.library.throwable.AuthenticationError
import com.github.pwittchen.rxbiometric.library.throwable.AuthenticationFail
import com.github.pwittchen.rxbiometric.library.throwable.BiometricNotSupported
import com.github.pwittchen.rxbiometric.library.validation.RxPreconditions
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class UserFirstLogin(var email:String, var password:String, var firstLogin:Boolean)

class LoginViewModel : ViewModel() {

    @Inject
    lateinit var inTimeDigital: InTimeDigitalApi
    @Inject
    lateinit var cryptoApi: CryptoApi

    companion object {
        const val USER_LOGIN_SETTINGS = "Login_Settings"
        const val USER_NAME = "User_Name"
        const val USER_PASSWORD = "User_Password"
        const val USER_FIRST_LOGIN = "User_First_login"
        const val USER_HAS_JUST_REGISTERED = "User_Has_Just_Registered" //For tracking if user has just completed registration but still needs to login
        const val IS_NOT_DISCLAIMER = "Disclaimer"
        const val ACCEPTED_TERMS_AND_CONDITIONS = "ACCEPTED_TERMS_AND_CONDITIONS_KEY"

        const val REFRESH_TOKEN = "Refresh_Token"
        const val ACCESS_TOKEN = "Access_Token"
        const val DEVICE_UUID = "Device_UUID"
        const val IS_FINGER_TOUCHE = "Is_Finger_Touche"
        const val PIN_CODE = "Pin_Code"
        //const val DEVICE_ID = "Device_ID"

        const val OS_NAME = "Android OS"

        const val PASSWORD_LENGTH = 6

        //const val SAFE_PASSWORD = "Safe_Password"
        var isStoragePermissionGranted: Boolean = false

        val liveUser = MutableLiveData<UserFirstLogin>()
        val liveResetPasswordString = MutableLiveData<String>()
        val refresh = MutableLiveData<Refresh>()
        val liveResultToken = MutableLiveData<ResultToken>()

        var singleResultToken = ResultToken("", "")
        var safePassword = false
        var isFingerTouch = false
        //var pinCode = "1234"
    }

    private var resultEmailSubscriber: Disposable? = null
    private var resultPasswordSubscriber: Disposable? = null
    private var disposableFinger: Disposable? = null

    private var policy: Policy? = null

    init {
        //Dagger2
        InTimeApplication.component?.injectToViewModel(this) //injectToLoginViewModel(this)
    }


    //fun checkUserNameExist(userName: String) = true

    fun checkEmailExistObservable(email:String) = inTimeDigital.isEmailExist(SendEmail(email)).map { it.exist }

    fun checkEmailFormat(email: String) = !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun checkIsEmailFormatView(context: Context, email: TextInputEditText, requestFocusAndMessage: Boolean) :Boolean {
        return if (!checkEmailFormat(email.text.toString())) {
            if (requestFocusAndMessage) {
                Toast.makeText(context, context.getText(R.string.wrong_email).toString(), Toast.LENGTH_SHORT).show()
                requestFocusAndAnimateError(email)
            }
            false
        } else true
    }


    fun checkIsPasswordView(context: Context, password: TextInputEditText):Boolean{
        if (password.text?.length ?: 0 < PASSWORD_LENGTH){
            Toast.makeText(context,  context.getText(R.string.wrong_password_length).toString(), Toast.LENGTH_SHORT).show()
            requestFocusAndAnimateError(password)
            return false
        } else {
            val alphabetCheck = "[a-zA-Zа-яА-Я]".toRegex()
            val numberCheck = "[0-9]".toRegex()
            val symbolCheck = "[!№;%:?()_+/*-.,|`~@#^&<>'{}$]".toRegex()

            when {
                password.text?.contains(alphabetCheck) == false -> {
                    Toast.makeText(context,  context.getText(R.string.wrong_password_alphabet).toString(), Toast.LENGTH_SHORT).show()
                    requestFocusAndAnimateError(password)
                    return false
                }
                password.text?.contains(numberCheck) == false -> {
                    Toast.makeText(context,  context.getText(R.string.wrong_password_number).toString(), Toast.LENGTH_SHORT).show()
                    requestFocusAndAnimateError(password)
                    return false
                }
                /*password.text?.contains(symbolCheck) == false -> {
                    Toast.makeText(context,  context.getText(R.string.wrong_password_symbol).toString(), Toast.LENGTH_SHORT).show()
                    requestFocusAndAnimateError(password)
                    return false
                }*/
                else -> return true
            }
        }
    }

    private fun generatePassword() = StringBuilder()
        .append(('0'..'z').map { it }.shuffled().subList(0, PASSWORD_LENGTH).joinToString(""))
        .append("[!№;%:?()_+/*-.,|`~@#^&<>'{}$]".random())
        .toString()

    fun generateGoodPassword(input:TextInputEditText){
        val alphabetCheck = "[a-zA-Z]".toRegex()
        val numberCheck = "[0-9]".toRegex()
        val symbolCheck = "[!№;%:?()_+/*-.,|`~@#^&<>'{}$]".toRegex()

        var result:Boolean
        var password:String
        do {
            password = generatePassword()
            when {
                !password.contains(alphabetCheck) -> result = false
                !password.contains(numberCheck) -> result = false
                !password.contains(symbolCheck) -> result = false
                else -> result = true
            }

        } while (!result)

        input.text?.clear()
        input.text?.insert(0, password)
        Log.d("Password", password)
    }


    fun checkIsEqualsPasswordsView(context: Context, passwordFirst:TextInputEditText, passwordSecond: TextInputEditText):Boolean{
        return if (passwordFirst.text.toString().compareTo(passwordSecond.text.toString()) != 0) {
            Toast.makeText(context, context.getText(R.string.wrong_password).toString(), Toast.LENGTH_SHORT).show()
            requestFocusAndAnimateError(passwordSecond)
            false
        } else true
    }

    private fun requestFocusAndAnimateError(textInputEditText: TextInputEditText) {
        textInputEditText.startAnimation(ShakeError)
        textInputEditText.requestFocus()
    }

    fun checkIsFieldEmpty(context: Context, editText1:TextInputEditText, editText2: TextInputEditText):Boolean{
        return if ((editText1.text.toString() == "") || (editText2.text.toString() == "")) {
            Toast.makeText(context, context.getText(R.string.empty_field), Toast.LENGTH_SHORT).show()
            true
        } else false
    }

    //fun checkEmailExistTest(email: String) = true

    fun passwordReset(email: SendEmail) = inTimeDigital.passwordReset(email)

    fun passwordResetConfirm(confirmReset: ConfirmReset) = inTimeDigital.passwordResetConfirm(confirmReset)

    fun getPolicy(locale: Locale) = if (policy != null) Observable.just(policy) else inTimeDigital.getPolicy(locale).doOnNext { policy = it }

    fun getTokenObservable(login: Login) = inTimeDigital.getUserToken(login)

    fun signUp(login: Login) = inTimeDigital.signUp(login)

    fun refreshToken(refresh: SendRefresh) = inTimeDigital.refreshUserToken(refresh)

    // Проверка email на существование
    fun setRxEmailControl(context: Context, emailText: EditText) {
        resultEmailSubscriber = RxSearchObservable.fromView(emailText)
            .debounce(1000, TimeUnit.MILLISECONDS) // :)
            .filter { it.isNotEmpty() }
            .filter { it.length >= 5 } // :)
            .filter { checkEmailFormat(it) }
            .distinctUntilChanged() //чтобы избежать дублирования одинаковых вызовов
            .doOnNext { Log.d("Email - send", it) } // Паралельные действия
            //.map { try {it.toInt()} catch (ex: Exception) {0}} // (String::toInt)// Не для Single
            /*.onErrorReturn{when (it){           // Это работает но актуально для Single
                is NumberFormatException -> return@onErrorReturn 0
                else -> throw IllegalArgumentException() }
            }*/
            //.switchMap { Observable.just(viewModel.checkEmailExistTest(it)) } // for test
            .switchMap { checkEmailExistObservable(it)}           // for work
            .doOnNext { Log.d("Result - email -", it.toString()) } // Паралельные действия
            //.onErrorReturnItem(employerViewModel.getDataErrorReturn()) // В случае ошибки обект
            //.onErrorResumeNext( Observable.just(true) )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ next ->
                next?.let {
                    if (!next) emailText.setTextColor( ContextCompat.getColor(context, R.color.goodText))
                    else {
                        Toast.makeText(context,"${emailText.text} ${context.getString(R.string.email_exist)}", Toast.LENGTH_SHORT).show()
                        emailText.setTextColor(ContextCompat.getColor(context, R.color.wrongText))
                        emailText.startAnimation(ShakeError)
                    }
                }
            },
                { error -> Log.d("Search - error", error?.message ?: "")})
    }

    fun setRxPasswordControl(context: Context, passwordText: EditText, image8: ImageView, imageNumber: ImageView, imageSymbol: ImageView){
        resultPasswordSubscriber = RxSearchObservable.fromView(passwordText)
            .debounce(300, TimeUnit.MILLISECONDS)
            .switchMap { Observable.just(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({next -> testPassword(next, image8, imageNumber, imageSymbol)},
                {error -> if (BuildConfig.DEBUG) Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()})

    }

    private fun testPassword(password: String, image8: ImageView, imageNumber: ImageView, imageSymbol: ImageView){
        val alphabetCheck = "[a-zA-Zа-яА-Я]".toRegex()
        val numberCheck = "[0-9]".toRegex()
        val symbolCheck = "[!№;%:?()_+/*-.,|`~@#^&<>'{}$]".toRegex()

        if (password.length < PASSWORD_LENGTH) image8.setImageResource(R.drawable.ic_icon_exclam)
        else if (!password.contains(alphabetCheck)) image8.setImageResource(R.drawable.ic_icon_exclam)
        else image8.setImageResource(R.drawable.ic_good_icon)

        if (!password.contains(numberCheck)) imageNumber.setImageResource(R.drawable.ic_icon_exclam)
        else imageNumber.setImageResource(R.drawable.ic_good_icon)

        /*if (!password.contains(symbolCheck)) imageSymbol.setImageResource(R.drawable.ic_icon_exclam)
        else  imageSymbol.setImageResource(R.drawable.ic_good_icon)*/
    }

    // Здесь проверка отпечатка RxFinger
    fun checkFinger(fragment:Fragment, functionIfChecked: () -> Unit, functionCheckedPinCode: () -> Unit){
        disposableFinger =
            RxPreconditions
                .hasBiometricSupport(fragment.requireContext())
                .flatMapCompletable {
                    if (!it) Completable.error(BiometricNotSupported())
                    else
                        RxBiometric
                            .title(fragment.getString(R.string.finger_title))
                            .description(fragment.getString(R.string.finger_description))
                            .negativeButtonText(fragment.getString(R.string.cancel))
                            .negativeButtonListener(DialogInterface.OnClickListener { _, _ ->
                                functionCheckedPinCode() //showMessage(fragment, fragment.getString(R.string.cancel))
                            })
                            .executor(ActivityCompat.getMainExecutor(fragment.requireContext()))
                            .build()
                            .authenticate(fragment.requireActivity())
                            //.doOnError{ error-> showMessage(fragment,"Error: ${error.message}") }
                }
                .observeOn(AndroidSchedulers.mainThread())
                //.doOnError {}
                .subscribeBy(
                    onComplete = { functionIfChecked() }, //showMessage(fragment,"Проверка прошла успешно!") },
                    onError = {
                        when (it) {
                            is BiometricNotSupported -> functionCheckedPinCode() //showMessage(fragment, fragment.getString(R.string.finger_error_not_support))
                            is AuthenticationError -> functionCheckedPinCode() //showMessage(fragment,"Error: ${it.errorCode} ${it.errorMessage}")//
                            is AuthenticationFail -> checkFinger(fragment, functionIfChecked, functionCheckedPinCode) //Unit // showMessage(fragment,"Fail") //functionCheckedPinCode()
                            else -> {
                                functionCheckedPinCode()
                                //showMessage(fragment, fragment.getString(R.string.error_unknown))
                            }
                        }
                    }
                )
    }

    private fun showMessage(fragment: Fragment, message: String) {
        Toast.makeText(fragment.requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        resultEmailSubscriber?.let {  if (!it.isDisposed) it.dispose()}
        resultPasswordSubscriber?.let {  if (!it.isDisposed) it.dispose()}
        disposableFinger?.let {  if (!it.isDisposed) it.dispose()}
        super.onCleared()
    }
}
