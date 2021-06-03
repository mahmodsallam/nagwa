package com.files.task.di

import com.files.task.presentation.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class , ProgressModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}