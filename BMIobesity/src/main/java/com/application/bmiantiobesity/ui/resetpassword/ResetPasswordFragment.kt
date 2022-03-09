package com.application.bmiantiobesity.ui.resetpassword


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.application.bmiantiobesity.retrofit.ConfirmReset
import com.application.bmiantiobesity.ui.login.LoginActivity

import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

// Функция расшения для запуска Activity
inline fun <reified T> startLoginActivity(context: Context) {
    val loginActivity = Intent(context.applicationContext, T::class.java)
    context.startActivity(loginActivity)
}

/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordFragment : Fragment() {

    private var resultEmailSubscriber: Disposable? = null

    private val viewModel by viewModels<LoginViewModel>()

    private var uid = ""
    private var token = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.reset_password_fragment, container, false)

        // Получение аргументов
        LoginViewModel.liveResetPasswordString.observe( this.viewLifecycleOwner, Observer<String>{
            // Получение значений из ссылки
            val str = it.split('/')
            val length = str.size
            if(length >= 4)
            {
                token = str[length-2]
                uid = str[length-3]
            }
        })

        val textPass1 = view.findViewById<TextInputEditText>(R.id.reset_input_password)
        val textPass2 = view.findViewById<TextInputEditText>(R.id.reset_input_password_rewrite)
        val btSwitcher = view.findViewById<SwitchMaterial>(R.id.save_password_switch)
        btSwitcher.isChecked = true

        val btReset = view.findViewById<Button>(R.id.reset_button)
        btReset.setOnClickListener {

            context?.let {itContext ->
                if (!viewModel.checkIsPasswordView(itContext, textPass1)) return@setOnClickListener

                if (!viewModel.checkIsEqualsPasswordsView(itContext, textPass1, textPass2)) return@setOnClickListener
            }

            resultEmailSubscriber = viewModel.passwordResetConfirm(
                ConfirmReset(
                    textPass1.text.toString(),
                    textPass2.text.toString(),
                    uid,
                    token
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->

                    // Сохраняем Значения
                    val sharedPreferences =  context?.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
                    if (btSwitcher.isChecked) sharedPreferences?.edit {  putString(LoginViewModel.USER_PASSWORD, viewModel.cryptoApi.encryptString(textPass1.text.toString())) }
                    else sharedPreferences?.edit {  putString(LoginViewModel.USER_PASSWORD, "") }

                    // Вывод сообщения и переход на логин активити
                    showInfoDialog(R.string.finish_reset_password, "${context?.getText(R.string.finish_reset_password_ok)}", R.string.button_understand)
                },
                    {error ->  Log.d("ResetPassword Error -", error?.message ?: "")
                        Toast.makeText(context, getText(R.string.error_reset_password), Toast.LENGTH_SHORT).show()}
                )
        }

        return view
    }

    private fun showInfoDialog(titleTextRes:Int, message: String,  buttonOKTextRes:Int) {
        context?.let { it ->
            val builder = AlertDialog.Builder(it)
            builder.setTitle(titleTextRes)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(buttonOKTextRes) { dialog, _ -> dialog.cancel()
                    // Закрытие Фрагмента и запуска Логин Активити
                    startLoginActivity<LoginActivity>(context!!)
                }
            val alert: AlertDialog = builder.create()
            alert.show()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resultEmailSubscriber?.let {  if (!it.isDisposed) it.dispose()}
    }
}