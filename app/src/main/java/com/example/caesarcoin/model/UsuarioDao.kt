package com.example.caesarcoin.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class UsuarioDao {
    private val db = FirebaseFirestore.getInstance()
    private val collection = "usuarios"
    
    // Callback para debug visual
    var onDebugMessage: ((String) -> Unit)? = null

    suspend fun adicionarUsuario(usuario: Usuario): Boolean {
        return try {
            db.collection(collection)
                .add(usuario)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun buscarUsuarioPorEmail(email: String): Usuario? {
        return try {
            val result = db.collection(collection)
                .whereEqualTo("email", email)
                .get()
                .await()
            
            if (!result.isEmpty) {
                val documento = result.documents.first()
                val usuario = documento.toObject<Usuario>()
                usuario?.let {
                    it.id = documento.id // Garantir que o ID seja definido
                    it
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun buscarUsuarioPorId(id: String): Usuario? {
        return try {
            val result = db.collection(collection)
                .document(id)
                .get()
                .await()
            
            if (result.exists()) {
                val usuario = result.toObject<Usuario>()
                usuario?.let {
                    it.id = result.id // CRÍTICO: Garantir que o ID seja definido
                    onDebugMessage?.invoke("✅ DAO: Usuário encontrado por ID - Nome: ${it.nome}")
                    it
                }
            } else {
                onDebugMessage?.invoke("❌ DAO: Documento com ID $id não existe")
                null
            }
        } catch (e: Exception) {
            onDebugMessage?.invoke("💥 DAO: Erro ao buscar por ID: ${e.message}")
            null
        }
    }

    suspend fun listarUsuarios(): List<Usuario> {
        return try {
            val result = db.collection(collection)
                .get()
                .await()
            
            result.toObjects<Usuario>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun atualizarUsuario(usuario: Usuario): Boolean {
        return try {
            onDebugMessage?.invoke("🔍 DAO: Iniciando atualização...")
            onDebugMessage?.invoke("🆔 DAO: ID recebido: '${usuario.id}'")
            onDebugMessage?.invoke("📊 DAO: ID vazio? ${usuario.id.isEmpty()}")
            onDebugMessage?.invoke("📋 DAO: Nome='${usuario.nome}', Email='${usuario.email}'")
            
            if (usuario.id.isNotEmpty()) {
                val dadosAtualizacao = mapOf(
                    "nome" to usuario.nome,
                    "apelido" to usuario.apelido,
                    "email" to usuario.email,
                    "senha" to usuario.senha
                )
                
                onDebugMessage?.invoke("📤 DAO: Enviando para Firebase...")
                onDebugMessage?.invoke("🎯 DAO: Documento: usuarios/${usuario.id}")
                
                db.collection(collection)
                    .document(usuario.id)
                    .update(dadosAtualizacao)
                    .await()
                    
                onDebugMessage?.invoke("✅ DAO: Update() executado com sucesso!")
                
                // Verificar se realmente foi atualizado
                val documentoAtualizado = db.collection(collection)
                    .document(usuario.id)
                    .get()
                    .await()
                    
                if (documentoAtualizado.exists()) {
                    val usuarioVerificado = documentoAtualizado.toObject<Usuario>()
                    onDebugMessage?.invoke("🔍 DAO: Verificação OK - Nome: '${usuarioVerificado?.nome}'")
                    onDebugMessage?.invoke("📧 DAO: Verificação OK - Email: '${usuarioVerificado?.email}'")
                } else {
                    onDebugMessage?.invoke("❌ DAO: ERRO - Documento não existe!")
                    Log.e("UsuarioDao", "❌ ERRO: Documento não existe após atualização!")
                    return false
                }
                
                true
            } else {
                onDebugMessage?.invoke("❌ DAO: ERRO - ID está vazio!")
                Log.e("UsuarioDao", "❌ ERRO: ID do usuário está vazio!")
                false
            }
        } catch (e: Exception) {
            onDebugMessage?.invoke("💥 DAO: EXCEÇÃO - ${e.message}")
            onDebugMessage?.invoke("🔍 DAO: Tipo: ${e.javaClass.simpleName}")
            Log.e("UsuarioDao", "❌ EXCEÇÃO ao atualizar usuário: ${e.message}", e)
            false
        }
    }

    suspend fun removerUsuario(usuario: Usuario): Boolean {
        return try {
            if (usuario.id.isNotEmpty()) {
                db.collection(collection)
                    .document(usuario.id)
                    .delete()
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                    }.await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
