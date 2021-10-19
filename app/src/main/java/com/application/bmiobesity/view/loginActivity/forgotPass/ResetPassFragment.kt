package com.application.bmiobesity.view.loginActivity.forgotPass

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.common.EventObserver
import com.application.bmiobesity.common.eventManager.EventManager
import com.application.bmiobesity.common.eventManager.ResetPassFragmentEvent
import com.application.bmiobesity.databinding.LoginResetpassFragmentBinding
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.model.retrofit.SendConfirmResetPass
import com.application.bmiobesity.viewModels.LoginViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

class ResetPassFragment : BaseFragment(R.layout.login_resetpass_fragment) {

    private var resetPassBinding: LoginResetpassFragmentBinding? = null

    private val loginModel: LoginViewModel by activityViewModels()
    private lateinit var allDisposable: CompositeDisposable
    private lateinit var passState: CheckResetPassFormState
    private lateinit var finalStateSubj: Subject<Boolean>
    private val eventManager: ResetPassFragmentEvent = EventManager.getEventManager()

    private var token = ""
    private var uid = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetPassBinding = LoginResetpassFragmentBinding.bind(view)

        val action = requireActivity().intent.action
        val data = requireActivity().intent.data

        if ((action == Intent.ACTION_VIEW) && (data != null)) {
            val pathSegments = data.pathSegments
            token = pathSegments[pathSegments.lastIndex]
            uid = pathSegments[(pathSegments.lastIndex) - 1]
        }

        init()
        addRx()
        addListeners()
    }

    private fun init() {
        allDisposable = CompositeDisposable()
        passState = CheckResetPassFormState()
        finalStateSubj = PublishSubject.create()
    }

    private fun addRx() {
        val checkPassDisposable = resetPassBinding?.resetPassTextInputEditTextPass?.textChanges()
            ?.skipInitialValue()
            ?.map {
                passState.checkLength = loginModel.checkPassLength(it)
                passState.checkNumber = loginModel.checkPassNumber(it)
                passState.checkSymbol = loginModel.checkPassSymbol(it)

                val colorGreen = ResourcesCompat.getColor(resources, R.color.green, null)
                val colorRed = ResourcesCompat.getColor(resources, R.color.red_900, null)

                if (passState.checkLength)
                    resetPassBinding?.resetPassTextViewHintLength?.setTextColor(colorGreen)
                else resetPassBinding?.resetPassTextViewHintLength?.setTextColor(colorRed)

                if (passState.checkNumber)
                    resetPassBinding?.resetPassTextViewHintNumber?.setTextColor(colorGreen)
                else resetPassBinding?.resetPassTextViewHintNumber?.setTextColor(colorRed)

                if (passState.checkSymbol)
                    resetPassBinding?.resetPassTextViewHintSymbol?.setTextColor(colorGreen)
                else resetPassBinding?.resetPassTextViewHintSymbol?.setTextColor(colorRed)

                return@map (passState.checkSymbol && passState.checkLength && passState.checkNumber)
            }?.subscribe {
                passState.finalState = it
                finalStateSubj.onNext(passState.finalState)
            }

        val finalStateDisposable = finalStateSubj.subscribe {
            resetPassBinding?.resetPassButtonReset?.isEnabled = it
        }

        allDisposable.addAll(checkPassDisposable, finalStateDisposable)
    }

    private fun addListeners() {
        resetPassBinding?.resetPassButtonReset?.clicks()?.subscribe { resetAction() }

        eventManager.getResetPassSuccessEvent().observe(viewLifecycleOwner, EventObserver {
            if (it) {
                findNavController().popBackStack()
                showDialog(getString(R.string.reset_pass_success))
            }
        })

        eventManager.getResetPassErrorEvent().observe(viewLifecycleOwner, EventObserver {
            setEnabledInterface(true)
            showErrorDialog(it)
        })
    }

    private fun resetAction() {
        setEnabledInterface(false)
        val pass = resetPassBinding?.resetPassTextInputEditTextPass?.text?.toString()
        if (token.isNotEmpty() && uid.isNotEmpty() && !pass.isNullOrEmpty()) {
            val sendConfirm = SendConfirmResetPass(pass, pass, uid, token)
            loginModel.resetPassAction(sendConfirm)
        } else {
            showErrorDialog(RetrofitError.UNKNOWN_ERROR)
        }
    }

    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setPositiveButton(getString(R.string.button_accept), null)
            .show()
    }
    private fun showErrorDialog(errorType: RetrofitError) {
        val text = when (errorType) {
            RetrofitError.MAIL_NOT_FOUND -> getString(R.string.error_not_found)
            RetrofitError.NO_INTERNET_CONNECTION -> getString(R.string.error_connection)
            else -> getString(R.string.error_common)
        }
        resetPassBinding?.root?.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
    }

    private fun setEnabledInterface(enable: Boolean) {
        resetPassBinding?.resetPassTextInputLayoutPass?.isEnabled = enable
        resetPassBinding?.resetPassButtonReset?.isEnabled = enable
        if (enable) {
            resetPassBinding?.resetPassGroupPassHint?.visibility = View.VISIBLE
            resetPassBinding?.resetPassProgress?.visibility = View.GONE
        } else {
            resetPassBinding?.resetPassGroupPassHint?.visibility = View.GONE
            resetPassBinding?.resetPassProgress?.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        resetPassBinding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        allDisposable.let {
            if (!allDisposable.isDisposed) allDisposable.dispose()
        }
        super.onDestroy()
    }

    data class CheckResetPassFormState(
        var checkLength: Boolean = false,
        var checkNumber: Boolean = false,
        var checkSymbol: Boolean = false,
        var finalState: Boolean = false
    )
}