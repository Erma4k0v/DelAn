package com.example.delan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText email, pass;
    Button reg_btn, login_btn;
    private FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        reg_btn = findViewById(R.id.register_btn);
        login_btn = findViewById(R.id.login_btn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        reg_btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        login_btn.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = pass.getText().toString().trim();

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                db.collection("users").document(uid).get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot document = task1.getResult();
                                                if (document.exists()) {
                                                    String role = document.getString("role");
                                                    switch (role) {
                                                        case "Поставщик":
                                                            startActivity(new Intent(this, SupplierActivity.class));
                                                            break;
                                                        case "Заказчик":
                                                            startActivity(new Intent(this, CustomerActivity.class));
                                                            break;
                                                        case "Курьер":
                                                            startActivity(new Intent(this, CourierActivity.class));
                                                            break;
                                                    }
                                                    finish();
                                                } else {
                                                    Toast.makeText(this, "Документ не найден", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Ошибка при аутентификации
                            Toast.makeText(this, "Ошибка аутентификации", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot document = task1.getResult();
                            if (document.exists()) {
                                String role = document.getString("role");
                                switch (role) {
                                    case "Поставщик":
                                        startActivity(new Intent(this, SupplierActivity.class));
                                        break;
                                    case "Заказчик":
                                        startActivity(new Intent(this, CustomerActivity.class));
                                        break;
                                    case "Курьер":
                                        startActivity(new Intent(this, CourierActivity.class));
                                        break;
                                }
                                finish();
                            } else {
                                Toast.makeText(this, "Документ не найден", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
