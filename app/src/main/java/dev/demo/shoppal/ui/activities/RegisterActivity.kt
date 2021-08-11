package dev.demo.shoppal.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dev.demo.shoppal.R
import dev.demo.shoppal.firestore.FirestoreClass
import dev.demo.shoppal.models.User
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()

        lbl_login.setOnClickListener {
            onBackPressed()
        }
        btn_register.setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_confirm_password), true)
                false
            }
            et_password.text.toString().trim { it <= ' ' }
                    != et_confirm_password.text.toString().trim { it <= ' ' } -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_confirm_password), true)
                false
            }
            !chk_accept_terms.isChecked -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_accept_terms), true)
                false
            }
            else -> {
//                showErrorSnackbar("Thanks for registering", false)
                true
            }
        }
    }
    
    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email = et_email.text.toString().trim { it <= ' ' }
            val password = et_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result!!.user

                        saveUser(firebaseUser)

                        showErrorSnackbar(resources.getString(R.string.msg_register_successful),
                            false)

                    }else {
                        hideProgressDialog()
                        showErrorSnackbar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    private fun saveUser(firebaseUser: FirebaseUser?) {
        val user = User(
            firebaseUser!!.uid,
            et_first_name.text.toString().trim { it <= ' ' },
            et_last_name.text.toString().trim { it <= ' ' },
            et_email.text.toString().trim { it <= ' ' }
        )
        FirestoreClass().registerUser(this@RegisterActivity, user)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        finish()
    }
}