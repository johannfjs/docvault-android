package com.johannjara.docvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.johannjara.docvault.design.theme.DocVaultTheme
import com.johannjara.docvault.ui.main.MainScreen
import com.johannjara.docvault.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsState()
            val context = LocalContext.current
            
            DocVaultTheme {
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
                    }
                )
            }
        }
    }
}
