package com.example.caesarcoin.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.caesarcoin.R

@Composable
fun FooterLogin(
    modifier: Modifier = Modifier,
    navController: NavController
) {
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
            FooterLoginItem(
                label = "Entrar", 
                icon = Icons.AutoMirrored.Filled.Login,
                isActive = currentRoute?.destination?.route == "entrar",
                onClick = {
                    navController.navigate("entrar")
                }
            )

            Spacer(modifier = Modifier.width(60.dp))

            FooterLoginItem(
                label = "Cadastrar", 
                icon = Icons.Default.PersonAdd,
                isActive = currentRoute?.destination?.route == "cadastrar",
                onClick = {
                    navController.navigate("cadastrar")
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
                    painter = painterResource(id = R.drawable.caesarcoin), // imagem da moeda
                    contentDescription = "Caesarcoin",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun FooterLoginItem(
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