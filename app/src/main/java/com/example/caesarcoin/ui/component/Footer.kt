package com.example.caesarcoin.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.caesarcoin.R
import com.example.caesarcoin.auth.AuthViewModel

@Composable
fun Footer(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val authViewModel: AuthViewModel = viewModel()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterItem("Home", Icons.Default.Home) {
                navController.navigate("home")
            }
            FooterItem("Extrato", Icons.Default.AccountBalanceWallet) {
                navController.navigate("extrato")
            }

            Spacer(modifier = Modifier.width(60.dp)) // espaço para a moeda

            FooterItem("Perfil", Icons.Default.AccountCircle) {
                navController.navigate("perfil")
            }
            FooterItem("Sair", Icons.AutoMirrored.Filled.ExitToApp) {
                authViewModel.logout()
                navController.navigate("entrar") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        // Botão Central (Caesarcoin)
        Box(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-25).dp)
                .shadow(10.dp, shape = CircleShape)
        ) {
            IconButton(
                onClick = { navController.navigate("caesarcoin") },
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFD700), shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.caesarcoin), // imagem da moeda
                    contentDescription = "Caesarcoin",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun FooterItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = Color.White)
        Text(text = label, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}
