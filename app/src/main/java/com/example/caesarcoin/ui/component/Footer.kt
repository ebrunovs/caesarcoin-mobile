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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.caesarcoin.R
import com.example.caesarcoin.viewmodel.AuthViewModel

@Composable
fun Footer(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val authViewModel: AuthViewModel = viewModel()
    val currentRoute by navController.currentBackStackEntryAsState()
    
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
            FooterItem(
                label = "Home", 
                icon = Icons.Default.Home,
                isActive = currentRoute?.destination?.route == "home",
                onClick = {
                    navController.navigate("home")
                }
            )
            FooterItem(
                label = "Extrato", 
                icon = Icons.Default.AccountBalanceWallet,
                isActive = currentRoute?.destination?.route == "extrato",
                onClick = {
                    navController.navigate("extrato")
                }
            )

            Spacer(modifier = Modifier.width(60.dp))

            FooterItem(
                label = "Perfil", 
                icon = Icons.Default.AccountCircle,
                isActive = currentRoute?.destination?.route == "perfil",
                onClick = {
                    navController.navigate("perfil")
                }
            )
            FooterItem(
                label = "Sair", 
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                isActive = false,
                onClick = {
                    authViewModel.logout()
                    navController.navigate("entrar") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

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
                    painter = painterResource(id = R.drawable.caesarcoin),
                    contentDescription = "Caesarcoin",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun FooterItem(
    label: String, 
    icon: ImageVector, 
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = label, 
            tint = if (isActive) Color(0xFFFFD700) else Color.White
        )
        Text(
            text = label, 
            color = if (isActive) Color(0xFFFFD700) else Color.White, 
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}