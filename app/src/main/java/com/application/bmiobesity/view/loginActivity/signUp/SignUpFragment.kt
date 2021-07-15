package com.application.bmiobesity.view.loginActivity.signUp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LoginSignupFragmentBinding
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.common.EventObserver
import com.application.bmiobesity.view.mainActivity.MainActivity
import com.application.bmiobesity.viewModels.LoginViewModel
import com.application.bmiobesity.common.eventManager.EventManager
import com.application.bmiobesity.common.eventManager.SignUpFragmentEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.focusChanges
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.*

class SignUpFragment : Fragment(R.layout.login_signup_fragment) {

    private var signUpBinding : LoginSignupFragmentBinding? = null
    private val loginModel: LoginViewModel by activityViewModels()
    private val eventManager: SignUpFragmentEvent = EventManager.getEventManager()

    private lateinit var checkFormState: CheckFormStateSignUp
    private lateinit var allDisposable: CompositeDisposable
    private lateinit var finalStateSubject: Subject<Boolean>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpBinding = LoginSignupFragmentBinding.bind(view)
        init()
        addRX()
        addListeners()
    }

    private fun init(){
        loginModel.updateAppPreference()
        allDisposable = CompositeDisposable()
        checkFormState = CheckFormStateSignUp()
        finalStateSubject = PublishSubject.create()
        val switchPolicyText = SpannableString(getString(R.string.switch_sign_in_privacy_agree))
        switchPolicyText.setSpan(UnderlineSpan(), 0, switchPolicyText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signUpBinding?.signUpTextViewLicenceAccept?.text = switchPolicyText
    }

    private fun addRX(){
        val emailDisposable = signUpBinding?.signUpTextInputEditTextMail?.textChanges()
            ?.skipInitialValue()
            ?.map {
                checkFormState.checkMail = loginModel.checkEmail(it)

                if (checkFormState.checkMail){
                    loginModel.checkExistMail(it.toString())
                }

                return@map checkFormState.checkMail
            }?.subscribe {
                if (it) setMailError(null) else setMailError(getString(R.string.error_form_fields_incorrect_email))
                finalStateSubject.onNext(checkFormState.finalState)
            }

        val passDisposable = signUpBinding?.signUpTextInputEditTextPass?.textChanges()
            ?.skipInitialValue()
            ?.map {
                checkFormState.lengthState = loginModel.checkPassLength(it)
                checkFormState.numState = loginModel.checkPassNumber(it)
                checkFormState.symbolState = loginModel.checkPassSymbol(it)

                if (checkFormState.lengthState) signUpBinding?.signUpTextViewPassHelpLength?.setTextColor(Color.GREEN) else signUpBinding?.signUpTextViewPassHelpLength?.setTextColor(Color.RED)
                if (checkFormState.numState) signUpBinding?.signUpTextViewPassHelpNumber?.setTextColor(Color.GREEN) else signUpBinding?.signUpTextViewPassHelpNumber?.setTextColor(Color.RED)
                if (checkFormState.symbolState) signUpBinding?.signUpTextViewPassHelpSymbol?.setTextColor(Color.GREEN) else signUpBinding?.signUpTextViewPassHelpSymbol?.setTextColor(Color.RED)

                return@map (checkFormState.lengthState && checkFormState.numState && checkFormState.symbolState)
            }?.subscribe {
                if (it) setPassError(null) else setPassError(getString(R.string.error_form_fields_incorrect_password))
                finalStateSubject.onNext(checkFormState.finalState)

                val confirmPass = signUpBinding?.signUpTextInputEditTextPassConfirm?.text.toString()
                if (confirmPass.isNotEmpty()) signUpBinding?.signUpTextInputEditTextPassConfirm?.setText(confirmPass)
            }
        val passFocusDisposable = signUpBinding?.signUpTextInputEditTextPass?.focusChanges()
            ?.subscribe { if (it) signUpBinding?.signUpHelpPassGroup?.visibility = View.VISIBLE else  signUpBinding?.signUpHelpPassGroup?.visibility = View.GONE}

        val passConfirmDisposable = signUpBinding?.signUpTextInputEditTextPassConfirm?.textChanges()
            ?.skipInitialValue()
            ?.map {
                checkFormState.confirmState = loginModel.checkPassConfirm(signUpBinding?.signUpTextInputEditTextPass?.text.toString(), it.toString())
                if (checkFormState.confirmState) signUpBinding?.signUpTextViewPassHelpConfirm?.setTextColor(Color.GREEN) else signUpBinding?.signUpTextViewPassHelpConfirm?.setTextColor(Color.RED)
                return@map checkFormState.confirmState
            }?.subscribe {
                if (it) setConfirmPassError(null) else setConfirmPassError(getString(R.string.error_form_fields_different_password))
                finalStateSubject.onNext(checkFormState.finalState)
            }
        val passConfirmFocusDisposable = signUpBinding?.signUpTextInputEditTextPassConfirm?.focusChanges()
                ?.subscribe { if (it) signUpBinding?.signUpTextViewPassHelpConfirm?.visibility = View.VISIBLE else  signUpBinding?.signUpTextViewPassHelpConfirm?.visibility = View.GONE}

        val finalStateDisposable = finalStateSubject.subscribe { signUpBinding?.signUpButtonSignUp?.isEnabled = it }

        signUpBinding?.signUpSwitchLicense?.setOnCheckedChangeListener { _, isChecked ->
            checkFormState.checkPolicySwitch = isChecked
            finalStateSubject.onNext(checkFormState.finalState)
        }

        allDisposable.addAll(emailDisposable, passDisposable, passFocusDisposable, passConfirmDisposable, passConfirmFocusDisposable, finalStateDisposable)
    }

    private fun addListeners(){
        signUpBinding?.signUpButtonSignUp?.clicks()?.subscribe { signUpAction() }
        signUpBinding?.signUpTextViewLicenceAccept?.clicks()?.subscribe { policyAction() }

        eventManager.getSignUpCheckMailExistEvent().observe(viewLifecycleOwner, EventObserver{
            if (it) setMailError(null) else setMailError(getString(R.string.error_form_fields_email_exist))
            checkFormState.mailExist = it
            finalStateSubject.onNext(checkFormState.finalState)
        })

        eventManager.getSignUpCheckMailErrorEvent().observe(viewLifecycleOwner, EventObserver{
            signUpBinding?.signUpTextInputEditTextMail?.setText("")
            showErrorDialog(RetrofitError.UNKNOWN_ERROR)
        })

        eventManager.getSignUpSuccessEvent().observe(viewLifecycleOwner, EventObserver{
            if (it) signUpSuccessAction()
        })

        eventManager.getSignUpErrorEvent().observe(viewLifecycleOwner, EventObserver{
            setEnabledInterface(true)
            showErrorDialog(it)
        })
    }

    private fun signUpAction(){
        setEnabledInterface(false)

        val mail = signUpBinding?.signUpTextInputEditTextMail?.text.toString()
        val pass = signUpBinding?.signUpTextInputEditTextPass?.text.toString()

        loginModel.signUpAction(mail, pass)
    }
    private fun policyAction(){
        val policyView = WebView(requireContext())
        policyView.loadData(loginModel.getPolicy(), null, null)

        MaterialAlertDialogBuilder(requireContext())
                .setView(policyView)
                .setNegativeButton(getString(R.string.button_i_disagree)) { _, _ ->
                    signUpBinding?.signUpSwitchLicense?.isChecked = false
                }
                .setPositiveButton(getString(R.string.button_i_agree)) { _, _ ->
                    signUpBinding?.signUpSwitchLicense?.isChecked = true
                }
                .show()
    }
    private fun signUpSuccessAction(){
        MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.sign_up_success))
                .setPositiveButton(getString(R.string.button_accept)) { _, _ ->
                    findNavController().navigate(R.id.loginNavSignUpToSignIn)
                }
                .show()
    }

    private fun setMailError(error: String?){
        signUpBinding?.signUpTextInputLayoutMail?.error = error
    }
    private fun setPassError(error: String?){
        signUpBinding?.signUpTextInputLayoutPass?.error = error
    }
    private fun setConfirmPassError(error: String?){
        signUpBinding?.signUpTextInputLayoutPassConfirm?.error = error
    }
    private fun setEnabledInterface(value: Boolean){
        signUpBinding?.signUpTextInputLayoutMail?.isEnabled = value
        signUpBinding?.signUpTextInputLayoutPass?.isEnabled = value
        signUpBinding?.signUpTextInputLayoutPassConfirm?.isEnabled = value
        signUpBinding?.signUpSwitchLicense?.isEnabled = value
        signUpBinding?.signUpTextViewLicenceAccept?.isClickable = value
        signUpBinding?.signUpButtonSignUp?.isEnabled = value
        if (value) {
            signUpBinding?.signUpProgress?.visibility = View.GONE
        }
        else{
            signUpBinding?.signUpProgress?.visibility = View.VISIBLE
            signUpBinding?.signUpHelpPassGroup?.visibility = View.GONE
            signUpBinding?.signUpTextViewPassHelpConfirm?.visibility = View.GONE
        }
    }
    private fun showErrorDialog(errorType: RetrofitError){
        val text = when(errorType){
            RetrofitError.MAIL_NOT_FOUND -> getString(R.string.error_not_found)
            RetrofitError.PASS_INCORRECT -> getString(R.string.error_unauthorized)
            RetrofitError.NO_INTERNET_CONNECTION -> getString(R.string.error_connection)
            else -> getString(R.string.error_common)
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
    private fun startMainActivity(){
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        signUpBinding = null
        super.onDestroyView()
    }
    override fun onDestroy() {
        allDisposable?.let {
            if (!allDisposable.isDisposed) allDisposable.dispose()
        }
        super.onDestroy()
    }

    class CheckFormStateSignUp(){
        var lengthState: Boolean = false
            set(value) {
                field = value
                setFinal()
            }
        var numState: Boolean = false
            set(value) {
                field = value
                setFinal()
            }
        var symbolState: Boolean = false
            set(value) {
                field = value
                setFinal()
            }
        var confirmState: Boolean = false
            set(value) {
                field = value
                setFinal()
            }
        var checkMail: Boolean = false
            set(value) {
                field = value
                setFinal()
            }
        var checkPolicySwitch: Boolean = false
            set(value) {
                field = value
                setFinal()
            }
        var mailExist: Boolean = false
            set(value) {
                field = value
                setFinal()
            }

        var finalState: Boolean = false
        private fun setFinal(){
            finalState = lengthState && numState && symbolState && confirmState && checkMail && checkPolicySwitch && mailExist
        }
    }
}