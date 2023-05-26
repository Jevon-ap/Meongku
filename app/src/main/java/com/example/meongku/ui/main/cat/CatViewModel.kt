package com.example.meongku.ui.main.cat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CatViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is cat Fragment"
    }
    val text: LiveData<String> = _text
}