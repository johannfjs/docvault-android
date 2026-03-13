package com.johannjara.docvault.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.johannjara.docvault.ui.detail.DetailScreen
import com.johannjara.docvault.ui.main.MainScreen
import com.johannjara.docvault.ui.main.MainViewModel
import kotlinx.serialization.Serializable

@Serializable
object MainRoute

@Serializable
data class DetailRoute(val documentId: String)

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoute,
        modifier = modifier
    ) {
        composable<MainRoute> {
            val viewModel: MainViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            val context = LocalContext.current

            MainScreen(
                state = state,
                onTypeFilterSelected = viewModel::onTypeFilterSelected,
                onAddDocument = { uri, name, type ->
                    viewModel.onAddDocument(
                        context = context,
                        uri = uri,
                        name = name,
                        type = type
                    )
                },
                onDocumentClick = { documentId ->
                    navController.navigate(DetailRoute(documentId))
                }
            )
        }
        composable<DetailRoute> { backStackEntry ->
            val detail: DetailRoute = backStackEntry.toRoute()
            DetailScreen(documentId = detail.documentId)
        }
    }
}
