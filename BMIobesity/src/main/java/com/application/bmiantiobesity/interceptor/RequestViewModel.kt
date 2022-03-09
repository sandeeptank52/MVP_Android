package com.application.bmiantiobesity.interceptor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.application.bmiantiobesity.InTimeApplication
import com.application.bmiantiobesity.db.restinterceptor.RequestResponse
import com.application.bmiantiobesity.db.restinterceptor.RequestResponseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RequestViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var requestRepo: RequestResponseRepo

    //val requestRepo = RequestResponseRepo.getRepo()//(application)

    val allReq: LiveData<List<RequestResponse>>
    val selected = MutableLiveData<RequestResponse>()
    var resultFTS = MutableLiveData<List<RequestResponse>>()

    init {
        //Inject
        InTimeApplication.component?.injectToViewModel(this)

        allReq = requestRepo.getAllRequest()
        Log.d("RequestViewModel", requestRepo.toString())
    }

    fun insert(item: RequestResponse) = viewModelScope.launch(Dispatchers.IO) {
        requestRepo.insertReq(item)
    }

    fun delete(item: RequestResponse) = viewModelScope.launch(Dispatchers.IO) {
        requestRepo.deleteReq(item)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        requestRepo.deleteAll()
    }

    fun update(item: RequestResponse) = viewModelScope.launch(Dispatchers.IO) {
        requestRepo.updateReq(item)
    }

    fun searchBody(body: String) = viewModelScope.launch(Dispatchers.IO) {
        val resultItems = requestRepo.searchBody(body)
        withContext(Dispatchers.Main) {
            resultFTS.value = resultItems
        }
    }

    fun select(item: RequestResponse) {
        selected.value = item
    }
}