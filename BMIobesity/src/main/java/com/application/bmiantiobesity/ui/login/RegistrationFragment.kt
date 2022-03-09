package com.application.bmiantiobesity.ui.login


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog

import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.retrofit.Login
import com.application.bmiantiobesity.retrofit.User
import com.application.bmiantiobesity.retrofit.showErrorIfNeed
import com.application.bmiantiobesity.utilits.getCurrentLocale
import com.application.bmiantiobesity.utilits.getDevice
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText


import devit951.github.magictip.MagicTip
import devit951.github.magictip.animationdelegate.OvershootMagicTipAnimation
import devit951.github.magictip.tip.OneMagicTip
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.registration_fragment.view.*

/**
 * A simple [Fragment] subclass.
 */
class RegistrationFragment : Fragment(), View.OnFocusChangeListener {

    private var resultSignUpSubscriber: Disposable? = null
    private var getPolicySubscriber: Disposable? = null

    //private lateinit var viewModel: LoginViewModel
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var color:ColorStateList

    private lateinit var btSwitcherLicense: SwitchMaterial
    private lateinit var progressBar: ProgressBar

    private var licenseClick:Boolean = false

    private var oneMagicTip : OneMagicTip? = null
    private var isOneMagicTipShow: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //viewModel = ViewModelProvider(this.requireActivity()).get(LoginViewModel::class.java)

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.registration_fragment, container, false)

        progressBar = view.findViewById(R.id.registration_progressBar)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) progressBar.progressTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this.requireContext(), R.color.button_enabled_endColor))
        progressBar.isIndeterminate = false
        progressBar.isVisible = false

        // Отображение подсказки
        val imageToolTip = view.findViewById<View>(R.id.registration_tooltip)
        oneMagicTip = OneMagicTip(imageToolTip, MagicTip(imageToolTip).settings {
            text = context.getText(R.string.wrong_password_requirements)
            bgColor = Color.BLUE
            startAnimationDelegate = OvershootMagicTipAnimation()
            exitAnimationDelegate = OvershootMagicTipAnimation.Reversed() })
        //imageRequired.isFocusable = true
        val imageClickListener = View.OnClickListener {
            oneMagicTip?.show()
            isOneMagicTipShow = !isOneMagicTipShow
        }

        view.characters_imageview.setOnClickListener(imageClickListener)
        view.is_eigth_symbol_textview.setOnClickListener(imageClickListener)
        view.number_imageview.setOnClickListener(imageClickListener)
        view.number_textview.setOnClickListener(imageClickListener)
        view.symbol_imageview.setOnClickListener(imageClickListener)
        view.symbol_textview.setOnClickListener(imageClickListener)

        //imageRequired.onFocusChangeListener = this
        /*view.setOnFocusChangeListener { v, hasFocus ->
            if ((v is TextInputEditText) && (!hasFocus) && (isOneMagicTipShow))
            {
                oneMagicTip?.show()
                isOneMagicTipShow = false
            }
        }*/
        //view.onFocusChangeListener{ }

        //val userNameText = view.findViewById<EditText>(R.id.registration_input_login)
        val emailText = view.findViewById<TextInputEditText>(R.id.registration_input_email)
        color = emailText.textColors
        emailText.onFocusChangeListener = this
        viewModel.setRxEmailControl(this.requireContext(), emailText)

        val passwordFirstText = view.findViewById<TextInputEditText>(R.id.registration_input_password)
        viewModel.setRxPasswordControl(this.requireContext(), passwordFirstText, view.characters_imageview, view.number_imageview, view.symbol_imageview)

        val passwordSecondText = view.findViewById<TextInputEditText>(R.id.registration_input_password_rewrite)
        val btSwitcher = view.findViewById<SwitchMaterial>(R.id.save_password_switch)
        btSwitcher.isChecked = true

        btSwitcherLicense = view.findViewById(R.id.license_switch)
        btSwitcherLicense.isChecked = false
        val licenseText = view.findViewById<TextView>(R.id.license_textView)
        licenseText.setOnClickListener {
            licenseClick = true
            showLicenseDialog()

            //viewModel.generateGoodPassword(passwordFirstText)
            //Toast.makeText(context, "License", Toast.LENGTH_SHORT).show()
        }

        val btRegistration = view.findViewById<Button>(R.id.registration_button)
        btRegistration.isEnabled = btSwitcherLicense.isChecked

        btSwitcherLicense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !licenseClick) showLicenseDialog()
            btRegistration.isEnabled = isChecked
        }


        btRegistration.setOnClickListener {

            // Обработка данных
            context?.let {
                if (!viewModel.checkIsEmailFormatView(it, emailText, true)) return@setOnClickListener

                if (!viewModel.checkIsPasswordView(it, passwordFirstText)) return@setOnClickListener

                if (!viewModel.checkIsEqualsPasswordsView(it, passwordFirstText, passwordSecondText)) return@setOnClickListener
            }

            btRegistration.isEnabled = false

            val user = User(
                emailText.text.toString(),
                passwordFirstText.text.toString()
            )
            val device = getDevice(requireContext())

            // Оправка запроса на сервер
            resultSignUpSubscriber = viewModel.signUp(
                Login(
                    user,
                    device
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                        LoginViewModel.singleResultToken = result
                        btRegistration.isEnabled = true

                        // Сохраняем Значения
                        val sharedPreferences =  context?.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
                        sharedPreferences?.edit { putString(LoginViewModel.ACCESS_TOKEN, result.access).apply() }
                        sharedPreferences?.edit { putString(LoginViewModel.REFRESH_TOKEN, result.refresh).apply()  }
                        sharedPreferences?.edit { putBoolean(LoginViewModel.USER_FIRST_LOGIN, true).apply()  }
                        sharedPreferences?.edit { putBoolean(LoginViewModel.USER_HAS_JUST_REGISTERED, true).apply()  }
                        sharedPreferences?.edit { putString(LoginViewModel.USER_NAME, viewModel.cryptoApi.encryptString(emailText.text.toString())).apply()  }
                        if (btSwitcher.isChecked) sharedPreferences?.edit {  putString(LoginViewModel.USER_PASSWORD, viewModel.cryptoApi.encryptString(passwordFirstText.text.toString())).apply()  }
                        else sharedPreferences?.edit {  putString(LoginViewModel.USER_PASSWORD, "").apply()  }


                        // Отправка значений в начальный фрагмент
                        if (btSwitcher.isChecked) LoginViewModel.liveUser.value = UserFirstLogin(emailText.text.toString(), passwordFirstText.text.toString(), true)
                        else LoginViewModel.liveUser.value = UserFirstLogin(emailText.text.toString(), "", true)

                        // Запуск основной Активити
                        //startMainActivity<MainActivity>(view.context, result)
                        // Закрытие Фрагмента
                        view.findNavController().popBackStack()
                        // Вывод сообщения
                        showInfoDialog(R.string.finish_registration, "${context?.getText(R.string.finish_registration_text)} ${emailText.text.toString()}", R.string.button_understand, R.string.button_cancel, false)

                        },
                    { error ->
                        btRegistration.isEnabled = true
                        Log.d("SignUp Error -", error?.message ?: "")
                        showErrorIfNeed(
                            this.requireContext(),
                            error
                        )
                        // Test
                        // Закрытие Фрагмента
                        //view.findNavController().popBackStack()
                        // Вывод сообщения
                        //showInfoDialog(emailText.text.toString())
                    })


            //Результата обработки сервера для теста без инета
            /*val result = true
            if (result){
                // Сохраняем Значения
                val sharedPreferences =  context?.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
                sharedPreferences?.edit{ putString(LoginViewModel.USER_NAME, emailText.text.toString())}
                if (btSwitcher.isChecked) sharedPreferences?.edit {  putString(LoginViewModel.USER_PASSWORD, passwordFirstText.text.toString()) }
                else sharedPreferences?.edit {  putString(LoginViewModel.USER_PASSWORD, "") }

                // Отправка значений в начальный фрагмент
                if (btSwitcher.isChecked) LoginViewModel.liveUser.value = Login(emailText.text.toString(), passwordFirstText.text.toString())
                else LoginViewModel.liveUser.value = Login(emailText.text.toString(), "")

                // Закрытие Фрагмента
                it.findNavController().popBackStack()
            }*/

        }

        return  view
    }

    private fun showLicenseDialog() {
        loadProgressBar(true)
        getPolicySubscriber = viewModel.getPolicy(getCurrentLocale(requireContext()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({result ->
                loadProgressBar(false)
                showInfoDialog(R.string.license_agreement, result?.policy ?: "", R.string.license_agreement_button, R.string.button_cancel, true)},
                {
                    loadProgressBar(false)
                    showInfoDialog(R.string.license_agreement,context?.getString(R.string.license_agreement_text) ?: "", R.string.license_agreement_button, R.string.button_cancel, true)})
    }

    private fun loadProgressBar(isLoad: Boolean) {
        if (isLoad) {
            btSwitcherLicense.isEnabled = false
            progressBar.isIndeterminate = true
            progressBar.isVisible = true
        } else {
            btSwitcherLicense.isEnabled = true
            progressBar.isIndeterminate = false
            progressBar.isVisible = false
        }
    }

    private fun showInfoDialog(titleTextRes:Int, message: String,  buttonOKTextRes:Int,  buttonCancelTextRes:Int, positive:Boolean) {
        context?.let { context ->
            if (positive) {
                val resultView = this.layoutInflater.inflate(R.layout.dialog_policy, null)
                val webView = resultView.findViewById<WebView>(R.id.web_view)
                //webView.loadUrl(message)
                webView.loadData(message,"text/html", "UTF-8")
                //webView.loadData("<html><body><h1>${message}</body></html>", "text/html", "UTF-8")
                //webView.loadUrl("http:\\www.yandex.ru");
                webView.webViewClient = WebViewClient()

                //PDF
                /*val pdfView = resultView.findViewById<ViewPager>(R.id.viewPager)
                VigerPDF(this.requireContext(), message).initFromFile(object: OnResultListener {
                    override fun progressData(p0: Int) {
                        //Log.d("PDF -", "Progress!" )
                    }

                    override fun failed(p0: Throwable?) {
                         //Log.d("PDF -", "Error!" )
                    }

                    override fun resultData(p0: Bitmap) {
                        val list = ArrayList<Bitmap>()
                        list.add(p0)
                        val adapter = VigerAdapter(this@RegistrationFragment.requireContext(), list)
                        pdfView.setAdapter(adapter)
                        Log.d("PDF -", "Good!" )


                    }

                })*/

                val builder = AlertDialog.Builder(context)
                builder.setTitle(titleTextRes)
                    .setView(resultView)
                    .setCancelable(false)
                    .setPositiveButton(buttonOKTextRes) { dialog, _ -> dialog.dismiss()
                        if (!btSwitcherLicense.isChecked) btSwitcherLicense.isChecked = true
                        if (licenseClick) licenseClick = false
                    }
                    .setNegativeButton(buttonCancelTextRes) { dialog, _ -> dialog.cancel()
                        btSwitcherLicense.isChecked = false
                    }
                val alert: AlertDialog = builder.create()
                alert.show()

            } else {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(titleTextRes)
                    .setMessage(message)
                    .setCancelable(false)
                    .setNegativeButton(buttonOKTextRes) { dialog, _ -> dialog.cancel() }
                val alert: AlertDialog = builder.create()
                alert.show()
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when(v){
            is TextInputEditText ->
                if (!hasFocus) {
                    context?.let {
                        if (!viewModel.checkIsEmailFormatView(it, v, false)) {
                            v.setTextColor(ContextCompat.getColor(it, R.color.wrongText))
                        }
                    }
                }
                else {
                    v.setTextColor(color)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resultSignUpSubscriber?.let {  if (!it.isDisposed) it.dispose()}
        getPolicySubscriber?.let {  if (!it.isDisposed) it.dispose()}
    }
}
