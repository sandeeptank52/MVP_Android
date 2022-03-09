package com.application.bmiantiobesity.ui.login


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController

import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.retrofit.SendEmail
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.forgot_password_fragment.view.*

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : Fragment() {

    private var resultEmailSubscriber: Disposable? = null

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.forgot_password_fragment, container, false)

        setProgress(view, false)

        val textEmail = view.findViewById<TextInputEditText>(R.id.forgot_input_email)
        // Получение аргументов
        val email = ForgotPasswordFragmentArgs.fromBundle(requireArguments()).emailAddress
        if (email != "") textEmail.text?.insert(0, email)

        val btReset = view.findViewById<Button>(R.id.forgot_button)
        btReset.setOnClickListener {

            context?.let {itContext ->
                if (!viewModel.checkIsEmailFormatView(itContext, textEmail,true)) return@setOnClickListener
            }

            btReset.isEnabled = false
            setProgress(view, true)

            resultEmailSubscriber = viewModel.passwordReset(
                SendEmail(
                    textEmail.text.toString()
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setProgress(view, false)
                    btReset.isEnabled = true
                    // Закрытие Фрагмента
                    view.findNavController().popBackStack()
                    // Вывод сообщения
                    showInfoDialog(R.string.finish_reset_password, "${context?.getText(R.string.finish_reset_password_text)} ${textEmail.text.toString()}", R.string.button_understand)
                },
                    {error ->
                        btReset.isEnabled = true
                        setProgress(view, false)
                        Log.d("ResetPassword Error -", error?.message ?: "")
                        Toast.makeText(context, getText(R.string.error_reset_password), Toast.LENGTH_SHORT).show()}
                )

            //val url = "https://intime.digital/ru/account/password_reset"
            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        return  view
    }

    private fun setProgress(view: View, progress:Boolean) {
        view.forgot_password_progressBar.isVisible = progress
        view.forgot_password_progressBar.isIndeterminate = progress
    }

    private fun showInfoDialog(titleTextRes:Int, message: String,  buttonOKTextRes:Int) {
        context?.let { it ->
                val builder = AlertDialog.Builder(it)
                builder.setTitle(titleTextRes)
                    .setMessage(message)
                    .setCancelable(false)
                    .setNegativeButton(buttonOKTextRes) { dialog, _ -> dialog.cancel() }
                val alert: AlertDialog = builder.create()
                alert.show()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resultEmailSubscriber?.let {  if (!it.isDisposed) it.dispose()}
    }

}
