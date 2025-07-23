package com.example.caesarcoin

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.example.caesarcoin.viewmodel.AuthViewModel
import com.example.caesarcoin.model.Usuario
import com.example.caesarcoin.ui.screen.CadastroScreen
import com.example.caesarcoin.ui.screen.CadastroTransacaoScreen
import com.example.caesarcoin.ui.screen.ExtratoScreen
import com.example.caesarcoin.ui.screen.HomeScreen
import com.example.caesarcoin.ui.screen.LoginScreen
import com.example.caesarcoin.ui.screen.PerfilScreen
import com.example.caesarcoin.viewmodel.ExtratoViewModel

@Composable
fun NavigationGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val extratoViewModel: ExtratoViewModel = viewModel()
    val usuario by authViewModel.usuarioLogado.collectAsState()
    val erro by authViewModel.erro.collectAsState()

    LaunchedEffect(usuario) {
        usuario?.let { user ->
            navController.navigate("home") {
                popUpTo("entrar") { inclusive = true }
            }
        }
    }

    LaunchedEffect(erro) {
        erro?.let {
            Log.e("FirebaseErro", it)
        }
    }

    NavHost(navController = navController, startDestination = "entrar") {
        composable("home") { 
            HomeScreen(
                authViewModel = authViewModel,
                extratoViewModel = extratoViewModel,
                onNavigateToExtrato = { 
                    navController.navigate("extrato")
                },
                onNavigateToCadastro = { 
                    navController.navigate("cadastro_transacao")
                },
                onNavigateToPerfil = { 
                    navController.navigate("perfil")
                }
            ) 
        }
        composable("extrato") { 
            ExtratoScreen(
                authViewModel = authViewModel,
                extratoViewModel = extratoViewModel,
                onNavigateToHome = { 
                    navController.navigate("home") {
                        popUpTo("extrato") { inclusive = true }
                    }
                },
                onNavigateToCadastro = { 
                    navController.navigate("cadastro_transacao")
                }
            ) 
        }
        composable("perfil") { 
            PerfilScreen(
                authViewModel = authViewModel,
                onNavigateBack = { 
                    navController.navigateUp()
                }
            ) 
        }
        composable("cadastro_transacao") { 
            CadastroTransacaoScreen(
                authViewModel = authViewModel,
                extratoViewModel = extratoViewModel,
                onVoltar = { 
                    navController.navigate("home") {
                        popUpTo("cadastro_transacao") { inclusive = true }
                    }
                },
                onTransacaoSalva = { 
                    navController.navigate("home") {
                        popUpTo("cadastro_transacao") { inclusive = true }
                    }
                }
            )
        }
        composable("entrar") {
            LoginScreen(
                authViewModel = authViewModel,
                onLogin = { _, _ -> /* Não usado mais */ }
            )
        }
        composable("cadastrar") {
            CadastroScreen(
                authViewModel = authViewModel,
                onCadastrar = { _ -> /* Não usado mais */ }
            )
        }
    }
}
