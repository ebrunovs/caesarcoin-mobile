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
                result.documents.first().toObject<Usuario>()
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
            
            result.toObject<Usuario>()
        } catch (e: Exception) {
            println("UsuarioDao", "Erro ao buscar usuário por ID: ${e.message}")
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
            println("UsuarioDao", "Erro ao listar usuários: ${e.message}")
            emptyList()
        }
    }

    suspend fun atualizarUsuario(usuario: Usuario): Boolean {
        return try {
            if (usuario.id.isNotEmpty()) {
                db.collection(collection)
                    .document(usuario.id)
                    .set(usuario)
                    .addOnSuccessListener {
                        Log.d("UsuarioDao", "Usuário atualizado com sucesso!")
                    }
                    .addOnFailureListener { e ->
                        println("UsuarioDao", "Erro ao atualizar usuário: $e")
                    }.await()
                true
            } else {
                println("UsuarioDao", "ID do usuário está vazio")
                false
            }
        } catch (e: Exception) {
            println("UsuarioDao", "Erro ao atualizar usuário: ${e.message}")
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
                        Log.d("UsuarioDao", "Usuário removido com sucesso!")
                    }
                    .addOnFailureListener { e ->
                        println("UsuarioDao", "Erro ao remover usuário: $e")
                    }.await()
                true
            } else {
                println("UsuarioDao", "ID do usuário está vazio")
                false
            }
        } catch (e: Exception) {
            println("UsuarioDao", "Erro ao remover usuário: ${e.message}")
            false
        }
    }
}
