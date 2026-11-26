
package com.example.materna.session

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SessionManager(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("materna_session", Context.MODE_PRIVATE)

    private val alias = "materna_jwt_key"

    private fun obterChave(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        val chaveExistente = keyStore.getKey(alias, null) as? SecretKey

        if (chaveExistente != null) {
            return chaveExistente
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)

        return keyGenerator.generateKey()
    }

    fun salvarToken(token: String): Boolean {
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, obterChave())

            val tokenCriptografado = cipher.doFinal(
                token.toByteArray(Charsets.UTF_8)
            )

            val iv = Base64.encodeToString(
                cipher.iv,
                Base64.NO_WRAP
            )

            val dados = Base64.encodeToString(
                tokenCriptografado,
                Base64.NO_WRAP
            )

            preferences.edit()
                .putString("token", dados)
                .putString("iv", iv)
                .commit()
        } catch (e: Exception) {
            limparSessao()
            false
        }
    }

    fun obterToken(): String? {
        return try {
            val dados = preferences.getString("token", null)
                ?: return null

            val iv = preferences.getString("iv", null)
                ?: return null

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")

            cipher.init(
                Cipher.DECRYPT_MODE,
                obterChave(),
                GCMParameterSpec(
                    128,
                    Base64.decode(iv, Base64.NO_WRAP)
                )
            )

            String(
                cipher.doFinal(
                    Base64.decode(dados, Base64.NO_WRAP)
                ),
                Charsets.UTF_8
            )
        } catch (e: Exception) {
            limparSessao()
            null
        }
    }

    fun tokenValido(): Boolean {
        val token = obterToken() ?: return false

        return try {
            val partes = token.split(".")
            if (partes.size != 3) return false

            val payload = String(
                Base64.decode(
                    partes[1],
                    Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
                ),
                Charsets.UTF_8
            )

            val expiracao = JSONObject(payload).getLong("exp")
            val agora = System.currentTimeMillis() / 1000

            expiracao > agora
        } catch (e: Exception) {
            false
        }
    }

    fun limparSessao(): Boolean {
        return preferences.edit().clear().commit()
    }
}
