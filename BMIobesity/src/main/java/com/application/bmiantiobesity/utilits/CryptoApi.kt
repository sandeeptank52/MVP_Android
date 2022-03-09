package com.application.bmiantiobesity.utilits

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

class CryptoApi(private val context: Context) {

    companion object{
        const val KEY_STORE = "AndroidKeyStore"
        const val KEY_ALIAS = "InTimeAlias"
    }

    private val keyStore:KeyStore = KeyStore.getInstance(KEY_STORE)

    init {
        //Получение ключей
        keyStore.load(null)
        createNewKeyIfNotExist(keyStore)
    }


    // Функция кодирования
    fun encryptString(string: String): String{
        if (string.isNotEmpty())
            try {
                val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
                val publicKey = privateKeyEntry.certificate.publicKey as RSAPublicKey

                val cipher = getCipher()
                cipher.init(Cipher.ENCRYPT_MODE, publicKey)

                val outputStream = ByteArrayOutputStream()
                val cipherOutputStream = CipherOutputStream(outputStream, cipher)
                cipherOutputStream.write(string.toByteArray())
                cipherOutputStream.close()

                val byteArray = outputStream.toByteArray()
                //Log.d("Crypt into -", data.toString())

                // Generate Special String for Save Shared Preference
                val stringBuilder = StringBuilder()
                byteArray.forEach { stringBuilder.append(it).append(" ") }

                return stringBuilder.toString() //...toString(Charset.forName("UTF-8"))

            } catch (e:Exception){
                Log.d("Error Encrypt", e.message ?: "")
                return ""
            }
        else return ""
    }

    // Функция декодирования
    fun decryptString(codeString: String): String{
        if (codeString.isNotEmpty())
            try {
                val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
                val privateKey = privateKeyEntry.privateKey //as RSAPrivateKey

                val cipher = getCipher()
                cipher.init(Cipher.DECRYPT_MODE, privateKey)

                //Log.d("DeCrypt", byteArray.toString())

                // Convert to ByteArray special string from Shared Preference
                val arraysString = codeString.split(" ")
                val size = arraysString.size - 1
                val byteArray = ByteArray(size)
                for (i in 0 until size) byteArray[i] = arraysString[i].toByte()


                // Decode
                val cipherInputStream = CipherInputStream(ByteArrayInputStream(byteArray), cipher)
                val value = ArrayList<Byte>()

                var nextByte : Int
                do {
                    nextByte = cipherInputStream.read()
                    if (nextByte != -1) value.add(nextByte.toByte())
                } while (nextByte != -1)

                val  decryptedBytes = value.toByteArray()

                return String(decryptedBytes, Charset.forName("UTF-8"))

            } catch (e:Exception){
                Log.d("Error DeEncrypt", e.message ?: "")
                return ""
            }
        else return ""
    }

    // Генерация пары ключей и их сохранение если ещё не существует
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("DEPRECATION")
    private fun createNewKeyIfNotExist(keystore: KeyStore){
        try {
            if (!keystore.containsAlias(KEY_ALIAS)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    val start = Calendar.getInstance()
                    val end = Calendar.getInstance()
                    end.add(Calendar.YEAR, 10)
                    val keyPairGeneratorSpec =
                        KeyPairGeneratorSpec.Builder(context)
                            .setAlias(KEY_ALIAS)
                            .setSubject(X500Principal("CN=InTimeLoginKeys, O=InTimeCorp"))
                            .setSerialNumber(BigInteger.ONE)
                            .setStartDate(start.time)
                            .setEndDate(end.time)
                            .build()
                    val keyPairGenerator = KeyPairGenerator.getInstance("RSA",
                        KEY_STORE
                    )
                    keyPairGenerator.initialize(keyPairGeneratorSpec)
                    keyPairGenerator.generateKeyPair()
                } else {
                    val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA,
                        KEY_STORE
                    )
                    keyPairGenerator.initialize(
                        KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_DECRYPT)
                            .setDigests(KeyProperties.DIGEST_SHA256) //, KeyProperties.DIGEST_SHA512)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .build())
                    keyPairGenerator.generateKeyPair()
                }
            }
        }catch (e:Exception){
            Log.d("Error KEY Generate", e.message ?: "")
        }
    }

    private fun getCipher(): Cipher {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
                Cipher.getInstance("RSA/ECB/PKCS1Padding","AndroidOpenSSL")
                // error in android 6: InvalidKeyException: Need RSA private or public key
            } else { // android m and above
                Cipher.getInstance("RSA/ECB/PKCS1Padding","AndroidKeyStoreBCWorkaround")
                // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
            }
        } catch (exception: Exception) {
            throw RuntimeException("getCipher: Failed to get an instance of Cipher", exception)
        }
    }

}