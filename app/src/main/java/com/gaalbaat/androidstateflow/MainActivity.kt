package com.gaalbaat.androidstateflow

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.gaalbaat.androidstateflow.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val defaultButtonTintColor = "#1B1717"
    private val onFormValidButtonTintColor = "#4F774F"

    private lateinit var mBinding: ActivityMainBinding

    private var errorMessage: String? = null

    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")
    private val passwordAgain = MutableStateFlow("")

    private val formIsValid = combine(
        email,
        password,
        passwordAgain
    ) { email, password, passwordAgain ->
        mBinding.txtErrorMessage.text = ""
        val emailIsValid = email.length > 6
        val passwordIsValid = password.length in 7..15
        val passwordAgainIsValid = passwordAgain == password
        errorMessage = when {
            emailIsValid.not() -> "email not valid"
            passwordIsValid.not() -> "Password not valid"
            passwordAgainIsValid.not() -> "Passwords do not match"
            else -> null
        }
        errorMessage?.let {
            mBinding.txtErrorMessage.text = it
        }
        emailIsValid and passwordIsValid and passwordAgainIsValid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        // onCreate
        with(mBinding) {

            txtEmail.doOnTextChanged { text, _, _, _ ->
                email.value = text.toString()
            }
            txtPassword.doOnTextChanged { text, _, _, _ ->
                password.value = text.toString()
            }
            txtPasswordAgain.doOnTextChanged { text, _, _, _ ->
                passwordAgain.value = text.toString()
            }
        }

        val snackBar = Snackbar.make(mBinding.root, "Login Successfully", Snackbar.LENGTH_LONG)

        mBinding.btnLogin.setOnClickListener { snackBar.show() }

        lifecycleScope.launch {
            formIsValid.collect {
                mBinding.btnLogin.apply {
                    backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor(if (it) onFormValidButtonTintColor else defaultButtonTintColor))
                    isClickable = it
                }
            }
        }

    }
}