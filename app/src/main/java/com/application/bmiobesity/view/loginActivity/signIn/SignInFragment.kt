package com.application.bmiobesity.view.loginActivity.signIn

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LoginSigninFragmentBinding
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.model.retrofit.RetrofitResult
import com.application.bmiobesity.model.retrofit.SendGoogleTokenId
import com.application.bmiobesity.services.google.signIn.GoogleSignInContract
import com.application.bmiobesity.utils.EventObserver
import com.application.bmiobesity.view.mainActivity.MainActivity
import com.application.bmiobesity.viewModels.LoginViewModel
import com.application.bmiobesity.viewModels.eventManager.EventManager
import com.application.bmiobesity.viewModels.eventManager.SignInFragmentEvent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.launch

class SignInFragment : Fragment(R.layout.login_signin_fragment) {

    private var signInBinding : LoginSigninFragmentBinding? = null

    private lateinit var allDisposable: CompositeDisposable
    private lateinit var formState: CheckFormStateSignIn
    private lateinit var finalFormStateSubj: Subject<Boolean>

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInLauncher: ActivityResultLauncher<GoogleSignInClient>

    private val loginModel: LoginViewModel by activityViewModels()
    private val eventManager: SignInFragmentEvent = EventManager.getEventManager()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signInBinding = LoginSigninFragmentBinding.bind(view)
        init()
        addRX()
        addListeners()
        loginModel.checkSavedPass()
        initGoogleService()
    }

    override fun onStart() {
        super.onStart()
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireContext())
    }

    private fun initGoogleService(){
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.PROFILE))
            .requestServerAuthCode(getString(R.string.server_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInLauncher = registerForActivityResult(GoogleSignInContract()){
            googleSignInCallBack(it)
        }
    }
    private fun googleSignIn(){
        //mGoogleSignInLauncher.launch(mGoogleSignInClient)
    }
    private fun googleSignInCallBack(completedTask: Task<GoogleSignInAccount>){
        try {
            val account = completedTask.result
            val code = account?.serverAuthCode

            /*lifecycleScope.launch {
                when (val result = loginModel.remoteRepo.getTokenFromGoogle(SendGoogleTokenId(code = code))){
                    is RetrofitResult.Success -> {
                        val test = 0
                    }
                    is RetrofitResult.Error -> {
                        val test1 = 1
                    }
                }
            }*/

        } catch (e: ApiException){

        }
    }

    private fun init(){
        loginModel.updateAppPreference()
        allDisposable = CompositeDisposable()
        formState = CheckFormStateSignIn()
        finalFormStateSubj = PublishSubject.create()
        val forgotText = SpannableString(getString(R.string.button_dont_remember_password))
        forgotText.setSpan(UnderlineSpan(), 0, forgotText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signInBinding?.signInTextViewForgotPass?.text = forgotText
    }

    private fun addRX(){
        val mailDisposable = signInBinding?.signInTextInputEditTextMail?.textChanges()
            ?.skipInitialValue()
            ?.map {
                return@map loginModel.checkEmail(it)
            }?.subscribe {
                formState.email = it
                formState.finally = (formState.email && formState.pass)
                if (it) setErrorMailField(null) else setErrorMailField(getString(R.string.error_form_fields_incorrect_email))
                finalFormStateSubj.onNext(formState.finally)
            }

        val passDisposable = signInBinding?.signInTextInputEditTextPass?.textChanges()
            ?.skipInitialValue()
            ?.map {
                return@map loginModel.checkPassNotEmpty(it)
            }?.subscribe {
                formState.pass = it
                formState.finally = (formState.email && formState.pass)
                if (it) setErrorPassField(null) else setErrorPassField(getString(R.string.error_form_fields_empty_password))
                finalFormStateSubj.onNext(formState.finally)
            }

        val finalStateDisposable = finalFormStateSubj.subscribe { signInBinding?.signInButtonSignIn?.isEnabled = it }

        allDisposable.addAll(mailDisposable, passDisposable, finalStateDisposable)
    }

    private fun addListeners(){
        signInBinding?.signInButtonSignIn?.clicks()?.subscribe { signInAction() }
        signInBinding?.signInButtonSignUp?.clicks()?.subscribe { signUpAction() }
        signInBinding?.signInTextViewForgotPass?.clicks()?.subscribe { forgotAction() }
        signInBinding?.signInButtonGoogleSignIn?.clicks()?.subscribe { googleSignIn() }

        eventManager.getSignInSuccessEvent().observe(viewLifecycleOwner, EventObserver{
            if (it) startMainActivity()
        })
        eventManager.getSignInRestorePassEvent().observe(viewLifecycleOwner, EventObserver{ user ->
            signInBinding?.signInTextInputEditTextMail?.setText(user.email)
            signInBinding?.signInTextInputEditTextPass?.setText(user.password)
            signInBinding?.signInSwitchRememberPassword?.isChecked = true
        })
        eventManager.getSignInShowErrorMessageEvent().observe(viewLifecycleOwner, EventObserver{
            setEnabledInterface(true)
            showErrorDialog(it)
        })
    }

    private fun signInAction(){
        setEnabledInterface(false)

        val mail = signInBinding?.signInTextInputEditTextMail?.text.toString()
        val pass = signInBinding?.signInTextInputEditTextPass?.text.toString()
        val rememberPass = signInBinding?.signInSwitchRememberPassword?.isChecked ?: false

        loginModel.signInAction(mail, pass, rememberPass)
    }
    private fun signUpAction() = findNavController().navigate(R.id.loginNavSignInToSignUp)
    private fun forgotAction() = findNavController().navigate(R.id.loginNavSignInToForgotPass)

    private fun setErrorMailField(error: String?){
        signInBinding?.signInTextInputLayoutMail?.error = error
    }
    private fun setErrorPassField(error: String?){
        signInBinding?.signInTextInputLayoutPass?.error = error
    }
    private fun setEnabledInterface(value: Boolean){
        signInBinding?.signInTextInputLayoutPass?.isEnabled = value
        signInBinding?.signInTextInputLayoutMail?.isEnabled = value
        signInBinding?.signInSwitchRememberPassword?.isEnabled = value
        signInBinding?.signInButtonSignIn?.isEnabled = value
        signInBinding?.signInButtonSignUp?.isEnabled = value
        signInBinding?.signInTextViewForgotPass?.isClickable = value
        if (value) signInBinding?.signInProgressBar?.visibility = View.GONE
        else signInBinding?.signInProgressBar?.visibility = View.VISIBLE
    }
    private fun startMainActivity(){
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
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

    override fun onDestroyView() {
        signInBinding = null
        super.onDestroyView()
    }
    override fun onDestroy() {
        if (!allDisposable.isDisposed) allDisposable.dispose()
        super.onDestroy()
    }

    data class CheckFormStateSignIn(
            var email: Boolean = false,
            var pass: Boolean = false,
            var finally: Boolean = false
    )
}