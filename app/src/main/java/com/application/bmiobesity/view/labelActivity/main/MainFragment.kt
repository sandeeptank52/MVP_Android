package com.application.bmiobesity.view.labelActivity.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LabelMainFragmentBinding
import com.application.bmiobesity.view.loginActivity.LoginActivity
import com.application.bmiobesity.viewModels.LabelViewModel
import kotlinx.coroutines.*

class MainFragment : Fragment(R.layout.label_main_fragment) {

    private val mDelay: Long = 2500
    private val diffDelay: Long = 1000

    private var mainBinding: LabelMainFragmentBinding? = null
    private val labelModel: LabelViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainBinding = LabelMainFragmentBinding.bind(view)
        animateLabelScreen()

        lifecycleScope.launch(Dispatchers.IO) {
            labelModel.initAppPreference()
            when {
                labelModel.isFirstTime() -> {
                    firstTime()
                }
                labelModel.isNeedShowDisclaimer() -> {
                    showDisclaimer()
                }
                else -> {
                    startLoginActivity()
                }
            }
        }
    }

    private fun startLoginActivity(){
        lifecycleScope.launch(Dispatchers.IO) {
            val delayJob = async { delay(mDelay) }
            val initJob = async {
                labelModel.initCommonSetting()
            }
            delayJob.join()
            initJob.join()
            withContext(Dispatchers.Main){
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun showDisclaimer(){
        lifecycleScope.launch(Dispatchers.IO) {
            val delayJob = async { delay(mDelay) }
            val initJob = async {
                labelModel.initCommonSetting()
            }
            delayJob.join()
            initJob.join()
            withContext(Dispatchers.Main){
                findNavController().navigate(R.id.mainToDisclaimer)
            }
        }
    }

    private fun firstTime(){
        lifecycleScope.launch(Dispatchers.IO) {
            val delayJob = async { delay(mDelay) }
            val initJob = async {
                labelModel.initParamSetting()
                labelModel.initCommonSetting()
            }
            delayJob.join()
            initJob.join()
            withContext(Dispatchers.Main){
                findNavController().navigate(R.id.mainToWelcome)
            }
        }
    }

    private fun animateLabelScreen(){
        val animatorImageX = ObjectAnimator.ofFloat(mainBinding?.labelImageViewLogo, "scaleX", 0f, 1f).setDuration((mDelay - diffDelay))
        val animatorImageY = ObjectAnimator.ofFloat(mainBinding?.labelImageViewLogo, "scaleY", 0f, 1f).setDuration((mDelay - diffDelay))

        val animatorTextAlpha = ObjectAnimator.ofFloat(mainBinding?.labelTextViewAppName, "alpha", 0f, 1f).setDuration((mDelay - diffDelay))
        val animatorProgressAlpha = ObjectAnimator.ofFloat(mainBinding?.labelProgressBar, "alpha", 0f, 1f).setDuration((mDelay - diffDelay))

        val animatorSet = AnimatorSet()
        animatorSet.play(animatorImageX).with(animatorImageY).before(animatorTextAlpha).before(animatorProgressAlpha)

        animatorSet.start()
    }

    override fun onDestroyView() {
        mainBinding = null
        super.onDestroyView()
    }
}