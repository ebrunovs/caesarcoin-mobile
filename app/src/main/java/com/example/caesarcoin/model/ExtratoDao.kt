package com.example.caesarcoin.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ExtratoDao {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("transacoes")

    suspend fun testarConexao(): Boolean {
        return try {
            firestore.disableNetwork().await()
            firestore.enableNetwork().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun adicionarTransacao(extrato: Extrato): Boolean {
        return try {
            val dados = hashMapOf(
                "titulo" to extrato.titulo,
                "descricao" to extrato.descricao,
                "valor" to extrato.valor,
                "tipo" to extrato.tipo.name,
                "data" to extrato.data,
                "usuarioId" to extrato.usuarioId
            )
            
            collection.add(dados).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun buscarExtratosPorUsuario(usuarioId: String): List<Extrato> {
        return try {
            val result = collection
                .whereEqualTo("usuarioId", usuarioId)
                .get()
                .await()

            val extratos = result.documents.mapNotNull { doc ->
                try {
                    val titulo = doc.getString("titulo") ?: ""
                    val descricao = doc.getString("descricao") ?: ""
                    val valor = doc.getDouble("valor") ?: doc.getLong("valor")?.toDouble() ?: 0.0
                    val tipoStr = doc.getString("tipo") ?: "DEBITO"
                    val usuarioIdDoc = doc.getString("usuarioId") ?: ""
                    val data = doc.getTimestamp("data") ?: com.google.firebase.Timestamp.now()
                    
                    val tipo = try {
                        TipoTransacao.valueOf(tipoStr)
                    } catch (e: Exception) {
                        TipoTransacao.DEBITO
                    }
                    
                    Extrato(
                        id = doc.id,
                        titulo = titulo,
                        descricao = descricao,
                        valor = valor,
                        tipo = tipo,
                        data = data,
                        usuarioId = usuarioIdDoc
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            extratos
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun excluirTransacao(extratoId: String): Boolean {
        return try {
            collection.document(extratoId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun calcularSaldo(extratos: List<Extrato>): Double {
        return extratos.sumOf { extrato ->
            when (extrato.tipo) {
                TipoTransacao.CREDITO -> extrato.valor
                TipoTransacao.DEBITO -> -extrato.valor
            }
        }
    }

    fun calcularTotalCreditos(extratos: List<Extrato>): Double {
        return extratos.filter { it.tipo == TipoTransacao.CREDITO }.sumOf { it.valor }
    }

    fun calcularTotalDebitos(extratos: List<Extrato>): Double {
        return extratos.filter { it.tipo == TipoTransacao.DEBITO }.sumOf { it.valor }
    }
}
