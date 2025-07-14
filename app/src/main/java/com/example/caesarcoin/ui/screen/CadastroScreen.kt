package com.example.caesarcoin.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.caesarcoin.R
import com.example.caesarcoin.model.Usuario // ajuste o path se necessário

@Composable
fun CadastroScreen(
    onCadastrar: (Usuario) -> Unit,
    modifier: Modifier = Modifier
) {
    var nome by remember { mutableStateOf("") }
    var apelido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.caesar),
            contentDescription = "Caesar",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Cadastro",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apelido,
            onValueChange = { apelido = it },
            label = { Text("Apelido") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val usuario = Usuario(nome, apelido, email, senha)
                onCadastrar(usuario)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }
    }
}
