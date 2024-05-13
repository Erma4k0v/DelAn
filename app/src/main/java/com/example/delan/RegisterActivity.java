package com.example.delan;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextLogin, editTextPass, editTextPassConf;
    Button buttonReg;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextLogin = findViewById(R.id.usernameEditText);
        editTextPass = findViewById(R.id.password);
        editTextPassConf = findViewById(R.id.passwordConfirm);
        buttonReg = findViewById(R.id.register_btn);
        mAuth = FirebaseAuth.getInstance();

        buttonReg.setOnClickListener(v -> {
            String login, pass, passConf;
            login = Objects.requireNonNull(editTextLogin.getText()).toString();
            pass = Objects.requireNonNull(editTextPass.getText()).toString();
            passConf = Objects.requireNonNull(editTextPassConf.getText()).toString();
            if (pass.equals(passConf)) {
                mAuth.createUserWithEmailAndPassword(login, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(RegisterActivity.this, "Вы успешно зарегистрировались",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Регистрация провалена",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
            else Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
