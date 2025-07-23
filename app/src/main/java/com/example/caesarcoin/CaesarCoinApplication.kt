package com.example.caesarcoin

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class CaesarCoinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("CaesarCoinApp", "=== Inicializando Aplicação ===")

        try {
            // Verificar se Firebase já foi inicializado
            val firebaseApp = if (FirebaseApp.getApps(this).isEmpty()) {
                Log.d("CaesarCoinApp", "Inicializando Firebase...")
                FirebaseApp.initializeApp(this)
            } else {
                Log.d("CaesarCoinApp", "Firebase já inicializado")
                FirebaseApp.getInstance()
            }

            if (firebaseApp != null) {
                Log.d("CaesarCoinApp", "✅ Firebase inicializado: ${firebaseApp.name}")

                // Configurar Firestore
                val firestore = FirebaseFirestore.getInstance()
                val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()
                firestore.firestoreSettings = settings

                Log.d("CaesarCoinApp", "✅ Firestore configurado com cache persistente")
                Log.d("CaesarCoinApp", "✅ Projeto ID: ${firebaseApp.options.projectId}")
                Log.d("CaesarCoinApp", "✅ App ID: ${firebaseApp.options.applicationId}")
            } else {
                Log.e("CaesarCoinApp", "❌ Falha ao inicializar Firebase")
            }

            Log.d("CaesarCoinApp", "=== Configuração concluída ===")

        } catch (e: Exception) {
            Log.e("CaesarCoinApp", "❌ Erro ao configurar Firebase: ${e.message}", e)
            Log.e("CaesarCoinApp", "Stacktrace: ", e)
        }
    }
}