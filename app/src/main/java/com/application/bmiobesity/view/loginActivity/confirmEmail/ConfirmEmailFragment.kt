package com.application.bmiobesity.view.loginActivity.confirmEmail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.LoginConfirmEmailFragmentBinding

class ConfirmEmailFragment : BaseFragment(R.layout.login_confirm_email_fragment) {

    private var confirmEmailBinding: LoginConfirmEmailFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirmEmailBinding = LoginConfirmEmailFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        confirmEmailBinding = null
        super.onDestroyView()
    }
}