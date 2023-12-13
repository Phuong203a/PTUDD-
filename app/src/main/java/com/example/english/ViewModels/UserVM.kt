package com.example.english.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.english.Models.User

class UserVM : ViewModel() {
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun setUser(user: User) {
        _userData.value = user
    }

    fun updateUser(user: User) {
        _userData.value = user
    }
}