package com.johannjara.docvault

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.johannjara.docvault.design.theme.DocVaultTheme
import com.johannjara.docvault.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DocVaultTheme {
                AppNavHost(
                    modifier = Modifier
                )
            }
        }
    }
}
