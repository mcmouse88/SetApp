package com.mcmouse88.file_to_server.presentation.files

import android.content.Context
import com.mcmouse88.file_to_server.R
import com.mcmouse88.file_to_server.domain.files.RemoteFile

sealed class FileListItem {

    data class File(
        val origin: RemoteFile,
        val deleteInProgress: Boolean
    ) : FileListItem() {
        private val size: Long get() = origin.size
        val id: String get() = origin.id
        val fileName: String get() = origin.fileName

        fun prettySize(context: Context): String {
            return if (size < 1024) {
                context.getString(R.string.size_bytes, size)
            } else if (size < 1024 * 1024) {
                context.getString(R.string.size_kbytes, size / 1024f)
            } else {
                context.getString(R.string.size_mbytes, size / 1024f / 1024f)
            }
        }
    }

    object Space : FileListItem()
}