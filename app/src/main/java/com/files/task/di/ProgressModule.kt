package com.files.task.di

import com.files.task.data.remote.OnAttachmentDownloadListener
import dagger.Binds
import dagger.Module

@Module
abstract class ProgressModule {
    @Binds
    abstract fun provideProgress(downloadProgress: OnAttachmentDownloadListener):
            OnAttachmentDownloadListener?
}