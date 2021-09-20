package com.application.bmiobesity.view.loginActivity.signUp

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.View
import android.webkit.WebView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.common.EventObserver
import com.application.bmiobesity.common.eventManager.EventManager
import com.application.bmiobesity.common.eventManager.SignUpFragmentEvent
import com.application.bmiobesity.databinding.LoginSignupFragmentBinding
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.viewModels.LoginViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.focusChanges
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.*

class SignUpFragment : Fragment(R.layout.login_signup_fragment) {

    private var signUpBinding: LoginSignupFragmentBinding? = null
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

    private fun init() {
        loginModel.updateAppPreference()
        allDisposable = CompositeDisposable()
        checkFormState = CheckFormStateSignUp()
        finalStateSubject = PublishSubject.create()
        val switchPolicyText = SpannableString(getString(R.string.switch_sign_in_privacy_agree))
        switchPolicyText.setSpan(
            UnderlineSpan(),
            0,
            switchPolicyText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        signUpBinding?.signUpTextViewLicenceAccept?.text = switchPolicyText
    }

    private fun addRX() {
        val emailDisposable = signUpBinding?.signUpTextInputEditTextMail?.textChanges()
            ?.skipInitialValue()
            ?.map {
                checkFormState.checkMail = loginModel.checkEmail(it)
                if (checkFormState.checkMail) {
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

                val colorGreen = ResourcesCompat.getColor(resources, R.color.green, null)
                val colorRed = ResourcesCompat.getColor(resources, R.color.red_900, null)

                // Set color when password satisfies the conditions
                if (checkFormState.lengthState)
                    signUpBinding?.signUpTextViewPassHelpLength?.setTextColor(colorGreen)
                else signUpBinding?.signUpTextViewPassHelpLength?.setTextColor(colorRed)

                if (checkFormState.numState)
                    signUpBinding?.signUpTextViewPassHelpNumber?.setTextColor(colorGreen)
                else signUpBinding?.signUpTextViewPassHelpNumber?.setTextColor(colorRed)

                if (checkFormState.symbolState)
                    signUpBinding?.signUpTextViewPassHelpSymbol?.setTextColor(colorGreen)
                else signUpBinding?.signUpTextViewPassHelpSymbol?.setTextColor(colorRed)

                return@map (checkFormState.lengthState && checkFormState.numState && checkFormState.symbolState)
            }?.subscribe {
                if (it) setPassError(null) else setPassError(getString(R.string.error_form_fields_incorrect_password))
                finalStateSubject.onNext(checkFormState.finalState)
            }

        val passFocusDisposable = signUpBinding?.signUpTextInputEditTextPass?.focusChanges()
            ?.subscribe {
                if (it)
                    signUpBinding?.signUpHelpPassGroup?.visibility = View.VISIBLE
                else signUpBinding?.signUpHelpPassGroup?.visibility = View.GONE
            }

        val passConfirmDisposable = signUpBinding?.signUpTextInputEditTextPassConfirm?.textChanges()
            ?.skipInitialValue()
            ?.map {
                checkFormState.confirmState = loginModel.checkPassConfirm(
                    signUpBinding?.signUpTextInputEditTextPass?.text.toString(),
                    it.toString()
                )

                val colorGreen = ResourcesCompat.getColor(resources, R.color.green, null)
                val colorRed = ResourcesCompat.getColor(resources, R.color.red_900, null)

                if (checkFormState.confirmState)
                    signUpBinding?.signUpTextViewPassHelpConfirm?.setTextColor(colorGreen)
                else signUpBinding?.signUpTextViewPassHelpConfirm?.setTextColor(colorRed)

                return@map checkFormState.confirmState
            }?.subscribe {
                if (it) setConfirmPassError(null) else setConfirmPassError(getString(R.string.error_form_fields_different_password))
                finalStateSubject.onNext(checkFormState.finalState)
            }

        val passConfirmFocusDisposable =
            signUpBinding?.signUpTextInputEditTextPassConfirm?.focusChanges()
                ?.subscribe {
                    if (it)
                        signUpBinding?.signUpTextViewPassHelpConfirm?.visibility = View.VISIBLE
                    else signUpBinding?.signUpTextViewPassHelpConfirm?.visibility = View.GONE
                }

        setEnabledSignUpButton(false)
        val finalStateDisposable = finalStateSubject.subscribe {
            setEnabledSignUpButton(it)
        }

        signUpBinding?.signUpSwitchLicense?.setOnCheckedChangeListener { _, isChecked ->
            checkFormState.checkPolicySwitch = isChecked
            finalStateSubject.onNext(checkFormState.finalState)
        }

        allDisposable.addAll(
            emailDisposable,
            passDisposable,
            passFocusDisposable,
            passConfirmDisposable,
            passConfirmFocusDisposable,
            finalStateDisposable
        )
    }

    private fun addListeners() {
        signUpBinding?.signUpTextViewSignIn?.clicks()?.subscribe { findNavController().popBackStack() }
        signUpBinding?.signUpButtonSignUp?.clicks()?.subscribe { signUpAction() }
        signUpBinding?.signUpTextViewLicenceAccept?.clicks()?.subscribe { policyAction() }

        eventManager.getSignUpCheckMailExistEvent().observe(viewLifecycleOwner, EventObserver {
            if (it) setMailError(null) else setMailError(getString(R.string.error_form_fields_email_exist))
            checkFormState.mailExist = it
            finalStateSubject.onNext(checkFormState.finalState)
        })

        eventManager.getSignUpCheckMailErrorEvent().observe(viewLifecycleOwner, EventObserver {
            signUpBinding?.signUpTextInputEditTextMail?.setText("")
            showErrorMessage(RetrofitError.UNKNOWN_ERROR)
        })

        eventManager.getSignUpSuccessEvent().observe(viewLifecycleOwner, EventObserver {
            if (it) signUpSuccessAction()
        })

        eventManager.getSignUpErrorEvent().observe(viewLifecycleOwner, EventObserver {
            setEnabledInterface(true)
            showErrorMessage(it)
        })
    }

    private fun signUpAction() {
        setEnabledInterface(false)
        setEnabledSignUpButton(false)

        val mail = signUpBinding?.signUpTextInputEditTextMail?.text.toString()
        val pass = signUpBinding?.signUpTextInputEditTextPass?.text.toString()

        loginModel.signUpAction(mail, pass)
    }
    private fun policyAction() {
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
    private fun signUpSuccessAction() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.sign_up_success))
            .setPositiveButton(getString(R.string.button_accept)) { _, _ ->
                requireActivity().onBackPressed()
            }
            .show()
    }

    private fun setMailError(error: String?) {
        signUpBinding?.signUpTextInputLayoutMail?.error = error
        if (error == null) {
            signUpBinding?.signUpTextInputLayoutMail?.hint = resources.getString(R.string.login_hint_email)
        } else {
            signUpBinding?.signUpTextInputLayoutMail?.hint = error
        }
    }
    private fun setPassError(error: String?) {
        signUpBinding?.signUpTextInputLayoutPass?.error = error
        if (error == null) {
            signUpBinding?.signUpTextInputLayoutPass?.hint = resources.getString(R.string.login_hint_password)
        } else {
            signUpBinding?.signUpTextInputLayoutPass?.hint = error
        }
    }
    private fun setConfirmPassError(error: String?) {
        signUpBinding?.signUpTextInputLayoutPassConfirm?.error = error
        if (error == null) {
            signUpBinding?.signUpTextInputLayoutPassConfirm?.hint = resources.getString(R.string.login_hint_confirm_password)
        } else {
            signUpBinding?.signUpTextInputLayoutPassConfirm?.hint = error
        }
    }

    private fun setEnabledSignUpButton(isEnabled: Boolean) {
        signUpBinding?.signUpButtonSignUp?.isEnabled = isEnabled
        if (isEnabled) {
            signUpBinding?.signUpButtonSignUp?.setTextColor(
                resources.getColor(
                    R.color.color_white,
                    null
                )
            )
            signUpBinding?.signUpButtonSignUp?.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.all_round_blue,
                null
            )
        } else {
            signUpBinding?.signUpButtonSignUp?.setTextColor(
                resources.getColor(
                    R.color.colorPrimary,
                    null
                )
            )
            signUpBinding?.signUpButtonSignUp?.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.all_round_gray,
                null
            )
        }
    }
    private fun setEnabledInterface(value: Boolean) {
        signUpBinding?.signUpTextInputLayoutMail?.isEnabled = value
        signUpBinding?.signUpTextInputLayoutPass?.isEnabled = value
        signUpBinding?.signUpTextInputLayoutPassConfirm?.isEnabled = value
        signUpBinding?.signUpSwitchLicense?.isEnabled = value
        signUpBinding?.signUpTextViewLicenceAccept?.isClickable = value
        signUpBinding?.signUpTextViewSignIn?.isClickable = value
        if (value) {
            signUpBinding?.signUpProgress?.visibility = View.GONE
        } else {
            signUpBinding?.signUpProgress?.visibility = View.VISIBLE
            signUpBinding?.signUpHelpPassGroup?.visibility = View.GONE
            signUpBinding?.signUpTextViewPassHelpConfirm?.visibility = View.GONE
        }
    }

    private fun showErrorMessage(errorType: RetrofitError) {
        val text = when (errorType) {
            RetrofitError.MAIL_NOT_FOUND -> getString(R.string.error_not_found)
            RetrofitError.PASS_INCORRECT -> getString(R.string.error_unauthorized)
            RetrofitError.NO_INTERNET_CONNECTION -> getString(R.string.error_connection)
            else -> getString(R.string.error_common)
        }
        signUpBinding?.root?.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
    }

    override fun onDestroyView() {
        signUpBinding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        allDisposable.let {
            if (!allDisposable.isDisposed) allDisposable.dispose()
        }
        super.onDestroy()
    }

    class CheckFormStateSignUp {
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

        private fun setFinal() {
            finalState =
                lengthState && numState && symbolState && confirmState && checkMail && checkPolicySwitch && mailExist
        }
    }
}