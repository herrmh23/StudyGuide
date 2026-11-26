
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.studyguide.components.FolderButton
import com.example.studyguide.models.Folder
import androidx.compose.ui.Alignment
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

@Composable
fun HomeScreen() {

    var showDialog by remember {mutableStateOf(false)}
    var folderName by remember {mutableStateOf("")}

    val folders = remember {
        mutableStateListOf<Folder>()
    }

    var currentFolderId by remember {mutableStateOf<String?>(null)}

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
                text = folders.find{ //loops through all folders
                    it.id == currentFolderId //finds which folder equals the current id
                }?.name ?: "StudyGuide", //if the result is null, display this
                style = MaterialTheme.typography.headlineLarge
            )

            Row(
                modifier = Modifier
                    .padding(innerPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) { //start of row
                if(currentFolderId != null) {

                    //folder path display
                    val folderPath = mutableListOf<String>()
                    var currFolder = currentFolderId

                    while(currFolder != null) {
                        val folder = folders.find {
                            it.id == currFolder
                        } ?: break

                        folderPath.add(folder.name)

                        currFolder = folder.parentId
                    }

                    folderPath.add("Home")
                    val currentFolderPath = folderPath
                        .reversed()
                        .joinToString("/")

                    //back button
                    TextButton(
                        onClick = {
                            currentFolderId = folders.find {
                                it.id == currentFolderId
                            }?.parentId
                        }
                    ) {Text("← Back")}

                    Text(
                        text = currentFolderPath,
                        style = MaterialTheme.typography.bodySmall
                    )

            }// end of if statement



            } //end of row





            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(folders.filter { it.parentId == currentFolderId }) { folder ->
                    FolderButton(
                        title = folder.name,
                        onClick = {
                            //set the ID when a folder is clicked on
                            currentFolderId = folder.id
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
                            folders.add(Folder(
                                name = folderName.trim(),
                                //sets the parent ID to the folder your currently in
                                parentId = currentFolderId))
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
