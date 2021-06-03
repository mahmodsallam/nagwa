package com.files.task.data.repository

import com.files.task.data.remote.FilesRemoteDS
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import javax.inject.Inject

class FilesRepository @Inject constructor(private val filesRemoteDS: FilesRemoteDS)  {
    fun downloadFile(url:String):Observable<ResponseBody> {
        return filesRemoteDS.downloadFile(url)
    }
}