package com.files.task.data.remote

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

interface OnAttachmentDownloadListener {
    fun onAttachmentDownloadedSuccess()
    fun onAttachmentDownloadedError()
    fun onAttachmentDownloadedFinished()
    fun onAttachmentDownloadUpdate(percent: Int)
}

class ProgressResponseBody(
    private val responseBody: ResponseBody,
    private val progressListener: OnAttachmentDownloadListener?
) :
    ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source())?.buffer()
        }
        return bufferedSource as BufferedSource
    }

    private fun source(source: Source): Source? {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L
            override fun read(sink: Buffer, byteCount: Long): Long {
                var bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                val percent =
                    if (bytesRead == -1L) 100f else totalBytesRead.toFloat() / responseBody.contentLength()
                        .toFloat() * 100
                if (totalBytesRead - bytesRead >= 2)
                    progressListener?.onAttachmentDownloadUpdate(percent.toInt())
                return bytesRead

            }
        }
    }


}