package com.example.caesarcoin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caesarcoin.model.Extrato
import com.example.caesarcoin.model.ExtratoDao
import com.example.caesarcoin.model.TipoTransacao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExtratoViewModel : ViewModel() {
    
    private val extratoDao = ExtratoDao()
    
    private val _extratos = MutableStateFlow<List<Extrato>>(emptyList())
    val extratos: StateFlow<List<Extrato>> = _extratos
    
    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando
    
    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro
    
    private val _totalCreditos = MutableStateFlow(0.0)
    val totalCreditos: StateFlow<Double> = _totalCreditos
    
    private val _totalDebitos = MutableStateFlow(0.0)
    val totalDebitos: StateFlow<Double> = _totalDebitos
    
    private val _saldoTotal = MutableStateFlow(0.0)
    val saldoTotal: StateFlow<Double> = _saldoTotal

    private var ultimoUsuarioCarregado: String? = null
    private var carregamentoInicial = false

    fun carregarExtratos(usuarioId: String, forcarReload: Boolean = false) {
        // Evita recarregamentos desnecessários
        if (!forcarReload && 
            ultimoUsuarioCarregado == usuarioId && 
            _extratos.value.isNotEmpty() && 
            carregamentoInicial) {
            return
        }
        
        ultimoUsuarioCarregado = usuarioId
        carregamentoInicial = true
        _carregando.value = true
        _erro.value = null
        
        viewModelScope.launch {
            try {
                val transacoesCarregadas = extratoDao.buscarExtratosPorUsuario(usuarioId)
                _extratos.value = transacoesCarregadas
                
                _totalCreditos.value = extratoDao.calcularTotalCreditos(transacoesCarregadas)
                _totalDebitos.value = extratoDao.calcularTotalDebitos(transacoesCarregadas)
                _saldoTotal.value = extratoDao.calcularSaldo(transacoesCarregadas)
                
            } catch (e: Exception) {
                _erro.value = "Erro ao carregar transações: ${e.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    fun adicionarTransacao(extrato: Extrato, usuarioId: String) {
        _carregando.value = true
        _erro.value = null
        
        viewModelScope.launch {
            try {
                val conexaoOk = extratoDao.testarConexao()
                if (!conexaoOk) {
                    _erro.value = "Problema de conexão com Firebase"
                    _carregando.value = false
                    return@launch
                }
                
                val extratoComUsuario = extrato.copy(usuarioId = usuarioId)
                val sucesso = extratoDao.adicionarTransacao(extratoComUsuario)
                
                if (sucesso) {
                    carregarExtratos(usuarioId, forcarReload = true) 
                } else {
                    _erro.value = "Falha ao salvar no Firebase"
                }
            } catch (e: Exception) {
                _erro.value = "Erro inesperado: ${e.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    fun excluirTransacao(extratoId: String, usuarioId: String) {
        _carregando.value = true
        _erro.value = null

        viewModelScope.launch {
            try {
                val sucesso = extratoDao.excluirTransacao(extratoId)

                if (sucesso) {
                    // Atualiza a lista local imediatamente
                    _extratos.value = _extratos.value.filter { it.id != extratoId }

                    // Recalcula os totais
                    _totalCreditos.value = extratoDao.calcularTotalCreditos(_extratos.value)
                    _totalDebitos.value = extratoDao.calcularTotalDebitos(_extratos.value)
                    _saldoTotal.value = extratoDao.calcularSaldo(_extratos.value)

                    // Força um recarregamento completo em seguida
                    carregarExtratos(usuarioId, forcarReload = true)
                } else {
                    _erro.value = "Erro ao excluir transação"
                }
            } catch (e: Exception) {
                _erro.value = "Erro ao excluir transação: ${e.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    fun limparErro() {
        _erro.value = null
    }

}
