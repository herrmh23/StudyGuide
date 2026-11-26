
package com.example.studyguide

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.studyguide.components.FolderButton
import com.example.studyguide.models.Folder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                HomeScreen()
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen() {

    var showDialog by remember {mutableStateOf(false)}
    var folderName by remember {mutableStateOf("")}

    val folders = remember {
        mutableStateListOf<Folder>()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    folderName = ""
                    showDialog = true
                }
            ) {
                Text(text = "+",
                    style = MaterialTheme.typography.headlineMedium)

            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Text(
                text = "StudyGuide",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MY FOLDERS",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(folders) { folder ->
                    FolderButton(
                        title = folder.name,
                        onClick = {
                            //future folder navigation
                        }
                    )
                }
            }
        }
    }

    if(showDialog) {
        AlertDialog(onDismissRequest = {
            showDialog = false
        },
            title = {Text("Create New Folder")},
            text = {
                OutlinedTextField(
                    value = folderName,
                    onValueChange = {folderName = it},
                    label = {Text("Folder Name")},
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if(folderName.isNotBlank()) {
                            folders.add(Folder(name = folderName.trim()))
                            showDialog = false
                        }
                    }
                ) {Text("Create")}
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {Text("Cancel")}
            })
    }
}
