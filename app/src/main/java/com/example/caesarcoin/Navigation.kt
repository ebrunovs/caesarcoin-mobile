package com.example.caesarcoin

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.example.caesarcoin.ui.screen.CadastroScreen
import com.example.caesarcoin.ui.screen.ExtratoScreen
import com.example.caesarcoin.ui.screen.HomeScreen
import com.example.caesarcoin.ui.screen.LoginScreen
import com.example.caesarcoin.ui.screen.PerfilScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
        composable("extrato") { ExtratoScreen() }
        composable("perfil") { PerfilScreen() }
        composable("sair") { LoginScreen('adm''adm') }
        composable("entrar") { LoginScreen() }
        composable("cadastrar") { CadastroScreen() }
        //composable("caesarcoin") { CaesarCoinScreen() }
    }
}

