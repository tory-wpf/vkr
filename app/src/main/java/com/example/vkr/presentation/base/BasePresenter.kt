package com.example.vkr.presentation.base

open class BasePresenter<T> {
    private var view: T? = null

    fun attachView(view: T) {
        this.view = view
    }

    fun detachView() {
        view = null
    }

    fun getView(): T? = view
}