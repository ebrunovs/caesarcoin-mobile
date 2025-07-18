package com.example.caesarcoin.model

import com.google.firebase.firestore.DocumentId

data class Usuario(
    var nome: String = "",
    var apelido: String = "",
    var email: String = "",
    var senha: String = ""
) {
    @DocumentId
    var id: String = ""
    
    constructor() : this("", "", "", "")
}