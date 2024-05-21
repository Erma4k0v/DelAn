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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText email, pass, passConf;
    Spinner userTypeSpinner;
    Button buttonReg;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.usernameEditText);
        pass = findViewById(R.id.password);
        passConf = findViewById(R.id.passwordConfirm);
        userTypeSpinner = findViewById(R.id.spinner);
        buttonReg = findViewById(R.id.register_btn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonReg.setOnClickListener(v -> {
            String email = Objects.requireNonNull(this.email.getText()).toString();
            String pass = Objects.requireNonNull(this.pass.getText()).toString();
            String passConf = Objects.requireNonNull(this.passConf.getText()).toString();
            String userType = userTypeSpinner.getSelectedItem().toString();

            if (email.isEmpty() || pass.isEmpty() || passConf.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(passConf)) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Регистрация успешна, получаем UID пользователя
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        // Создаем объект данных пользователя
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("uid", uid);
                        userData.put("email", email);
                        userData.put("role", userType);

                        // Сохраняем данные в Firestore
                        db.collection("users").document(uid).set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    // Данные успешно сохранены
                                    Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Ошибка при сохранении данных
                                    Toast.makeText(this, "Ошибка при сохранении данных", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    // Ошибка при регистрации
                    Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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
