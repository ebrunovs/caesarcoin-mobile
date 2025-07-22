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
        onDebugMessage = { message -> addDebugMessage(message) }
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
                // Verificar se email já existe
                val usuarioExistente = usuarioDao.buscarUsuarioPorEmail(usuario.email)
                if (usuarioExistente != null) {
                    _erro.value = "Este email já está cadastrado"
                    _carregando.value = false
                    return@launch
                }

                // Salvar novo usuário
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
        
        addDebugMessage("🔄 Iniciando atualização do perfil...")
        addDebugMessage("👤 Usuário atual ID: ${usuarioAtual.id}")
        addDebugMessage("📝 Nome: '$novoNome'")
        addDebugMessage("👨 Sobrenome: '$novoSobrenome'") 
        addDebugMessage("📧 Email: '$novoEmail'")
        addDebugMessage("🔒 Senha: '${if(novaSenha.isBlank()) "VAZIA" else "DEFINIDA (${novaSenha.length} chars)"}'")

        if (novoNome.isBlank() || novoEmail.isBlank()) {
            addDebugMessage("❌ Erro: Nome e email são obrigatórios")
            _erro.value = "Nome e email são obrigatórios"
            return
        }

        _carregando.value = true
        _erro.value = null

        try {
            addDebugMessage("⏳ Criando usuário atualizado...")
            val usuarioAtualizado = usuarioAtual.copy(
                nome = novoNome,
                apelido = novoSobrenome,
                email = novoEmail,
                senha = novaSenha
            ).apply {
                id = usuarioAtual.id // CRÍTICO: Preservar o ID após o copy
            }
            
            addDebugMessage("📤 Enviando para UsuarioDao...")
            addDebugMessage("🆔 ID que será usado: '${usuarioAtualizado.id}'")

            val sucesso = usuarioDao.atualizarUsuario(usuarioAtualizado)
            if (sucesso) {
                addDebugMessage("✅ Sucesso! Atualizando estado local...")
                _usuarioLogado.value = usuarioAtualizado
                _erro.value = null
            } else {
                addDebugMessage("❌ UsuarioDao retornou FALSE")
                _erro.value = "Erro ao atualizar perfil"
            }
        } catch (e: Exception) {
            addDebugMessage("💥 EXCEÇÃO: ${e.message}")
            addDebugMessage("🔍 Tipo: ${e.javaClass.simpleName}")
            _erro.value = "Erro ao atualizar perfil: ${e.message}"
        } finally {
            _carregando.value = false
            addDebugMessage("🏁 Processo finalizado")
        }
    }
}
