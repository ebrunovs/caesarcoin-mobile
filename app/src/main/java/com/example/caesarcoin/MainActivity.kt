package com.example.caesarcoin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.caesarcoin.ui.component.Footer
import com.example.caesarcoin.ui.theme.CaesarCoinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaesarCoinTheme {
                val navController = rememberNavController()
                MainScreen(navController = navController)
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            Footer(navController = navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavigationGraph(navController = navController)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    CaesarCoinTheme {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}