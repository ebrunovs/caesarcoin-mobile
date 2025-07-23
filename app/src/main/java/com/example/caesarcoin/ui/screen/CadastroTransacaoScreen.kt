package com.example.caesarcoin.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caesarcoin.viewmodel.AuthViewModel
import com.example.caesarcoin.model.Extrato
import com.example.caesarcoin.model.TipoTransacao
import com.example.caesarcoin.viewmodel.ExtratoViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroTransacaoScreen(
    authViewModel: AuthViewModel = viewModel(),
    extratoViewModel: ExtratoViewModel = viewModel(),
    onVoltar: () -> Unit,
    onTransacaoSalva: () -> Unit = onVoltar,
    modifier: Modifier = Modifier
) {
    val usuario by authViewModel.usuarioLogado.collectAsState()
    val carregando by extratoViewModel.carregando.collectAsState()
    val erro by extratoViewModel.erro.collectAsState()
    
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var tipoSelecionado by remember { mutableStateOf(TipoTransacao.DEBITO) }
    var dataSelecionada by remember { mutableStateOf(Date()) }
    var transacaoSalva by remember { mutableStateOf(false) }
    
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVoltar) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Transação",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                
                // Espaço para balancear o layout
                Box(modifier = Modifier.width(48.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Card do formulário
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Cadastre uma Nova Transação",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    
                    // Campo Título
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFFFD700)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Campo Descrição
                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Descrição (opcional)", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFFFD700)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    
                    // Dropdown para Tipo
                    var expandido by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expandido,
                        onExpandedChange = { expandido = !expandido },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = if (tipoSelecionado == TipoTransacao.CREDITO) "Entrada (+)" else "Saída (-)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo", color = Color.Gray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFFFD700)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expandido,
                            onDismissRequest = { expandido = false }
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        "Entrada (+)", 
                                        color = Color(0xFF4CAF50)
                                    ) 
                                },
                                onClick = {
                                    tipoSelecionado = TipoTransacao.CREDITO
                                    expandido = false
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        "Saída (-)", 
                                        color = Color(0xFFF44336)
                                    ) 
                                },
                                onClick = {
                                    tipoSelecionado = TipoTransacao.DEBITO
                                    expandido = false
                                }
                            )
                        }
                    }
                    
                    // Campo Data - Novo seletor melhorado
                    OutlinedTextField(
                        value = dateFormatter.format(dataSelecionada),
                        onValueChange = {},
                        label = { Text("Data de Realização", color = Color.Gray) },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { 
                                // Usar DatePickerDialog nativo do Android
                                val calendar = Calendar.getInstance()
                                calendar.time = dataSelecionada
                                
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val novaData = Calendar.getInstance()
                                        novaData.set(year, month, dayOfMonth)
                                        dataSelecionada = novaData.time
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Selecionar data",
                                    tint = Color(0xFFFFD700)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFFFD700)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Botões de atalho para datas comuns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val hoje = Date()
                        val ontem = Date(hoje.time - 24 * 60 * 60 * 1000)
                        
                        TextButton(
                            onClick = { dataSelecionada = hoje },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Hoje",
                                color = Color(0xFFFFD700),
                                fontSize = 12.sp
                            )
                        }
                        
                        TextButton(
                            onClick = { dataSelecionada = ontem },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Ontem",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    // Campo Valor
                    OutlinedTextField(
                        value = valor,
                        onValueChange = { novoValor ->
                            // Filtrar apenas números e ponto decimal
                            if (novoValor.matches(Regex("^\\d*\\.?\\d*$"))) {
                                valor = novoValor
                            }
                        },
                        label = { Text("Valor", color = Color.Gray) },
                        prefix = { Text("R$ ", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFFFD700)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Botões
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onVoltar,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray
                            )
                        ) {
                            Text("Cancelar")
                        }
                        
                        Button(
                            onClick = {
                                val usuarioAtual = usuario
                                if (usuarioAtual != null && titulo.isNotBlank() && valor.isNotBlank()) {
                                    val valorDouble = valor.toDoubleOrNull() ?: 0.0
                                    if (valorDouble > 0) {
                                        val novaTransacao = Extrato(
                                            titulo = titulo,
                                            descricao = descricao,
                                            valor = valorDouble,
                                            tipo = tipoSelecionado,
                                            data = Timestamp(dataSelecionada),
                                            usuarioId = usuarioAtual.id
                                        )
                                        extratoViewModel.adicionarTransacao(novaTransacao, usuarioAtual.id)
                                        transacaoSalva = true
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700),
                                contentColor = Color.Black
                            ),
                            enabled = !carregando
                        ) {
                            if (carregando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black
                                )
                            } else {
                                Text("Cadastrar")
                            }
                        }
                    }
                }
            }
            
            // Mostrar erro se houver
            erro?.let { mensagemErro ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x44FF0000)
                    )
                ) {
                    Text(
                        text = "❌ ERRO: $mensagemErro",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Espaço extra no final para garantir que tudo seja visível
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
    
    // Redirecionar para extrato quando transação for salva com sucesso
    LaunchedEffect(transacaoSalva, carregando, erro) {
        if (transacaoSalva && !carregando && erro == null) {
            onTransacaoSalva()
        }
    }
}