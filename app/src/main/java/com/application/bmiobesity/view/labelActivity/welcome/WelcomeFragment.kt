package com.application.bmiobesity.view.labelActivity.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LabelWelcomeTextFragmentBinding
import com.application.bmiobesity.view.loginActivity.LoginActivity
import com.application.bmiobesity.viewModels.LabelViewModel

class WelcomeFragment : Fragment(R.layout.label_welcome_text_fragment) {

    private var welcomeBinding: LabelWelcomeTextFragmentBinding? = null
    private val labelModel: LabelViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding = LabelWelcomeTextFragmentBinding.bind(view)

        welcomeBinding?.welcomeButtonNext?.setOnClickListener {
            if (labelModel.isNeedShowDisclaimer()){
                findNavController().navigate(R.id.welcomeToDisclaimer)
            } else {
                val intent = Intent(context, LoginActivity::class.java)
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