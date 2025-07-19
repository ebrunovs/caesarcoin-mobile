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
                db.collection(collection)
                    .document(usuario.id)
                    .set(usuario)
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
