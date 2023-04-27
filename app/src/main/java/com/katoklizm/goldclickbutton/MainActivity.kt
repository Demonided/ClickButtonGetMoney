package com.katoklizm.goldclickbutton

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextClock
import androidx.appcompat.app.AlertDialog
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
    var root: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        users = db?.getReference("Users")

        binding.btnRegister.setOnClickListener {
            showRegisterWindow()
        }
    }

    fun  showRegisterWindow() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle("Зарегистрироваться")
        dialog.setMessage("Введите все данные для регистрации")

        val inflater: LayoutInflater = LayoutInflater.from(this)
        val register_window: View = inflater.inflate(R.layout.register_window, null)
        dialog.setView(register_window)

        val emailRegister: EditText = register_window.findViewById(R.id.txtEmail_register)
        val namePlayerRegister: EditText = register_window.findViewById(R.id.txtNamePlayer_register)
        val passwordRegister: EditText = register_window.findViewById(R.id.txtPassword_register)
        val telephoneRegister: EditText = register_window.findViewById(R.id.txtTelephone_register)

        dialog.setNegativeButton("Отменить", DialogInterface.OnClickListener() {
                dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        })

        dialog.setNegativeButton("Добавить", DialogInterface.OnClickListener() {
                dialogInterface: DialogInterface, i: Int ->
            if (TextUtils.isEmpty(emailRegister.text.toString())) {
                Snackbar.make(binding.root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (TextUtils.isEmpty(namePlayerRegister.text.toString())) {
                Snackbar.make(binding.root, "Введите ваше имя", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (passwordRegister.text.toString().length < 5) {
                Snackbar.make(binding.root, "Пароль должен содержать не меньше 6 символов",
                    Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (TextUtils.isEmpty(telephoneRegister.text.toString())) {
                Snackbar.make(binding.root, "Введите ваш номер телефона", Snackbar.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            // Регистрация пользователя
            auth!!.createUserWithEmailAndPassword(emailRegister.text.toString(),
                passwordRegister.text.toString())
                .addOnSuccessListener {
                    val user = User()
                    user.email.toString()
                    user.pass.toString()
                    user.phone.toString()
                    user.name.toString()

                    user.email?.let { it1 -> users?.child(it1)?.setValue(user) }
                        ?.addOnSuccessListener {
                            Snackbar.make(binding.root, "Пользователь добавлен",
                                Snackbar.LENGTH_SHORT)
                                .show()
                        }
                }
        })

        dialog.show()
    }



}