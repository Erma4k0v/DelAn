package com.example.delan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    Button exit_btn;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        exit_btn = findViewById(R.id.exit_btn);
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            String uid = user.getUid();
            db.collection("users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String role = document.getString("role");
                        assert role != null;
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

        exit_btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
