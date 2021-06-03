package com.files.task.di

import com.files.task.data.remote.FilesRemoteDS
import com.files.task.data.remote.OnAttachmentDownloadListener
import com.files.task.data.remote.ProgressResponseBody
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ProgressModule::class])
class NetworkModule {

     var progressListener:OnAttachmentDownloadListener? = null

    @Provides
    fun provideBaseUrl() = "https://www.example.com"

    @Provides
    fun provideProgressListener(progressListener:OnAttachmentDownloadListener?):OnAttachmentDownloadListener?{
        return progressListener
    }


    @Provides
    @Singleton
    fun provideRetrofit(

        baseUrl: String,
        client: OkHttpClient.Builder?
    ): Retrofit = run {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .client(client?.build())
            .build()
    }

    @Provides
    fun provideMoviesRemoteDS(retrofit: Retrofit): FilesRemoteDS =
        retrofit.create(FilesRemoteDS::class.java)


    @Provides
    @Singleton
    fun getOkHttpDownloadClientBuilder(): OkHttpClient.Builder? {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.connectTimeout(20, TimeUnit.SECONDS)
        httpClientBuilder.writeTimeout(0, TimeUnit.SECONDS)
        httpClientBuilder.readTimeout(5, TimeUnit.MINUTES)
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClientBuilder.addInterceptor(loggingInterceptor)
        httpClientBuilder.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                if (progressListener == null) return chain.proceed(chain.request())
                val originalResponse: Response = chain.proceed(chain.request())
                return originalResponse.newBuilder()
                    .body(originalResponse.body?.let { ProgressResponseBody(it, progressListener) })
                    .build()
            }
        })
        return httpClientBuilder
    }



}