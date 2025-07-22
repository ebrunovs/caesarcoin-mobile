package com.example.caesarcoin.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class UsuarioDao {
    private val db = FirebaseFirestore.getInstance()
    private val collection = "usuarios"
    

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
                    it.id = documento.id 
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
                    it.id = result.id
                    it
                }
            } else {
                null
            }
        } catch (e: Exception) {
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
            
            if (usuario.id.isNotEmpty()) {
                val dadosAtualizacao = mapOf(
                    "nome" to usuario.nome,
                    "apelido" to usuario.apelido,
                    "email" to usuario.email,
                    "senha" to usuario.senha
                )
                
                db.collection(collection)
                    .document(usuario.id)
                    .update(dadosAtualizacao)
                    .await()
                    
                val documentoAtualizado = db.collection(collection)
                    .document(usuario.id)
                    .get()
                    .await()
                    
                if (documentoAtualizado.exists()) {
                    val usuarioVerificado = documentoAtualizado.toObject<Usuario>()
                } else {
                    return false
                }
                
                true
            } else {
                false
            }
        } catch (e: Exception) {
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
