package com.application.bmiobesity.view.labelActivity.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.LabelWelcomeTextFragmentBinding
import com.application.bmiobesity.view.loginActivity.LoginActivity
import com.application.bmiobesity.view.mainActivity.MainActivity
import com.application.bmiobesity.viewModels.LabelViewModel
import kotlinx.coroutines.*

class WelcomeFragment : BaseFragment(R.layout.label_welcome_text_fragment) {

    private var welcomeBinding: LabelWelcomeTextFragmentBinding? = null
    private val labelModel: LabelViewModel by activityViewModels()

    private var pagerItemCount = 5
    private lateinit var pagerAdapter: WelcomeFragmentAdapter
    private lateinit var dots: Array<TextView>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding = LabelWelcomeTextFragmentBinding.bind(view)
        init()
        initListeners()
    }

    private fun init() {
        pagerAdapter = WelcomeFragmentAdapter(requireActivity())
        pagerItemCount = pagerAdapter.itemCount
        welcomeBinding?.welcomeViewPager?.adapter = pagerAdapter
        dots = Array(pagerItemCount) {
            val item = TextView(requireContext())
            item.text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
            item.textSize = 35f
            item.visibility = View.GONE
            item.setTextColor(resources.getColor(R.color.welcome_inactive_dot, null))
            welcomeBinding?.welcomeDotsLayout?.addView(item)
            return@Array item
        }
    }

    private fun initListeners() {
        welcomeBinding?.welcomeViewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                setCurrentColorDots(position)
                val colouredDot =
                    ResourcesCompat.getDrawable(resources, R.drawable.coloureddot, null)
                val justDot = ResourcesCompat.getDrawable(resources, R.drawable.justdot, null)

                when (position) {
                    0 -> {
                        welcomeBinding?.welcomeNextButton?.visibility = View.GONE
                        welcomeBinding?.welcomeSkipButton?.visibility = View.GONE
                        welcomeBinding?.dotone?.background = colouredDot
                        welcomeBinding?.dottwo?.background = justDot
                        welcomeBinding?.dotthree?.background = justDot
                        welcomeBinding?.dotfour?.background = justDot
                        welcomeBinding?.dotfive?.background = justDot

                        welcomeBinding?.dotone?.visibility = View.VISIBLE
                        welcomeBinding?.dottwo?.visibility = View.VISIBLE
                        welcomeBinding?.dotthree?.visibility = View.VISIBLE
                        welcomeBinding?.dotfour?.visibility = View.VISIBLE
                        welcomeBinding?.dotfive?.visibility = View.VISIBLE
                    }
                    1 -> {
                        welcomeBinding?.welcomeNextButton?.visibility = View.GONE
                        welcomeBinding?.welcomeSkipButton?.visibility = View.GONE
                        welcomeBinding?.dotone?.background = justDot
                        welcomeBinding?.dottwo?.background = colouredDot
                        welcomeBinding?.dotthree?.background = justDot
                        welcomeBinding?.dotfour?.background = justDot
                        welcomeBinding?.dotfive?.background = justDot

                        welcomeBinding?.dotone?.visibility = View.VISIBLE
                        welcomeBinding?.dottwo?.visibility = View.VISIBLE
                        welcomeBinding?.dotthree?.visibility = View.VISIBLE
                        welcomeBinding?.dotfour?.visibility = View.VISIBLE
                        welcomeBinding?.dotfive?.visibility = View.VISIBLE
                    }
                    2 -> {
                        welcomeBinding?.welcomeNextButton?.visibility = View.GONE
                        welcomeBinding?.welcomeSkipButton?.visibility = View.GONE
                        welcomeBinding?.dotone?.background = justDot
                        welcomeBinding?.dottwo?.background = justDot
                        welcomeBinding?.dotthree?.background = colouredDot
                        welcomeBinding?.dotfour?.background = justDot
                        welcomeBinding?.dotfive?.background = justDot

                        welcomeBinding?.dotone?.visibility = View.VISIBLE
                        welcomeBinding?.dottwo?.visibility = View.VISIBLE
                        welcomeBinding?.dotthree?.visibility = View.VISIBLE
                        welcomeBinding?.dotfour?.visibility = View.VISIBLE
                        welcomeBinding?.dotfive?.visibility = View.VISIBLE
                    }
                    3 -> {
                        welcomeBinding?.welcomeNextButton?.visibility = View.GONE
                        welcomeBinding?.welcomeSkipButton?.visibility = View.GONE
                        welcomeBinding?.dotone?.background = justDot
                        welcomeBinding?.dottwo?.background = justDot
                        welcomeBinding?.dotthree?.background = justDot
                        welcomeBinding?.dotfour?.background = colouredDot
                        welcomeBinding?.dotfive?.background = justDot

                        welcomeBinding?.dotone?.visibility = View.VISIBLE
                        welcomeBinding?.dottwo?.visibility = View.VISIBLE
                        welcomeBinding?.dotthree?.visibility = View.VISIBLE
                        welcomeBinding?.dotfour?.visibility = View.VISIBLE
                        welcomeBinding?.dotfive?.visibility = View.VISIBLE
                    }
                    else -> {
                        welcomeBinding?.welcomeNextButton?.text =
                            resources.getString(R.string.button_come_in)
                        welcomeBinding?.welcomeNextButton?.visibility = View.VISIBLE
                        welcomeBinding?.welcomeSkipButton?.visibility = View.GONE
                        welcomeBinding?.dotone?.visibility = View.GONE
                        welcomeBinding?.dottwo?.visibility = View.GONE
                        welcomeBinding?.dotthree?.visibility = View.GONE
                        welcomeBinding?.dotfour?.visibility = View.GONE
                        welcomeBinding?.dotfive?.visibility = View.GONE
                    }
                }
            }
        })

        welcomeBinding?.welcomeNextButton?.setOnClickListener {
            val nextPage = getCurrentPage() + 1
            if (nextPage < dots.size) {
                welcomeBinding?.welcomeViewPager?.currentItem = nextPage
            } else {
                startActivity()
            }
        }

        welcomeBinding?.welcomeSkipButton?.setOnClickListener { startActivity() }
    }

    private fun getCurrentPage(): Int {
        return welcomeBinding?.welcomeViewPager?.currentItem ?: 0
    }

    private fun setCurrentColorDots(position: Int) {
        if (dots.isNotEmpty() && position < dots.size) {
            dots.forEach {
                it.setTextColor(resources.getColor(R.color.welcome_inactive_dot, null))
            }
            dots[position].setTextColor(resources.getColor(R.color.welcome_active_dot, null))
        }
    }

    private fun startActivity() {
        when {
            labelModel.isFirstTime() -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    val initJob = async {
                        labelModel.initParamSetting()
                    }
                    initJob.join()
                    withContext(Dispatchers.Main) {
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }
            else -> {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        welcomeBinding = null
        super.onDestroyView()
    }
}