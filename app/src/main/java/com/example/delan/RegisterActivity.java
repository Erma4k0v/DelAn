package com.example.delan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextLogin, editTextPass, editTextPassConf;
    Spinner userTypeSpinner;
    Button buttonReg;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextLogin = findViewById(R.id.usernameEditText);
        editTextPass = findViewById(R.id.password);
        editTextPassConf = findViewById(R.id.passwordConfirm);
        userTypeSpinner = findViewById(R.id.spinner);
        buttonReg = findViewById(R.id.register_btn);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonReg.setOnClickListener(v -> {
            String login, pass, passConf;
            login = Objects.requireNonNull(editTextLogin.getText()).toString();
            pass = Objects.requireNonNull(editTextPass.getText()).toString();
            passConf = Objects.requireNonNull(editTextPassConf.getText()).toString();
            String userType = userTypeSpinner.getSelectedItem().toString();

            if (pass.equals(passConf)) {
                mAuth.createUserWithEmailAndPassword(login, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                String uid = user.getUid();

                                // Сохранение роли в Firestore
                                db.collection("users").document(uid)
                                        .set(new User(login, userType))
                                        .addOnSuccessListener(aVoid -> {
                                            Intent intent = new Intent(this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(RegisterActivity.this, "Вы успешно зарегистрировались", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show());

                            } else {
                                Toast.makeText(RegisterActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    static class User {
        String email;
        String role;

        User(String email, String role) {
            this.email = email;
            this.role = role;
        }
    }
}
