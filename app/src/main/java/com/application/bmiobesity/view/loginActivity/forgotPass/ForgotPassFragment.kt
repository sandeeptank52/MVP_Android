package com.application.bmiobesity.view.loginActivity.forgotPass

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LoginForgotpassFragmentBinding
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.model.retrofit.RetrofitResult
import com.application.bmiobesity.model.retrofit.SendEmail
import com.application.bmiobesity.common.EventObserver
import com.application.bmiobesity.viewModels.LoginViewModel
import com.application.bmiobesity.common.eventManager.EventManager
import com.application.bmiobesity.common.eventManager.ForgotPassFragmentEvent
import com.application.bmiobesity.databinding.LoginForgotpassFragmentV2Binding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.launch

class ForgotPassFragment : Fragment(R.layout.login_forgotpass_fragment_v2) {

    private var forgotPassBinding : LoginForgotpassFragmentV2Binding? = null

    private lateinit var allDisposable: CompositeDisposable
    private var stateMailField: Boolean = false
    private lateinit var stateMailFieldSubject: Subject<Boolean>
    private val loginModel: LoginViewModel by activityViewModels()
    private val eventManager: ForgotPassFragmentEvent = EventManager.getEventManager()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forgotPassBinding = LoginForgotpassFragmentV2Binding.bind(view)
        init()
        addRx()
        addListeners()
    }

    private fun init(){
        allDisposable = CompositeDisposable()
        stateMailFieldSubject = PublishSubject.create()
    }

    private fun addRx(){
        val mailDisposable = forgotPassBinding?.forgotPassTextInputEditTextMail?.textChanges()
            ?.skipInitialValue()
            ?.map {
                val mailCheck = loginModel.checkEmail(it)

                if (mailCheck){
                    setErrorMailField(null)
                    setVisibleProgressBar(true)
                    stateMailFieldSubject.onNext(false)
                    lifecycleScope.launch {
                        when (val existMail = loginModel.remoteRepo.isMailExist(SendEmail(it.toString()))){
                            is RetrofitResult.Success -> {
                                stateMailField = existMail.value.exist ?: false
                                if (stateMailField) setErrorMailField(null) else setErrorMailField(getString(R.string.error_form_field_email_not_registered))
                                stateMailFieldSubject.onNext(stateMailField)
                            }
                            is RetrofitResult.Error -> {
                                stateMailField = false
                                stateMailFieldSubject.onNext(stateMailField)
                            }
                        }
                        setVisibleProgressBar(false)
                    }
                } else {
                    setErrorMailField(getString(R.string.error_form_fields_incorrect_email))
                    stateMailField = false
                    stateMailFieldSubject.onNext(stateMailField)
                }
            }?.subscribe()

        val mailCheckDisposable = stateMailFieldSubject.subscribe { forgotPassBinding?.forgotPassButtonSend?.isEnabled = it }

        allDisposable.addAll(mailDisposable, mailCheckDisposable)
    }

    private fun addListeners(){
        forgotPassBinding?.forgotPassButtonSend?.clicks()?.subscribe { sendAction() }

        eventManager.getForgotPassSuccessEvent().observe(viewLifecycleOwner, EventObserver{
            if (it) {
                findNavController().popBackStack()
                showDialog(getString(R.string.forgot_pass_sending_info))
            }
        })
        eventManager.getForgotPassErrorEvent().observe(viewLifecycleOwner, EventObserver{
            setEnableInterface(true)
            showErrorDialog(it)
        })
    }

    private fun sendAction(){
        setEnableInterface(false)
        val mail = forgotPassBinding?.forgotPassTextInputEditTextMail?.text.toString()
        loginModel.forgotPassAction(mail)
    }

    private fun setErrorMailField(error: String?){
        forgotPassBinding?.forgotPassTextInputLayoutMail?.error = error
    }
    private fun setVisibleProgressBar(isVisible: Boolean){
        if (isVisible) forgotPassBinding?.forgotPassProgressBar?.visibility = View.VISIBLE else forgotPassBinding?.forgotPassProgressBar?.visibility = View.GONE
    }
    private fun setEnableInterface(enable: Boolean){
        forgotPassBinding?.forgotPassTextInputLayoutMail?.isEnabled = enable
        forgotPassBinding?.forgotPassButtonSend?.isEnabled = enable
        if (enable) forgotPassBinding?.forgotPassProgressBar?.visibility = View.GONE else forgotPassBinding?.forgotPassProgressBar?.visibility = View.VISIBLE
    }
    private fun showDialog(message: String){
        MaterialAlertDialogBuilder(requireContext())
                .setMessage(message)
                .setPositiveButton(getString(R.string.button_accept), null)
                .show()
    }
    private fun showErrorDialog(errorType: RetrofitError){
        val text = when(errorType){
            RetrofitError.MAIL_NOT_FOUND -> getString(R.string.error_not_found)
            RetrofitError.NO_INTERNET_CONNECTION -> getString(R.string.error_connection)
            else -> getString(R.string.error_common)
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        forgotPassBinding = null
        super.onDestroyView()
    }
    override fun onDestroy() {
        allDisposable?.let {
            if (!allDisposable.isDisposed) allDisposable.dispose()
        }
        super.onDestroy()
    }
}