package com.example.caesarcoin.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import com.example.caesarcoin.auth.AuthViewModel
import com.example.caesarcoin.model.Extrato
import com.example.caesarcoin.model.TipoTransacao
import com.example.caesarcoin.viewmodel.ExtratoViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtratoScreen(
    authViewModel: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val extratoViewModel: ExtratoViewModel = viewModel()
    val usuario by authViewModel.usuarioLogado.collectAsState()
    val extratos by extratoViewModel.extratos.collectAsState()
    val totalCreditos by extratoViewModel.totalCreditos.collectAsState()
    val totalDebitos by extratoViewModel.totalDebitos.collectAsState()
    val saldoTotal by extratoViewModel.saldoTotal.collectAsState()
    val carregando by extratoViewModel.carregando.collectAsState()
    val erro by extratoViewModel.erro.collectAsState()
    
    var mostrarCadastro by remember { mutableStateOf(false) }
    
    val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formatoData = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    // Carregar extratos quando o usuário estiver disponível
    LaunchedEffect(usuario) {
        usuario?.let { user ->
            extratoViewModel.carregarExtratos(user.id)
        }
    }
    
    if (mostrarCadastro) {
        CadastroTransacaoScreen(
            authViewModel = authViewModel,
            extratoViewModel = extratoViewModel,
            onVoltar = { mostrarCadastro = false }
        )
        return
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Navigation back */ }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Extrato",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                
                // Espaço para balancear o layout
                Box(modifier = Modifier.width(48.dp))
            }
            
            if (carregando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFFD700))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Componente de Resumo (Entradas/Saídas)
                    item {
                        ResumoFinanceiroCard(
                            totalCreditos = totalCreditos,
                            totalDebitos = totalDebitos,
                            formatoMoeda = formatoMoeda
                        )
                    }
                    
                    // 2. Componente do Saldo Semanal
                    item {
                        SaldoSemanalCard(
                            saldoTotal = saldoTotal,
                            formatoMoeda = formatoMoeda
                        )
                    }
                    
                    // 3. Componente do Gráfico (Simplificado)
                    item {
                        GraficoSimplificadoCard(extratos = extratos)
                    }
                    
                    // 4. Botão Adicionar Transação
                    item {
                        Button(
                            onClick = { mostrarCadastro = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text(
                                    "Adicionar Transação",
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    
                    // 5. Lista de Transações
                    items(extratos) { extrato ->
                        TransacaoCard(
                            extrato = extrato,
                            formatoMoeda = formatoMoeda,
                            formatoData = formatoData
                        )
                    }
                    
                    // Espaço no final para o footer
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
        
        // Mostrar erro se houver
        erro?.let { mensagemErro ->
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter),
                action = {
                    TextButton(onClick = { 
                        // Recarregar extratos para limpar o erro
                        usuario?.let { user ->
                            extratoViewModel.carregarExtratos(user.id)
                        }
                    }) {
                        Text("OK", color = Color(0xFFFFD700))
                    }
                }
            ) {
                Text(mensagemErro)
            }
        }
    }
}

@Composable
fun ResumoFinanceiroCard(
    totalCreditos: Double,
    totalDebitos: Double,
    formatoMoeda: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Card Entradas
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Entradas",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatoMoeda.format(totalCreditos),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Card Saídas
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Saídas",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatoMoeda.format(totalDebitos),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SaldoSemanalCard(
    saldoTotal: Double,
    formatoMoeda: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Balanço Semanal",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = formatoMoeda.format(saldoTotal),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GraficoSimplificadoCard(extratos: List<Extrato>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (extratos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📊",
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Nenhuma transação encontrada",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Gráfico de Transações",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Simulação simples de gráfico com barras
                val totalCreditos = extratos.filter { it.tipo == TipoTransacao.CREDITO }.size
                val totalDebitos = extratos.filter { it.tipo == TipoTransacao.DEBITO }.size
                val maxTransacoes = maxOf(totalCreditos, totalDebitos).coerceAtLeast(1)
                
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Barra de Entradas
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = totalCreditos.toString(),
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height((totalCreditos.toFloat() / maxTransacoes * 100).dp.coerceAtLeast(10.dp))
                                .background(
                                    Color(0xFF4CAF50),
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        Text(
                            text = "Entradas",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                    
                    // Barra de Saídas
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = totalDebitos.toString(),
                            color = Color(0xFFF44336),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height((totalDebitos.toFloat() / maxTransacoes * 100).dp.coerceAtLeast(10.dp))
                                .background(
                                    Color(0xFFF44336),
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        Text(
                            text = "Saídas",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransacaoCard(
    extrato: Extrato,
    formatoMoeda: NumberFormat,
    formatoData: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone baseado no tipo
            val (icone, cor) = when (extrato.tipo) {
                TipoTransacao.CREDITO -> Pair(Icons.Default.ArrowDownward, Color(0xFF4CAF50))
                TipoTransacao.DEBITO -> Pair(Icons.Default.ArrowUpward, Color(0xFFF44336))
            }
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        cor.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icone,
                    contentDescription = null,
                    tint = cor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Informações da transação
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = extrato.titulo,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (extrato.descricao.isNotBlank()) {
                    Text(
                        text = extrato.descricao,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = formatoData.format(extrato.data.toDate()),
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
            
            // Valor
            Text(
                text = "${if (extrato.tipo == TipoTransacao.CREDITO) "+" else "-"}${formatoMoeda.format(extrato.valor)}",
                color = cor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}