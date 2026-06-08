package org.censusmate.mobile.presentation.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.censusmate.mobile.R
import org.censusmate.mobile.domain.model.Role
import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.domain.usecase.user.BlockUserUseCase
import org.censusmate.mobile.domain.usecase.user.GetUsersUseCase
import org.censusmate.mobile.presentation.users.components.UserCard
import org.censusmate.mobile.ui.theme.MobileApplicationTheme

@Composable
fun UsersRoute(
    getUsersUseCase: GetUsersUseCase,
    blockUserUseCase: BlockUserUseCase,
    shouldRefresh: Boolean = false,
    onRefreshHandled: () -> Unit = {},
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: UsersViewModel = viewModel(
        factory = UsersViewModel.factory(getUsersUseCase, blockUserUseCase)
    )

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadUsers(refresh = true)
            onRefreshHandled()
        }
    }

    UsersScreen(
        state = viewModel.state,
        onRefresh = { viewModel.loadUsers(refresh = true) },
        onLoadNextPage = { viewModel.loadNextPage() },
        onBlockUser = { id, blocked -> viewModel.blockUser(id, blocked) },
        onEditUser = onNavigateToEdit,
        onNavigateToCreate = onNavigateToCreate,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    state: UsersViewModel.State,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    onBlockUser: (String, Boolean) -> Unit,
    onEditUser: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.users)) }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }, actions = {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = onNavigateToCreate) {
            Icon(Icons.Default.PersonAdd, contentDescription = stringResource(R.string.add_user))
        }
    }) { padding ->
        when (state) {
            is UsersViewModel.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is UsersViewModel.State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRefresh) { Text(stringResource(R.string.repeat)) }
                    }
                }
            }

            is UsersViewModel.State.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.users, key = { it.id }) { user ->
                        UserCard(
                            user = user,
                            onEdit = { onEditUser(user.id) },
                            onBlock = { onBlockUser(user.id, !user.isBlocked) },
                        )
                    }

                    if (!state.isLastPage) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(onClick = onLoadNextPage) {
                                    Text(stringResource(R.string.load_more))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private val previewUsers = listOf(
    User(
        "1", "admin@census.ru",
        "Иван", "Петров",
        Role.ADMINISTRATOR,
        false,
        "2026-01-01",
        null
    ),
    User(
        "2", "agent@census.ru",
        "Иван", "Петров",
        Role.AGENT,
        false,
        "2026-01-02",
        null
    ),
)

@Preview(showBackground = true, name = "Users Screen")
@Composable
private fun UsersSuccessPreview() {
    MobileApplicationTheme {
        UsersScreen(
            state = UsersViewModel.State.Success(previewUsers, isLastPage = true),
            onRefresh = {},
            onLoadNextPage = {},
            onBlockUser = { _, _ -> },
            onEditUser = { _ -> },
            onNavigateToCreate = {},
            onBack = {}
        )
    }
}