package com.example.caesarcoin.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.caesarcoin.viewmodel.AuthViewModel
import com.example.caesarcoin.model.Extrato
import com.example.caesarcoin.model.TipoTransacao
import com.example.caesarcoin.viewmodel.ExtratoViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = viewModel(),
    extratoViewModel: ExtratoViewModel = viewModel(),
    onNavigateToExtrato: () -> Unit = {},
    onNavigateToCadastro: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val usuario by authViewModel.usuarioLogado.collectAsState()
    val extratos by extratoViewModel.extratos.collectAsState()
    val totalCreditos by extratoViewModel.totalCreditos.collectAsState()
    val totalDebitos by extratoViewModel.totalDebitos.collectAsState()
    val saldoTotal by extratoViewModel.saldoTotal.collectAsState()
    val carregando by extratoViewModel.carregando.collectAsState()
    
    val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formatoData = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formatoDataCompleta = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("pt", "BR"))
    
    LaunchedEffect(usuario?.id) {
        usuario?.let { user ->
            extratoViewModel.carregarExtratos(user.id)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Olá ${usuario?.nome ?: "Usuário"}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatoDataCompleta.format(Date()).replaceFirstChar { 
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
                            },
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    
                    // Ícone de perfil (placeholder)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFFFFD700),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onNavigateToPerfil() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = usuario?.nome?.firstOrNull()?.toString()?.uppercase() ?: "U",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFD700)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "SALDO",
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = formatoMoeda.format(saldoTotal),
                                        color = Color.Black,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                IconButton(
                                    onClick = onNavigateToCadastro,
                                    modifier = Modifier
                                        .background(
                                            Color.Black.copy(alpha = 0.1f),
                                            RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Adicionar Transação",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            
                            Text(
                                text = formatoDataCompleta.format(Date()),
                                color = Color.Black.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Color(0xFF4CAF50).copy(alpha = 0.2f),
                                        RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = "Entradas",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = formatoMoeda.format(totalCreditos),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Color(0xFFF44336).copy(alpha = 0.2f),
                                        RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color(0xFFF44336),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = "Saídas",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = formatoMoeda.format(totalDebitos),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Extrato da conta",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    TextButton(onClick = onNavigateToExtrato) {
                        Text(
                            text = "Ver tudo",
                            color = Color(0xFFFFD700),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            if (carregando) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFFD700))
                    }
                }
            } else {
                val ultimasTransacoes = extratos.take(5)
                
                if (ultimasTransacoes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
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
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(ultimasTransacoes) { transacao ->
                        HomeTransacaoCard(
                            extrato = transacao,
                            formatoMoeda = formatoMoeda,
                            formatoData = formatoData
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HomeTransacaoCard(
    extrato: Extrato,
    formatoMoeda: NumberFormat,
    formatoData: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            
            Text(
                text = "${if (extrato.tipo == TipoTransacao.CREDITO) "+" else "-"}${formatoMoeda.format(extrato.valor)}",
                color = cor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
