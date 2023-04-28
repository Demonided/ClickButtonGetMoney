package com.katoklizm.goldclickbutton

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextClock
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.katoklizm.goldclickbutton.databinding.ActivityMainBinding
import com.katoklizm.goldclickbutton.models.User

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var auth: FirebaseAuth? = null
    var db: FirebaseDatabase? = null
    var users: DatabaseReference? = null

    var rootRegister: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rootRegister = findViewById(R.id.liner_layout_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        users = db?.getReference("Users")

        binding.btnRegister.setOnClickListener { showRegisterWindow() }
        binding.btnSignIn.setOnClickListener { showSignInWindow() }
    }

    private fun showSignInWindow() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Войти")
        dialog.setMessage("Введите данные для входа")

        val inflater: LayoutInflater = LayoutInflater.from(this)
        val sign_in_window: View = inflater.inflate(R.layout.sign_in_window, null)
        dialog.setView(sign_in_window)

        val emailRegister: EditText = sign_in_window.findViewById(R.id.txtEmail_sign_in)
        val passwordRegister: EditText = sign_in_window.findViewById(R.id.txtPassword_sign_in)

        dialog.setNegativeButton("Отменить") {dialogInterface, i -> dialogInterface.dismiss() }

        dialog.setPositiveButton("Войти") { dialogInterface, i ->
            if (TextUtils.isEmpty(emailRegister.text.toString())) {
                Snackbar.make(binding.root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (passwordRegister.text.toString().length < 5) {
                Snackbar.make(binding.root, "Пароль должен содержать не меньше 6 символов",
                    Snackbar.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            auth?.signInWithEmailAndPassword(emailRegister.text.toString(),
                passwordRegister.text.toString())?.addOnSuccessListener {
                val intent = Intent(this, com.katoklizm.goldclickbutton
                    .StartGameWindowActivity::class.java)
                finish()
                startActivity(intent)
            }?.addOnFailureListener {
                Snackbar.make(binding.root, "Ошибка авторизации",
                    Snackbar.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    fun  showRegisterWindow() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Зарегистрироваться")
        dialog.setMessage("Введите все данные для регистрации")

        val inflater: LayoutInflater = LayoutInflater.from(this)
        val register_window: View = inflater.inflate(R.layout.register_window, null)
        dialog.setView(register_window)

        val emailRegister: EditText = register_window.findViewById(R.id.txtEmail_register)
        val namePlayerRegister: EditText = register_window.findViewById(R.id.txtNamePlayer_register)
        val passwordRegister: EditText = register_window.findViewById(R.id.txtPassword_register)
        val telephoneRegister: EditText = register_window.findViewById(R.id.txtTelephone_register)

        dialog.setNegativeButton("Отменить") {dialogInterface, i -> dialogInterface.dismiss() }

        dialog.setPositiveButton("Добавить") { dialogInterface, i ->
            if (TextUtils.isEmpty(emailRegister.text.toString())) {
                Snackbar.make(binding.root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (TextUtils.isEmpty(namePlayerRegister.text.toString())) {
                Snackbar.make(binding.root, "Введите ваше имя", Snackbar.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (passwordRegister.text.toString().length < 5) {
                Snackbar.make(binding.root, "Пароль должен содержать не меньше 6 символов",
                    Snackbar.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (TextUtils.isEmpty(telephoneRegister.text.toString())) {
                Snackbar.make(binding.root, "Введите ваш номер телефона", Snackbar.LENGTH_SHORT)
                    .show()
                return@setPositiveButton
            }

            // Регистрация пользователя
            auth?.createUserWithEmailAndPassword(emailRegister.text.toString(),
                passwordRegister.text.toString())
                ?.addOnSuccessListener {
                    val user = User()
                    user.email = emailRegister.text.toString()
                    user.pass = passwordRegister.text.toString()
                    user.phone = telephoneRegister.text.toString()
                    user.name = namePlayerRegister.text.toString()

                    users?.child(FirebaseAuth.getInstance().currentUser!!.uid)
                        ?.setValue(user)
                        ?.addOnSuccessListener {
                            Snackbar.make(binding.root, "Авторизация прошла успешно" + it,
                                Snackbar.LENGTH_SHORT).show()
                        }
                }
        }

        dialog.show()
    }
}