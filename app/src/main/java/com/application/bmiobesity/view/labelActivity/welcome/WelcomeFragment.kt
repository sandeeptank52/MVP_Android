package com.application.bmiobesity.view.labelActivity.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LabelWelcomeTextFragmentV2Binding
import com.application.bmiobesity.view.loginActivity.LoginActivity
import com.application.bmiobesity.viewModels.LabelViewModel

class WelcomeFragment : Fragment(R.layout.label_welcome_text_fragment_v2) {

    private var welcomeBinding2: LabelWelcomeTextFragmentV2Binding? = null
    private val labelModel: LabelViewModel by activityViewModels()

    private var pagerItemCount = 5
    private lateinit var pagerAdapter: WelcomeFragmentAdapter
    private lateinit var dots: Array<TextView>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding2 = LabelWelcomeTextFragmentV2Binding.bind(view)
        init()
        initListeners()
    }

    private fun init(){
        pagerAdapter = WelcomeFragmentAdapter(requireActivity())
        pagerItemCount = pagerAdapter.itemCount
        welcomeBinding2?.welcomeViewPager?.adapter = pagerAdapter
        dots = Array(pagerItemCount){
            val item = TextView(requireContext())
            item.text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
            item.textSize = 35f
            item.setTextColor(resources.getColor(R.color.welcome_inactive_dot, null))
            welcomeBinding2?.welcomeDotsLayout?.addView(item)
            return@Array item
        }
    }

    private fun initListeners(){
        welcomeBinding2?.welcomeViewPager?.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentColorDots(position)
                if (position == pagerItemCount - 1){
                    welcomeBinding2?.welcomeNextButton?.text = resources.getString(R.string.button_got_it)
                    welcomeBinding2?.welcomeSkipButton?.visibility = View.GONE
                } else {
                    welcomeBinding2?.welcomeNextButton?.text = resources.getString(R.string.button_next)
                    welcomeBinding2?.welcomeSkipButton?.visibility = View.VISIBLE
                }
            }
        })

        welcomeBinding2?.welcomeNextButton?.setOnClickListener {
            val nextPage = getCurrentPage() + 1
            if (nextPage < dots.size){
                welcomeBinding2?.welcomeViewPager?.currentItem = nextPage
            } else {
                startLoginActivity()
            }
        }

        welcomeBinding2?.welcomeSkipButton?.setOnClickListener { startLoginActivity() }
    }

    private fun getCurrentPage(): Int{
        return welcomeBinding2?.welcomeViewPager?.currentItem ?: 0
    }

    private fun setCurrentColorDots(position: Int){
        if (dots.isNotEmpty() && position < dots.size){
            dots.forEach {
                it.setTextColor(resources.getColor(R.color.welcome_inactive_dot, null))
            }
            dots[position].setTextColor(resources.getColor(R.color.welcome_active_dot, null))
        }
    }

    private fun startLoginActivity(){
        if (labelModel.isNeedShowDisclaimer()){
            findNavController().navigate(R.id.welcomeToDisclaimer)
        } else {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        welcomeBinding2 = null
        super.onDestroyView()
    }
}