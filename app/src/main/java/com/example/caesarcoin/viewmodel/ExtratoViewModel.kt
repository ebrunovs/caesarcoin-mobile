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

    fun carregarExtratos(usuarioId: String) {
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
                    carregarExtratos(usuarioId)
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
                    carregarExtratos(usuarioId)
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
}
