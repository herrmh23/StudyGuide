package com.example.studyguide

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

internal enum class SettingsPage(@StringRes val title: Int) {
    Menu(R.string.settings),
    Profile(R.string.settings_profile),
    Terms(R.string.settings_terms),
    About(R.string.settings_about),
    Help(R.string.settings_help)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    page: SettingsPage,
    onBack: () -> Unit,
    onOpenPage: (SettingsPage) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(page.title),
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.settings_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            key(page) {
                Column(
                    modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth()
                        .verticalScroll(rememberScrollState()).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (page) {
                        SettingsPage.Menu -> {
                            SettingsPage.entries.drop(1).forEach { destination ->
                                OutlinedCard(
                                    onClick = { onOpenPage(destination) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.outlinedCardColors(containerColor = colors.surface)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            stringResource(destination.title),
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Icon(
                                            painter = painterResource(R.drawable.ic_chevron),
                                            contentDescription = null,
                                            tint = colors.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        SettingsPage.Profile -> SettingsCard {
                            Box(
                                modifier = Modifier.size(60.dp)
                                    .background(colors.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.settings_profile_initial),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = colors.onPrimaryContainer
                                )
                            }
                            SettingsSection(R.string.settings_profile_name, R.string.settings_profile_role)
                            Text(
                                stringResource(R.string.settings_profile_email),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )
                        }
                        SettingsPage.Terms -> SettingsCard {
                            SettingsSection(R.string.settings_terms_use_title, R.string.settings_terms_use)
                            SettingsSection(R.string.settings_terms_content_title, R.string.settings_terms_content)
                            SettingsSection(R.string.settings_terms_availability_title, R.string.settings_terms_availability)
                        }
                        SettingsPage.About -> SettingsCard {
                            SettingsSection(R.string.app_name, R.string.settings_copyright)
                        }
                        SettingsPage.Help -> SettingsCard {
                            SettingsSection(R.string.settings_help_folders_title, R.string.settings_help_folders)
                            SettingsSection(R.string.settings_help_import_title, R.string.settings_help_import)
                            SettingsSection(R.string.settings_help_back_title, R.string.settings_help_back)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsSection(@StringRes title: Int, @StringRes body: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            stringResource(title),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            stringResource(body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
