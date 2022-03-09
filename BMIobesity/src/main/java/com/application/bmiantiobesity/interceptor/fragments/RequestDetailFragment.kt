package com.application.bmiantiobesity.interceptor.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.db.restinterceptor.RequestResponse
import com.application.bmiantiobesity.interceptor.RequestViewModel
import kotlinx.android.synthetic.main.interceptor_fragment_detail.*

class RequestDetailFragment : Fragment(R.layout.interceptor_fragment_detail) {

    private val model: RequestViewModel by activityViewModels()
    private var mActionBar: ActionBar? = null
    private lateinit var mStrFoIntent: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        mActionBar = (activity as AppCompatActivity).supportActionBar

        model.selected.observe(viewLifecycleOwner, Observer {
            it?.let {
                initUI(it)
                mActionBar?.title = it.requestMethod + " " + it.requestUrl
                mStrFoIntent = it.toString()
            }
        })
    }

    private fun initUI(item: RequestResponse) {
        textMethod.text = item.requestMethod
        textRequestDate.text = item.requestDate
        textRequestURL.text = item.requestUrl

        textRequestHeaders.text = if (item.requestHeaders.isEmpty()) "-" else item.requestHeaders
        textRequestBody.text = if (item.requestBody.isNullOrEmpty()) "-" else item.requestBody
        textRequestContentType.text =
            if (item.requestContentType.equals("null")) "-" else item.requestContentType
        textContentLength.text =
            if (item.requestContentLength == null) "0" else item.requestContentLength.toString()

        textResponceCode.text = item.responseCode.toString()
        textResponseTime.text = item.responseTime.toString()
        textResponseMessage.text = if (item.responseMessage.isEmpty()) "-" else item.responseMessage
        textResponseProtocol.text = item.responseProtocol
        textResponseHeaders.text = item.responseHeaders
        textResponseBody.text = item.responseBody
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.searchAction).isVisible = false
        val shareItem = MenuItemCompat.getActionProvider(menu.findItem(R.id.shareAction)) as ShareActionProvider
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_TEXT, mStrFoIntent)
        shareItem.setShareIntent(mIntent)
    }


}