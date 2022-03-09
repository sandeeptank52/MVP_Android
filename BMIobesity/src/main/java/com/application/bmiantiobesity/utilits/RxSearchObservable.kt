package com.application.bmiantiobesity.utilits

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.lang.IllegalArgumentException

object RxSearchObservable {

    fun <T:View> fromView(view: T): Observable<String> {

        val subject: PublishSubject<String> = PublishSubject.create()

        when (view) {
            is SearchView -> view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d("Search - query", query ?: "")
                    //subject.onComplete()
                    //if (query != null) subject.onNext(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        Log.d("Search - query", newText)
                        subject.onNext(newText)
                    }
                    return true
                }
            })

            is EditText -> view.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    Log.d("Search - text", s.toString())
                    subject.onNext(s.toString())
                }

                override fun afterTextChanged(s: Editable) {
                }
            })

            else -> throw IllegalArgumentException("Unknown EditView!")
        }

        return subject
    }
}