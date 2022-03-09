package com.application.bmiantiobesity.interceptor.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.db.restinterceptor.RequestResponse
import kotlinx.android.synthetic.main.interceptor_item_recycler.view.*


interface UiListener {
    fun onDelete(item: RequestResponse)
    fun onClickDetail(item: View)
}

class RequestRecyclerAdapter(val uiListener: UiListener) :
    ListAdapter<RequestResponse, RequestRecyclerAdapter.RequestRecyclerHolder>(ReqDiff) {

    override fun getItemCount() = currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestRecyclerHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.interceptor_item_recycler, parent, false)

        view.setOnClickListener {
            uiListener.onClickDetail(it)
        }

        return RequestRecyclerHolder(view)
    }

    override fun onBindViewHolder(holder: RequestRecyclerHolder, position: Int) {
        val current = currentList[position]

        holder.requestMethod.text = current.requestMethod
        holder.requestDate.text = current.requestDate
        holder.requestURL.text = current.requestUrl
        holder.responseCode.text =
            if (current.responseCode == 0) "-" else current.responseCode.toString()
        holder.responseTime.text = current.responseTime.toString()

        holder.deleteButton.setOnClickListener { uiListener.onDelete(current) }
    }

    inner class RequestRecyclerHolder(view: View) : RecyclerView.ViewHolder(view) {
        val requestMethod: TextView = view.textRequestMethod
        val requestDate: TextView = view.textRequestDate
        val requestURL: TextView = view.textRequestURL
        val responseCode: TextView = view.textResponseCode
        val responseTime: TextView = view.textResponseTime

        val deleteButton: Button = view.deleteReq
    }


    private object ReqDiff : DiffUtil.ItemCallback<RequestResponse>() {
        override fun areItemsTheSame(oldItem: RequestResponse, newItem: RequestResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: RequestResponse,
            newItem: RequestResponse
        ): Boolean {
            return oldItem.requestBody.equals(newItem.requestBody) &&
                    oldItem.requestContentLength == newItem.requestContentLength &&
                    oldItem.requestContentType.equals(newItem.requestContentType) &&
                    oldItem.requestDate.equals(newItem.requestDate) &&
                    oldItem.requestHeaders.equals(newItem.requestHeaders) &&
                    oldItem.requestMethod.equals(newItem.requestMethod) &&
                    oldItem.requestUrl.equals(newItem.requestUrl) &&
                    oldItem.responseCode == newItem.responseCode &&
                    oldItem.responseBody.equals(newItem.responseBody) &&
                    oldItem.responseHeaders.equals(newItem.responseHeaders) &&
                    oldItem.responseMessage.equals(newItem.responseMessage) &&
                    oldItem.responseProtocol.equals(newItem.responseProtocol) &&
                    oldItem.responseTime == newItem.responseTime
        }
    }
}