package com.application.bmiantiobesity.ui.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.text.color
import androidx.core.text.underline
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.retrofit.Login
import com.application.bmiantiobesity.retrofit.ResultToken
import com.application.bmiantiobesity.retrofit.User
import com.application.bmiantiobesity.retrofit.showErrorIfNeed
import com.application.bmiantiobesity.ui.main.MainActivity
import com.application.bmiantiobesity.utilits.getDevice
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

// Функция расшения для запуска Activity
inline fun <reified T> startMainActivity(context: Context, result: ResultToken, firstLogin: Boolean) {
    val mainActivity = Intent(context.applicationContext, T::class.java)
    mainActivity.putExtra(LoginViewModel.ACCESS_TOKEN, result.access)
    mainActivity.putExtra(LoginViewModel.REFRESH_TOKEN, result.refresh)
    mainActivity.putExtra(LoginViewModel.USER_FIRST_LOGIN, firstLogin)
    context.startActivity(mainActivity)
}

class LoginFragment : Fragment() {

    private var disposableToken: Disposable? = null
    private var disposableRefresh: Disposable? = null

    //private lateinit var viewModel: LoginViewModel
    private val viewModel by viewModels<LoginViewModel>()

    private var firstLogin:Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //viewModel = ViewModelProvider(this.requireActivity()).get(LoginViewModel::class.java)

        val view = inflater.inflate(R.layout.login_fragment, container, false)

        val textLogin = view.findViewById<TextInputEditText>(R.id.login_input_login)
        val textPassword = view.findViewById<TextInputEditText>(R.id.login_input_password)

        val progressBar = view.findViewById<ProgressBar>(R.id.login_progressBar)
        progressBar.isIndeterminate = false
        progressBar.isVisible = false

        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        //ColorStateList.valueOf( ContextCompat.getColor(this.requireContext(), R.color.button_enabled_endColor))

        LoginViewModel.liveUser.observe(this.requireActivity(), Observer<UserFirstLogin> {
            textLogin.setText(it.email)
            textPassword.setText(it.password)
            firstLogin = it.firstLogin
        })

        // Автоматический вход
        /*LoginViewModel.liveResultToken.observe(this.viewLifecycleOwner, Observer<ResultToken>{
            // Start Main Activity
            if (!LoginViewModel.isFingerTouch)  startMainActivity<MainActivity>(view.context, it, firstLogin)
            else viewModel.checkFinger(this, { startMainActivity<MainActivity>(view.context, it, firstLogin) }, {findNavController().navigate(R.id.checkPinFragment)})    //control finger

        })*/

        /*LoginViewModel.refresh.observe(this.requireActivity(), Observer<Refresh> {
            if (it.refresh.isNotEmpty() && (it.refresh != "-0-")) {
                setProgress(btLogin, progressBar, true)

                val sendRefresh = SendRefresh(it, getDevice(context!!))
                // Проверка на сервере. сюда прилетает
                disposableRefresh = viewModel.refreshToken(sendRefresh)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        // Auto login
                        Log.d("Response refresh -", result.toString())
                        LoginViewModel.singleResultToken = result

                        // Сохранение refresh token
                        context?.let { itContext ->
                            val sharedPreferences = itContext.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS,Context.MODE_PRIVATE)
                            sharedPreferences?.edit { putString(LoginViewModel.REFRESH_TOKEN, result.refresh)}
                        }

                        // Start Main Activity
                        if (!LoginViewModel.isFingerTouch)  startMainActivity<MainActivity>(view.context, result, firstLogin)
                        else viewModel.checkFinger(this, { startMainActivity<MainActivity>(view.context, result, firstLogin) }, {findNavController().navigate(R.id.checkPinFragment)})    //control finger
                        setProgress(btLogin, progressBar, false)
                    },
                    { error ->
                        setProgress(btLogin, progressBar, false)

                        val errorMessage = parserError(error)
                        Log.d("Response fail - ", errorMessage.toString())
                        Toast.makeText(context, errorAnalyzer(context, errorMessage),Toast.LENGTH_SHORT).show()
                    })
            }
        }) */

        val btSwitcher = view.findViewById<SwitchMaterial>(R.id.login_save_password_switch)
        btSwitcher.isChecked = LoginViewModel.safePassword


        val btRegistration = view.findViewById<TextView>(R.id.button_registration)
        val spnRegistration = SpannableStringBuilder( getString(R.string.registration))
            .append(" ")
            .underline { color(Color.BLUE) {append(getString(R.string.click_here))}}
        btRegistration.text = spnRegistration
        btRegistration.setOnClickListener {
            // Переход на другой фрагмент
            val action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
            it.findNavController().navigate(action)
        }

        //В случае восстановления пароля
        val btRemember = view.findViewById<TextView>(R.id.login_button_remember)
        val spnRemember = SpannableStringBuilder(getString(R.string.remember))
            .append(" ")
            .underline { color(Color.BLUE) {append(getString(R.string.click_here))}}
        btRemember.text = spnRemember
        btRemember.setOnClickListener {
            // Переход на форму восстановления пароля по email
            val emailAddress = textLogin.text.toString()
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment(emailAddress)
            it.findNavController().navigate(action)

        }

        val btLogin = view.findViewById<Button>(R.id.button_login)
        btLogin.setOnClickListener {

            setProgress(btLogin, progressBar, true)

            // Проверка полей ввода данных
            context?.let {
                if (viewModel.checkIsFieldEmpty(it, textLogin, textPassword)) {
                    setProgress(btLogin, progressBar, false)
                    return@setOnClickListener}

                if (!viewModel.checkIsEmailFormatView(it, textLogin, true)){
                    setProgress(btLogin, progressBar, false)
                    return@setOnClickListener
                }

                if (!viewModel.checkIsPasswordView(it, textPassword)){
                    setProgress(btLogin, progressBar, false)
                    return@setOnClickListener
                }
            }

            val user = User(
                textLogin.text.toString(),
                textPassword.text.toString()
            )
            val device = getDevice(requireContext())
            // Проверка на сервере.
            disposableToken =  viewModel.getTokenObservable(Login(user, device))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result ->
                    Log.d("Response -", result.toString())

                    LoginViewModel.singleResultToken = result

                    //Сохраняем значения
                    context?.let {
                        val sharedPreferences =  it.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
                        sharedPreferences?.edit { putString(LoginViewModel.USER_NAME, viewModel.cryptoApi.encryptString(textLogin.text.toString())) }
                        if (btSwitcher.isChecked) sharedPreferences?.edit {  putString(LoginViewModel.USER_PASSWORD, viewModel.cryptoApi.encryptString(textPassword.text.toString())) }
                        else sharedPreferences?.edit {  putString(LoginViewModel.USER_PASSWORD, "") }
                        // Сохранение refresh token
                        sharedPreferences?.edit{ putString(LoginViewModel.REFRESH_TOKEN, result.refresh)}
                    }

                    // Start Main Activity
                    if (!LoginViewModel.isFingerTouch) startMainActivity<MainActivity>(view.context, result, firstLogin)
                    else viewModel.checkFinger(this, { startMainActivity<MainActivity>(view.context, result, firstLogin) }, {findNavController().navigate(R.id.checkPinFragment)}) //control finger

                    setProgress(btLogin, progressBar, false)},
                    { error ->
                        setProgress(btLogin, progressBar, false)

                        showErrorIfNeed(this.requireContext(), error)
                        //val errorMessage = parserError(error)
                        Log.d("Response fail - ", error.message.toString())
                    }
                )

            //For Test Start Main Activity
            //if (!LoginViewModel.isFingerTouch) startMainActivity<MainActivity>(view.context, ResultToken(textLogin.text.toString(), textPassword.text.toString()), true) //firstLogin)
            //else viewModel.checkFinger(this, { startMainActivity<MainActivity>(view.context, LoginViewModel.singleResultToken, firstLogin) }, {findNavController().navigate(R.id.checkPinFragment)}) //control finger

        }

        // Test Crypt
        /*val btEncrypt = view.findViewById<Button>(R.id.button_encrypt)
        btEncrypt.setOnClickListener {
            Log.d("Crypt", "-----------")
            val str = viewModel.cryptoApi.encryptString(textLogin.text.toString())

            Log.d("Crypt", str)
            textLogin.text?.clear()
            textLogin.text?.insert(0, str)

            context?.let { val sharedPreferences =  it.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
                sharedPreferences?.edit { putString (LoginViewModel.SAFE_PASSWORD, str) }
            }

        }

        val btDecrypt = view.findViewById<Button>(R.id.button_decrypt)
        btDecrypt.setOnClickListener {
            Log.d("DeCrypt", "-----------")
            //val text = cryptoApi.decryptString(byteArray)
            context?.let { val sharedPreferences =  it.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
                val textCrypt = sharedPreferences?.getString(LoginViewModel.SAFE_PASSWORD, "")
                Log.d("DeCrypt", textCrypt ?: "")

                val text = viewModel.cryptoApi.decryptString(textCrypt ?: "")
                Log.d("DeCrypt", text)

                textLogin.text?.clear()
                textLogin.text?.insert(0, text)
            }
        }*/

        return view
    }

    private fun setProgress(btLogin: Button, progressBar: ProgressBar, progress: Boolean) {
        if (progress) {
            btLogin.isEnabled = false
            progressBar.isIndeterminate = true
            progressBar.isVisible = true
        } else {
            btLogin.isEnabled = true
            progressBar.isIndeterminate = false
            progressBar.isVisible = false
        }
    }

    override fun onDestroy() {
        disposableToken?.let { if (!it.isDisposed) it.dispose()}
        disposableRefresh?.let { if (!it.isDisposed) it.dispose()}
        super.onDestroy()
    }
}