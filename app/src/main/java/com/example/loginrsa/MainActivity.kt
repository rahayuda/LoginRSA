package com.example.loginrsa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.Security
import javax.crypto.Cipher

class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var encryptedPasswordTextView: TextView
    private lateinit var loginStatusTextView: TextView // Tambahkan untuk status login

    // Password terenkripsi yang benar
    private val correctUsername = "android"
    private val correctPassword = "Enkr1ps1" // Password yang benar
    private lateinit var keyPair: KeyPair // Menyimpan pasangan kunci RSA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Security.addProvider(BouncyCastleProvider())

        // Menghasilkan pasangan kunci RSA sekali
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        keyPair = keyPairGenerator.generateKeyPair()

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        encryptedPasswordTextView = findViewById(R.id.encryptedPasswordTextView)
        loginStatusTextView = findViewById(R.id.loginStatusTextView) // Inisialisasi

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val encryptedPassword = encryptPassword(password)
                // Tampilkan hasil enkripsi di TextView
                encryptedPasswordTextView.text = encryptedPassword.joinToString(", ") { it.toString() }

                // Cek apakah username dan password yang dimasukkan benar
                if (username == correctUsername && isPasswordCorrect(encryptedPassword)) {
                    loginStatusTextView.text = "Login berhasil untuk $username" // Status login berhasil
                    Toast.makeText(this, "Login berhasil untuk $username", Toast.LENGTH_SHORT).show()
                } else {
                    loginStatusTextView.text = "Username atau password salah" // Status login gagal
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Silakan isi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun encryptPassword(password: String): ByteArray {
        val publicKey: PublicKey = keyPair.public

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        return cipher.doFinal(password.toByteArray())
    }

    private fun isPasswordCorrect(encryptedPassword: ByteArray): Boolean {
        // Enkripsi password yang benar sekali saja menggunakan kunci yang sama
        val correctEncryptedPassword = encryptPassword(correctPassword)
        return encryptedPassword.contentEquals(correctEncryptedPassword)
    }
}
