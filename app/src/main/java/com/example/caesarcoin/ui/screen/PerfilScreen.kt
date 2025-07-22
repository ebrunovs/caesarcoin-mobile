package com.example.caesarcoin.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caesarcoin.auth.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    authViewModel: AuthViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    // Estados da UI
    val usuario by authViewModel.usuarioLogado.collectAsState()
    val carregando by authViewModel.carregando.collectAsState()
    val erro by authViewModel.erro.collectAsState()
    val debugMessages by authViewModel.debugMessages.collectAsState()
    
    // Estados dos campos editáveis
    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var showDebug by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // Preencher campos quando usuário carregado
    LaunchedEffect(usuario) {
        usuario?.let { user ->
            nome = user.nome
            sobrenome = user.apelido
            email = user.email
            senha = user.senha
        }
    }
    
    // Mostrar debug quando houver mensagens
    LaunchedEffect(debugMessages.size) {
        if (debugMessages.isNotEmpty()) {
            showDebug = true
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header com botão voltar e título
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Botão Debug
            if (debugMessages.isNotEmpty() && !showDebug) {
                TextButton(onClick = { showDebug = true }) {
                    Text(
                        text = "🔍 Debug",
                        color = Color(0xFFFFD700),
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD700))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🐕",
                fontSize = 60.sp,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Card de Debug Visual (se houver mensagens)
        if (showDebug && debugMessages.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D1810)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔍 Debug Firebase",
                            color = Color(0xFFFFD700),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            TextButton(onClick = { showDebug = false }) {
                                Text("Ocultar", color = Color(0xFFFFD700))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.height(200.dp),
                        reverseLayout = true
                    ) {
                        items(debugMessages.reversed()) { message ->
                            Text(
                                text = message,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Card com informações do usuário
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Nome e email no topo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = nome,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = email,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    
                    IconButton(
                        onClick = { editMode = !editMode }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = if (editMode) "Cancelar edição" else "Editar perfil",
                            tint = Color(0xFFFFD700)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Campos editáveis
                CampoEditavel(
                    label = "Nome",
                    valor = nome,
                    onValorChange = { nome = it },
                    editMode = editMode
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                CampoEditavel(
                    label = "Sobrenome",
                    valor = sobrenome,
                    onValorChange = { sobrenome = it },
                    editMode = editMode
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                CampoEditavel(
                    label = "E-mail",
                    valor = email,
                    onValorChange = { email = it },
                    editMode = editMode,
                    keyboardType = KeyboardType.Email
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Campo senha com visualização
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Senha",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        if (editMode) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editável",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    if (editMode) {
                        OutlinedTextField(
                            value = senha,
                            onValueChange = { senha = it },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                                    Icon(
                                        imageVector = if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha",
                                        tint = Color(0xFFFFD700)
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.Gray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (senhaVisivel) senha else "••••••••••••",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                                Icon(
                                    imageVector = if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha",
                                    tint = Color(0xFFFFD700)
                                )
                            }
                        }
                    }
                }
                
                // Botão Salvar (apenas em modo de edição)
                if (editMode) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            scope.launch {
                                authViewModel.atualizarPerfil(nome, sobrenome, email, senha)
                                editMode = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !carregando
                    ) {
                        if (carregando) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "Salvar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                // Mostrar erro se houver
                erro?.let { mensagemErro ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = mensagemErro,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoEditavel(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit,
    editMode: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )
            if (editMode) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editável",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        if (editMode) {
            OutlinedTextField(
                value = valor,
                onValueChange = onValorChange,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
            )
        } else {
            Text(
                text = valor,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}