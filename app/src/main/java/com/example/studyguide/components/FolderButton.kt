package com.example.studyguide.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FolderButton(
    title: String,              // name of folder
    subtitle: String = "",      //optional folder contents info
    onClick: () -> Unit         //onClick actions
) {
    Card (
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
           Text(
               text = title,
               style = MaterialTheme.typography.titleMedium
           )

           if(subtitle.isNotEmpty()) {
               Spacer(modifier = Modifier.height(4.dp))

               Text(
                   text = subtitle,
                   style = MaterialTheme.typography.bodySmall
               )
           }
        }}
}

