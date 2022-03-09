package com.application.bmiantiobesity.interceptor.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.db.restinterceptor.RequestResponse
import com.application.bmiantiobesity.interceptor.RequestViewModel
import com.application.bmiantiobesity.interceptor.recyclerView.RequestRecyclerAdapter
import com.application.bmiantiobesity.interceptor.recyclerView.UiListener
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import kotlinx.android.synthetic.main.interceptor_fragment_list.*
import java.util.*
import java.util.concurrent.TimeUnit


class RequestListFragment : Fragment(R.layout.interceptor_fragment_list), UiListener {

    private val viewModel: RequestViewModel by activityViewModels()
    private lateinit var recycler: RecyclerView
    private val recyclerAdapter = RequestRecyclerAdapter(this)
    //private lateinit var mApi: PlaceHldrApi

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initActionBar()
        initFunc()
        initRecycler()
    }

    private fun initActionBar() {
        val mActionBar = (activity as AppCompatActivity).supportActionBar
        mActionBar?.title = "List of requests"
    }

    private fun initFunc() {
        //sendButtonGet1.setOnClickListener { submitRequest(1) }
        //sendButtonGet2.setOnClickListener { submitRequest(2) }
        //sendButtonGet3.setOnClickListener { submitRequest(3) }
        viewModel.resultFTS.observe(viewLifecycleOwner, Observer {
            it?.let {
                recyclerAdapter.submitList(it)
                textItemCount.text = it.size.toString()
            }
        })
    }

    /*private fun submitRequest(id: Int) {
        mApi = NetworkService.getService(activity?.application!!).getPhldrApi()
        mApi.getPostById(id, "testGetHeader_$id").enqueue(object : Callback<PlaceHldrData> {
            override fun onFailure(call: Call<PlaceHldrData>, t: Throwable) {
            }

            override fun onResponse(call: Call<PlaceHldrData>, response: Response<PlaceHldrData>) {
            }
        })
    }*/

    private fun initRecycler() {
        recycler = requestRecycler
        recycler.adapter = recyclerAdapter
        recycler.addItemDecoration(
            DividerItemDecoration(
                activity,
                (recycler.layoutManager as LinearLayoutManager).orientation
            )
        )
        viewModel.allReq.observe(viewLifecycleOwner, Observer {
            it?.let {
                recyclerAdapter.submitList(it)
                textItemCount.text = it.size.toString()
            }
        })
    }

    override fun onDelete(item: RequestResponse) {
        viewModel.delete(item)
    }

    override fun onClickDetail(item: View) {
        val childrenPosition = recycler.getChildAdapterPosition(item)
        val currentItem = recyclerAdapter.currentList[childrenPosition]
        viewModel.select(currentItem)
        findNavController().navigate(R.id.actionListReqToDetail)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.shareAction).isVisible = false

        val menuSearchItem = menu.findItem(R.id.searchAction)
        val searchView = menuSearchItem.actionView as SearchView
        val searchClose =
            searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView

        searchView.setIconifiedByDefault(false)

        menuSearchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                recyclerAdapter.submitList(viewModel.allReq.value)
                textItemCount.text = viewModel.allReq.value?.size.toString()
                return true
            }
        })

        searchClose.setOnClickListener {
            recyclerAdapter.submitList(viewModel.allReq.value)
            textItemCount.text = viewModel.allReq.value?.size.toString()
            searchView.setQuery("", false)
        }

        Observable.create(ObservableOnSubscribe<String> {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) it.onNext(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) it.onNext(newText)
                    return false
                }

            })
        })
            .map { text -> text.toLowerCase(Locale.getDefault()).trim() }
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { text -> text.isNotBlank() }
            .subscribe {
                viewModel.searchBody(it)
            }
    }
}