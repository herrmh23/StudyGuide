package com.example.studyguide.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.studyguide.models.StudyFile
import java.io.File
import java.util.UUID


fun importFile (
    context: Context,
    uri: Uri,
    parentId: String?
): StudyFile {

    val resolver = context.contentResolver //allows app to accesses selected document

    var fileName = "Unkonwn File"

    //gets file information
    resolver.query(uri, null, null, null, null)?.use {cursor ->
        val nameIndex = cursor.getColumnIndex(
            OpenableColumns.DISPLAY_NAME //retreives origional file name
        )

        if(nameIndex >= 0 && cursor.moveToFirst()) {
            fileName = cursor.getString(nameIndex)
        }
    }

    val mimeType = resolver.getType(uri)?: "application/octet-stream" //gets file type

    val fileId = UUID.randomUUID().toString()

    //access the applications local storage and creates a directory
    val storageDirectory = File(context.filesDir, "documents")
    storageDirectory.mkdirs()

    val destination = File(storageDirectory, fileId)

    try {
        val input = resolver.openInputStream(uri)?: error("Unable to open selected file")

        input.use {inputStream ->
            destination.outputStream().use {outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }catch(e: Exception) {
        destination.delete()
        throw e
    }

    return StudyFile(
        id = fileId,
        name = fileName,
        parentId = parentId,
        filePath = destination.absolutePath,
        mimeType = mimeType
    )

}