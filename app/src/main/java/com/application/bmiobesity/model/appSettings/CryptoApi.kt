package com.application.bmiobesity.model.appSettings

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import javax.crypto.Cipher

class CryptoApi private constructor(){

    private val androidStore = "AndroidKeyStore"
    private val intimeAlias = "intimeCrypto"
    private val keySize = 2048

    private val mKeyStore: KeyStore

    init {
        mKeyStore = KeyStore.getInstance(androidStore).apply { load(null) }
        if (!mKeyStore.containsAlias(intimeAlias)) initKey()
    }

    fun encryptString(src: String): String{
        val public = mKeyStore.getCertificate(intimeAlias).publicKey
        val encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, public)

        val srcSize = src.length
        val blockSize = encryptCipher.blockSize
        val srcByteArray = src.toByteArray()

        if (srcSize > blockSize){
            val inputStream = ByteArrayInputStream(srcByteArray)
            val outputStream = ByteArrayOutputStream()
            val outBuffer = ByteArray(blockSize)

            var readLength = inputStream.read(outBuffer, 0, blockSize)
            while (readLength != -1){
                if (readLength < blockSize){
                    val encryptPart = encryptCipher.doFinal(outBuffer, 0, readLength)
                    outputStream.write(encryptPart)
                } else {
                    val encryptPart = encryptCipher.doFinal(outBuffer)
                    outputStream.write(encryptPart)
                }
                readLength = inputStream.read(outBuffer, 0, blockSize)
            }

            val encryptByteArray = outputStream.toByteArray()
            inputStream.close()
            outputStream.close()
            return Base64.encodeToString(encryptByteArray, Base64.DEFAULT)

        } else {
            val encryptByteArray = encryptCipher.doFinal(srcByteArray)
            return Base64.encodeToString(encryptByteArray, Base64.DEFAULT)
        }
    }

    fun decryptString(src: String?): String{
        if (src.isNullOrEmpty()) return ""
        val private = mKeyStore.getKey(intimeAlias, null) as PrivateKey
        val decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        decryptCipher.init(Cipher.DECRYPT_MODE, private)

        val blockSize =  decryptCipher.getOutputSize(keySize)
        val encryptByteArray = Base64.decode(src, Base64.DEFAULT)

        if (encryptByteArray.size > blockSize){

            val inputStream = ByteArrayInputStream(encryptByteArray)
            val outputStream = ByteArrayOutputStream()
            val outBuffer = ByteArray(blockSize)

            var readLength = inputStream.read(outBuffer, 0, blockSize)
            while (readLength != -1){
                val decryptPart = decryptCipher.doFinal(outBuffer)
                outputStream.write(decryptPart)
                readLength = inputStream.read(outBuffer, 0, blockSize)
            }

            val decryptByteArray = outputStream.toByteArray()
            inputStream.close()
            outputStream.close()
            return decryptByteArray.decodeToString()

        } else {
            val decryptByteArray = decryptCipher.doFinal(encryptByteArray)
            return decryptByteArray.decodeToString()
        }
    }

    private fun initKey(){
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, androidStore)
        val paramSpec = KeyGenParameterSpec.Builder(
            intimeAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).run {
            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            setKeySize(keySize)
            setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            build()
        }
        kpg.initialize(paramSpec)
        kpg.generateKeyPair()
    }

    companion object{
        @Volatile
        private var INSTANCE: CryptoApi? = null

        fun getCryptoApi(): CryptoApi{
            return INSTANCE ?: synchronized(this){
                val instance = CryptoApi()
                INSTANCE = instance
                instance
            }
        }
    }
}