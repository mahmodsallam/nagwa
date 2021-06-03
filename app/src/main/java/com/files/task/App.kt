package com.files.task

import android.app.Application
import com.files.task.di.AppComponent
import com.files.task.di.DaggerAppComponent

class App :Application() {
    val appComponent: AppComponent = DaggerAppComponent.create()
}