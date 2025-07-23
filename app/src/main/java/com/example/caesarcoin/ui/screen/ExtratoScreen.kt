package com.example.caesarcoin.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtratoScreen(
    authViewModel: AuthViewModel = viewModel(),
    extratoViewModel: ExtratoViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToCadastro: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val usuario by authViewModel.usuarioLogado.collectAsState()
    val extratos by extratoViewModel.extratos.collectAsState()
    val totalCreditos by extratoViewModel.totalCreditos.collectAsState()
    val totalDebitos by extratoViewModel.totalDebitos.collectAsState()
    val saldoTotal by extratoViewModel.saldoTotal.collectAsState()
    val carregando by extratoViewModel.carregando.collectAsState()
    val erro by extratoViewModel.erro.collectAsState()
    
    val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formatoData = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    LaunchedEffect(usuario) {
        usuario?.let { user ->
            extratoViewModel.carregarExtratos(user.id)
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateToHome) {
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
                    item {
                        ResumoFinanceiroCard(
                            totalCreditos = totalCreditos,
                            totalDebitos = totalDebitos,
                            formatoMoeda = formatoMoeda
                        )
                    }
                    
                    item {
                        SaldoSemanalCard(
                            saldoTotal = saldoTotal,
                            formatoMoeda = formatoMoeda
                        )
                    }
                    
                    // 3. Componente do Gráfico (Gráfico de Linhas)
                    item {
                        GraficoLinhasCard(extratos = extratos)
                    }
                    
                    // 4. Lista de Transações
                    items(extratos) { extrato ->
                        TransacaoCard(
                            extrato = extrato,
                            formatoMoeda = formatoMoeda,
                            formatoData = formatoData,
                            onExcluir = { extratoId ->
                                usuario?.let { user ->
                                    extratoViewModel.excluirTransacao(extratoId, user.id)
                                }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
        
        FloatingActionButton(
            onClick = onNavigateToCadastro,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFFFD700),
            contentColor = Color.Black
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Adicionar Transação",
                modifier = Modifier.size(24.dp)
            )
        }
        
        erro?.let { mensagemErro ->
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter),
                action = {
                    TextButton(onClick = { 
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
fun GraficoLinhasCard(extratos: List<Extrato>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
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
                    text = "Últimos 7 Dias",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Calcular os últimos 7 dias FORA do Canvas
                val calendar = Calendar.getInstance()
                val hoje = calendar.time
                val diasLabels = mutableListOf<String>()
                val datasConsulta = mutableListOf<Date>()
                
                // Criar lista dos últimos 7 dias (do mais antigo para o mais recente)
                for (i in 6 downTo 0) {
                    calendar.time = hoje
                    calendar.add(Calendar.DAY_OF_YEAR, -i)
                    datasConsulta.add(calendar.time)
                    
                    val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
                    diasLabels.add(formatter.format(calendar.time))
                }
                
                val dadosEntradas = mutableListOf<Float>()
                val dadosSaidas = mutableListOf<Float>()
                
                // Agrupar transações por cada um dos últimos 7 dias
                for (data in datasConsulta) {
                    val entradasDia = extratos.filter { extrato ->
                        val extratoCalendar = Calendar.getInstance()
                        extratoCalendar.time = extrato.data.toDate()
                        
                        val dataCalendar = Calendar.getInstance()
                        dataCalendar.time = data
                        
                        extratoCalendar.get(Calendar.YEAR) == dataCalendar.get(Calendar.YEAR) &&
                        extratoCalendar.get(Calendar.DAY_OF_YEAR) == dataCalendar.get(Calendar.DAY_OF_YEAR) &&
                        extrato.tipo == TipoTransacao.CREDITO
                    }.sumOf { it.valor }.toFloat()
                    
                    val saidasDia = extratos.filter { extrato ->
                        val extratoCalendar = Calendar.getInstance()
                        extratoCalendar.time = extrato.data.toDate()
                        
                        val dataCalendar = Calendar.getInstance()
                        dataCalendar.time = data
                        
                        extratoCalendar.get(Calendar.YEAR) == dataCalendar.get(Calendar.YEAR) &&
                        extratoCalendar.get(Calendar.DAY_OF_YEAR) == dataCalendar.get(Calendar.DAY_OF_YEAR) &&
                        extrato.tipo == TipoTransacao.DEBITO
                    }.sumOf { it.valor }.toFloat()
                    
                    dadosEntradas.add(entradasDia)
                    dadosSaidas.add(saidasDia)
                }
                
                val maxValor = maxOf(
                    dadosEntradas.maxOrNull() ?: 0f,
                    dadosSaidas.maxOrNull() ?: 0f
                ).coerceAtLeast(100f)
                
                // Área do gráfico
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val padding = 40.dp.toPx()
                    
                    // Desenhar linhas de grade horizontais
                    val linhasGrade = 4
                    for (i in 0..linhasGrade) {
                        val y = padding + (height - 2 * padding) * i / linhasGrade
                        drawLine(
                            color = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.3f),
                            start = Offset(padding, y),
                            end = Offset(width - padding, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(5f, 5f)
                            )
                        )
                    }
                    
                    val espacamento = (width - 2 * padding) / (diasLabels.size - 1)
                    
                    // Desenhar linha das entradas (verde)
                    if (dadosEntradas.isNotEmpty()) {
                        val pathEntradas = Path()
                        for (i in dadosEntradas.indices) {
                            val x = padding + i * espacamento
                            val y = height - padding - (dadosEntradas[i] / maxValor) * (height - 2 * padding)
                            
                            if (i == 0) {
                                pathEntradas.moveTo(x, y)
                            } else {
                                pathEntradas.lineTo(x, y)
                            }
                        }
                        
                        drawPath(
                            path = pathEntradas,
                            color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                        
                        // Pontos nas entradas
                        for (i in dadosEntradas.indices) {
                            val x = padding + i * espacamento
                            val y = height - padding - (dadosEntradas[i] / maxValor) * (height - 2 * padding)
                            drawCircle(
                                color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }
                    
                    // Desenhar linha das saídas (vermelho)
                    if (dadosSaidas.isNotEmpty()) {
                        val pathSaidas = Path()
                        for (i in dadosSaidas.indices) {
                            val x = padding + i * espacamento
                            val y = height - padding - (dadosSaidas[i] / maxValor) * (height - 2 * padding)
                            
                            if (i == 0) {
                                pathSaidas.moveTo(x, y)
                            } else {
                                pathSaidas.lineTo(x, y)
                            }
                        }
                        
                        drawPath(
                            path = pathSaidas,
                            color = androidx.compose.ui.graphics.Color(0xFFF44336),
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                        
                        // Pontos nas saídas
                        for (i in dadosSaidas.indices) {
                            val x = padding + i * espacamento
                            val y = height - padding - (dadosSaidas[i] / maxValor) * (height - 2 * padding)
                            drawCircle(
                                color = androidx.compose.ui.graphics.Color(0xFFF44336),
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Labels das datas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    diasLabels.forEach { dia ->
                        Text(
                            text = dia,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Legenda
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Entradas
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    Color(0xFF4CAF50),
                                    androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Entradas",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Saídas
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    Color(0xFFF44336),
                                    androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Saídas",
                            color = Color.Gray,
                            fontSize = 12.sp
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
    formatoData: SimpleDateFormat,
    onExcluir: (String) -> Unit = {} // Novo parâmetro
)  {
    var mostrarDialogo by remember { mutableStateOf(false) }
    
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
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (extrato.tipo == TipoTransacao.CREDITO) "+" else "-"}${formatoMoeda.format(extrato.valor)}",
                    color = cor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Botão de excluir com borda mínima
                Box(
                    modifier = Modifier
                        .size(18.dp) // Tamanho total muito pequeno
                        .background(
                            Color(0xFFFF1744).copy(alpha = 0.15f),
                            RoundedCornerShape(9.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Remove o ripple effect
                        ) { 
                            mostrarDialogo = true 
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir transação",
                        tint = Color(0xFFFF1744),
                        modifier = Modifier.size(14.dp) // Ícone bem pequeno
                    )
                }
            }
        }
    }
    
    // Diálogo de confirmação
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = {
                Text(
                    text = "Confirmar Exclusão",
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Tem certeza que deseja excluir a transação \"${extrato.titulo}\"?",
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onExcluir(extrato.id)
                        mostrarDialogo = false
                    }
                ) {
                    Text(
                        text = "Excluir",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogo = false }
                ) {
                    Text(
                        text = "Cancelar",
                        color = Color.Gray
                    )
                }
            },
            containerColor = Color(0xFF2A2A2A)
        )
    }
}