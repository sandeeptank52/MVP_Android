package com.application.bmiobesity.view.loginActivity.checkPin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.LoginCheckpinFragmentBinding

class CheckPinFragment : BaseFragment(R.layout.login_checkpin_fragment) {

    private var checkPinBinding: LoginCheckpinFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPinBinding = LoginCheckpinFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        checkPinBinding = null
        super.onDestroyView()
    }
}