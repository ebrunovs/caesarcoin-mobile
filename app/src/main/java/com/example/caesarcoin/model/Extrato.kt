package com.example.caesarcoin.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Extrato(
    @DocumentId
    var id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val valor: Double = 0.0,
    val data: Timestamp = Timestamp.now(),
    val tipo: TipoTransacao = TipoTransacao.DEBITO,
    val usuarioId: String = ""
) {
    constructor() : this("", "", "", 0.0, Timestamp.now(), TipoTransacao.DEBITO, "")
}

enum class TipoTransacao {
    CREDITO,  
    DEBITO   
}
