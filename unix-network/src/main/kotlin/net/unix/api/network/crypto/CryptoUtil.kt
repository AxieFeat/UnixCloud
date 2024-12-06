package net.unix.api.network.crypto

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.*
import javax.crypto.Cipher

@Suppress("unused")
object CryptoUtil {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA", "BC")
        keyGen.initialize(2048)
        return keyGen.generateKeyPair()
    }

    fun encrypt(data: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    fun decrypt(data: ByteArray, privateKey: PrivateKey): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(data)
    }
}