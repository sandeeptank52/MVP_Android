package com.application.bmiantiobesity.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.retrofit.ResultToken
import com.application.bmiantiobesity.retrofit.SendRefresh
import com.application.bmiantiobesity.ui.main.MainActivity

import com.application.bmiantiobesity.utilits.getDevice
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.label_fragment.view.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class LabelFragment : Fragment() {

    private val timeOut = 4L // Время задержки в секундах

    private var disposableTimer: Disposable? = null
    private var disposableRefresh: Disposable? = null

    private var firstLogin = true

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.label_fragment, container, false)

        LoginViewModel.liveUser.observe(this.requireActivity(), Observer {
            firstLogin = it.firstLogin
        })

        // Проверка Refresh и автоматический SingIn
        LoginViewModel.refresh.observe(this.requireActivity(), Observer {

            val launchTimeOut = Observable
                    .just("Launch")
                    .delay(timeOut, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())

            if (it.refresh.isNotEmpty() && (it.refresh != "-0-")) {

                val sendRefresh = SendRefresh(it, getDevice(requireContext()))

                // Проверка на сервере (+ объединение с таймаутом). сюда прилетает
                disposableRefresh = viewModel.refreshToken(sendRefresh)
                        .subscribeOn(Schedulers.io())
                        .onErrorResumeNext(Observable.just(ResultToken("", ""))) // в случае ошибки сервера не попадаем в onError досрочно (ожидание завершения показа логотипа)
                        .zipWith(launchTimeOut) { result, launch -> Pair(result, launch) }  // ожидание потоков
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ pair ->
                            // Auto login
                            Log.d("Response refresh -", pair.first.toString())
                            LoginViewModel.singleResultToken = pair.first
                            LoginViewModel.liveResultToken.value = pair.first

                            if (LoginViewModel.singleResultToken.access != "") {
                                // Сохранение refresh token
                                context?.let { itContext ->
                                    val sharedPreferences = itContext.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
                                    sharedPreferences?.edit { putString(LoginViewModel.REFRESH_TOKEN, pair.first.refresh) }
                                }

                                // Start Main Activity
                                startLogin(view, true)
                            } else
                                startLogin(view, false)
                        },
                                {
                                    startLogin(view, false)
                                    /*val errorMessage = parserError(error)
                                    Log.d("Response fail - ", errorMessage.toString())
                                    Toast.makeText(context, errorAnalyzer(context, errorMessage), Toast.LENGTH_SHORT).show()
                                    */
                                })
            } else {
                disposableTimer = launchTimeOut
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { startLogin(view, false) },
                                { error ->
                                    Log.d("Error Intro -", error.message ?: "")
                                    startLogin(view, false)
                                })
            }
        })

        // Анимация
        animateLogo(view.label_imageView, view.label_textView)

        return view
    }

    private fun startLogin(view: View, result: Boolean) {
        var isNotDisclaimer = false
        // Восстановление сохранённых значений
        val sharedPreferences = this.requireContext().getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences?.let { isNotDisclaimer = it.getBoolean(LoginViewModel.IS_NOT_DISCLAIMER, false) }
        if (isNotDisclaimer) {
            if (result) {

                // Автоматический вход
                LoginViewModel.liveResultToken.observe(this.viewLifecycleOwner, Observer<ResultToken> {
                    // Start Main Activity
                    if (!LoginViewModel.isFingerTouch) startMainActivity<MainActivity>(view.context, it, firstLogin)
                    else viewModel.checkFinger(this@LabelFragment, { startMainActivity<MainActivity>(view.context, it, firstLogin) }, { findNavController().navigate(R.id.checkPinFragment) })    //control finger
                })
            } else {
                if(sharedPreferences.getBoolean(LoginViewModel.ACCEPTED_TERMS_AND_CONDITIONS, false)) {
                    // Переход минуя disclaimer на логин фрагмент
                    val action = LabelFragmentDirections.actionLabelFragmentToLoginFragment()
                    findNavController().navigate(action)
                } else {
                    val action = LabelFragmentDirections.actionLabelFragmentToWelcomeFragment()
                    findNavController().navigate(action)
                }
            }
        } else {
            // Переход на disclaimer
            val action = LabelFragmentDirections.actionLabelFragmentToDisclaimerFragment()
            findNavController().navigate(action)
        }
    }

    private fun animateLogo(imageView: ImageView, textView: TextView) {

        val animatorImageX = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f).setDuration(1500)
        val animatorImageY = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 1f).setDuration(1500)

        val animatorTextAlpha = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).setDuration(1500)

        val animatorSet = AnimatorSet()
        animatorSet.play(animatorImageX).with(animatorImageY).before(animatorTextAlpha)

        animatorSet.start()
    }

    override fun onDestroy() {
        disposableTimer?.let { if (!it.isDisposed) it.dispose() }
        disposableRefresh?.let { if (!it.isDisposed) it.dispose() }
        super.onDestroy()
    }

}
