package com.files.task.data.remote

import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FilesRemoteDS {

    @Streaming
    @GET
    fun downloadFile(@Url url: String): Observable<ResponseBody>
}