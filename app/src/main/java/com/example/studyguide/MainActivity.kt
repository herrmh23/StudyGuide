package com.example.studyguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.remember
import com.example.studyguide.utils.importFile
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import com.example.studyguide.components.FileButton

//custom imports
import com.example.studyguide.components.FolderButton
import com.example.studyguide.models.Folder
import com.example.studyguide.ui.theme.StudyGuideTheme
import com.example.studyguide.models.StudyFile

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StudyGuideTheme {
                HomeScreen()
            }
        }
    }
}

private val folderListSaver = listSaver<List<Folder>, Bundle>(
    save = { folders ->
        folders.map { folder ->
            Bundle().apply {
                putString("id", folder.id)
                putString("name", folder.name)
                putString("parentId", folder.parentId)
            }
        }
    },
    restore = { entries ->
        entries.map { entry ->
            Folder(
                id = requireNotNull(entry.getString("id")),
                name = requireNotNull(entry.getString("name")),
                parentId = entry.getString("parentId")
            )
        }
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var showDialog by rememberSaveable {mutableStateOf(false) }
    var folderName by rememberSaveable { mutableStateOf("") }
    var settingsPage by rememberSaveable { mutableStateOf<SettingsPage?>(null) }
    var showAddMenu by remember { mutableStateOf(false) }

    //region List Variables
    //folders list
    var folders by rememberSaveable(stateSaver = folderListSaver) {
        mutableStateOf(emptyList<Folder>())
    }
    var currentFolderId by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    //files list
    var files by remember {
        mutableStateOf<List<StudyFile>>(emptyList())
    }
    //endregion

    val gridState = rememberSaveable(currentFolderId, saver = LazyGridState.Saver) {
        LazyGridState()
    }
    val foldersById = folders.associateBy { it.id }
    val foldersByParent = folders.groupBy { it.parentId }
    val currentFolder = currentFolderId?.let(foldersById::get)
    val visibleFolders = foldersByParent[currentFolderId].orEmpty()
    val filesByParent = files.groupBy{ (it.parentId)}
    val visibleFiles = filesByParent[currentFolderId].orEmpty()
    val goBack = { currentFolderId = currentFolder?.parentId }
    val openCreateDialog = {
        folderName = ""
        showDialog = true
    }
    val colors = MaterialTheme.colorScheme

    //region Access File Selection Tools
    //allows for file picker to be opened and file to be picked
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val filePicker = rememberLauncherForActivityResult( //allows app to launch another android service
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> //file id
        if(uri != null) { //if a file is picked
            val parentId = currentFolderId

            scope.launch {
                try {
                    val newFile = withContext(Dispatchers.IO){
                        importFile(
                            context = context,
                            uri = uri,
                            parentId = parentId
                        )
                    }

                    files = files + newFile

                } catch(e: Exception) {
                    Toast.makeText(
                        context,
                        "Unable to import file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    //endregion

    val goBackFromSettings = {
        settingsPage = if (settingsPage == SettingsPage.Menu) null else SettingsPage.Menu
    }
    BackHandler(enabled = currentFolder != null && settingsPage == null, onBack = goBack)
    BackHandler(enabled = settingsPage != null, onBack = goBackFromSettings)

    //filter files by folder
    val shownFiles = files.filter {
        it.parentId == currentFolderId
    }

    val page = settingsPage
    if (page != null) {
        SettingsScreen(
            page = page,
            onBack = goBackFromSettings,
            onOpenPage = { settingsPage = it }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    if (currentFolder != null) {
                        IconButton(onClick = goBack) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = stringResource(R.string.back_to_parent)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showAddMenu = false
                        settingsPage = SettingsPage.Menu
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface)
            )
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = {
                        showAddMenu = !showAddMenu
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.new_folder)
                    )
                }

                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = {showAddMenu = false}
                ) {
                    DropdownMenuItem(
                        text = {Text("New Folder")},
                        onClick = {
                            showAddMenu = false
                            folderName = ""
                            showDialog = true
                        }
                    )

                    DropdownMenuItem(
                        text = {Text("New File")},
                        onClick = {
                            showAddMenu = false

                            filePicker.launch(
                                arrayOf(
                                    "application/pdf",
                                    "text/plain"
                                )
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                state = gridState,
                modifier = Modifier
                    .widthIn(max = 1040.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (currentFolder != null) {
                            val ancestors = generateSequence(currentFolder.parentId?.let(foldersById::get)) {
                                it.parentId?.let(foldersById::get)
                            }.toList().asReversed()
                            val path = listOf(stringResource(R.string.home)) + ancestors.map { it.name }
                            Text(
                                text = path.joinToString(" / "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )
                        }
                        Text(
                            text = currentFolder?.name ?: stringResource(R.string.learning_materials),
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = if (currentFolder == null) {
                                stringResource(R.string.library_description)
                            } else {
                                folderCountLabel(visibleFolders.size)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
                if (visibleFolders.isEmpty() && shownFiles.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.empty_folders),
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = openCreateDialog) {
                                Text(stringResource(R.string.new_folder))
                            }
                        }
                    }
                }
                //display folders
                items(visibleFolders, key = { it.id }) { folder ->
                    FolderButton(
                        title = folder.name,
                        subtitle = folderCountLabel(foldersByParent[folder.id].orEmpty().size) + " - " + fileCountLabel(filesByParent[folder.id].orEmpty().size),
                        onClick = { currentFolderId = folder.id }
                    )
                }
                //display files
                items(shownFiles, key = { it.id }) { file ->
                    FileButton(
                        title = file.name
                    )
                    //Text(text = file.name)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.new_folder)) },
            text = {
                OutlinedTextField(
                    value = folderName,
                    onValueChange = { folderName = it },
                    label = { Text(stringResource(R.string.folder_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    enabled = folderName.isNotBlank(),
                    onClick = {
                        folders = folders + Folder(name = folderName.trim(), parentId = currentFolderId)
                        showDialog = false
                    }
                ) { Text(stringResource(R.string.create_folder)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun folderCountLabel(count: Int): String =
    pluralStringResource(R.plurals.folder_count, count, count)

@Composable
private fun fileCountLabel(count: Int): String =
    pluralStringResource(R.plurals.file_count, count, count)
