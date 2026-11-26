package com.example.studyguide.models

import java.util.UUID

data class Folder(
    val id: String = UUID.randomUUID().toString(), //unique ID
    val name: String,                              //Display name
    val parentId: String? = null                   //ID of parent
)