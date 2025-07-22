package com.example.caesarcoin.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caesarcoin.model.Usuario
import com.example.caesarcoin.model.UsuarioDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val usuarioDao = UsuarioDao().apply {
    }

    private val _usuarioLogado = MutableStateFlow<Usuario?>(null)
    val usuarioLogado: StateFlow<Usuario?> = _usuarioLogado

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando

    private val _diagnostico = MutableStateFlow<String?>(null)
    val diagnostico: StateFlow<String?> = _diagnostico
    
    private val _debugMessages = MutableStateFlow<List<String>>(emptyList())
    val debugMessages: StateFlow<List<String>> = _debugMessages
    
    private fun addDebugMessage(message: String) {
        val timestamp = System.currentTimeMillis() % 100000
        val newMessage = "[$timestamp] $message"
        _debugMessages.value = (_debugMessages.value + newMessage).takeLast(10)
    }

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _erro.value = "Email e senha são obrigatórios"
            return
        }

        _carregando.value = true
        _erro.value = null

        viewModelScope.launch {
            try {
                val usuario = usuarioDao.buscarUsuarioPorEmail(email)
                if (usuario != null && usuario.senha == senha) {
                    _usuarioLogado.value = usuario
                    _erro.value = null
                } else {
                    _erro.value = "Email ou senha incorretos"
                }
            } catch (e: Exception) {
                _erro.value = "Erro ao fazer login: ${e.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    fun cadastrar(usuario: Usuario) {
        if (usuario.nome.isBlank() || usuario.email.isBlank() || usuario.senha.isBlank()) {
            _erro.value = "Todos os campos são obrigatórios"
            return
        }

        if (usuario.senha.length < 6) {
            _erro.value = "A senha deve ter pelo menos 6 caracteres"
            return
        }

        _carregando.value = true
        _erro.value = null

        viewModelScope.launch {
            try {
                val usuarioExistente = usuarioDao.buscarUsuarioPorEmail(usuario.email)
                if (usuarioExistente != null) {
                    _erro.value = "Este email já está cadastrado"
                    _carregando.value = false
                    return@launch
                }

                val sucesso = usuarioDao.adicionarUsuario(usuario)
                if (sucesso) {
                    _usuarioLogado.value = usuario
                    _erro.value = null
                } else {
                    _erro.value = "Erro ao cadastrar usuário"
                }
            } catch (e: Exception) {
                _erro.value = "Erro ao cadastrar: ${e.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    fun logout() {
        _usuarioLogado.value = null
        _erro.value = null
    }

    fun limparErro() {
        _erro.value = null
    }
    
    suspend fun atualizarPerfil(novoNome: String, novoSobrenome: String, novoEmail: String, novaSenha: String) {
        val usuarioAtual = _usuarioLogado.value ?: return

        if (novoNome.isBlank() || novoEmail.isBlank()) {
            _erro.value = "Nome e email são obrigatórios"
            return
        }

        _carregando.value = true
        _erro.value = null

        try {
            val usuarioAtualizado = usuarioAtual.copy(
                nome = novoNome,
                apelido = novoSobrenome,
                email = novoEmail,
                senha = novaSenha
            ).apply {
                id = usuarioAtual.id 
            }
            

            val sucesso = usuarioDao.atualizarUsuario(usuarioAtualizado)
            if (sucesso) {
                _usuarioLogado.value = usuarioAtualizado
                _erro.value = null
            } else {
                _erro.value = "Erro ao atualizar perfil"
            }
        } catch (e: Exception) {
            _erro.value = "Erro ao atualizar perfil: ${e.message}"
        } finally {
            _carregando.value = false
        }
    }
}
