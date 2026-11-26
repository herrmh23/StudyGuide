package com.example.studyguide.models

import java.util.UUID

data class StudyFile(
    val id: String = UUID.randomUUID().toString(), //unique identifier
    val name: String,                              //origional file name
    val parentId: String?,                          //id of file's folder
    val filePath: String,                          //location of file
    val mimeType: String                           //type of file
)
