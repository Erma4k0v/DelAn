package com.example.delan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    Button exitBtn, updateBtn;
    FirebaseAuth auth;
    FirebaseFirestore db;
    TextView first_name, last_name, address, warehouse_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        address = findViewById(R.id.customer_address);
        warehouse_name = findViewById(R.id.warehouse_name);
        updateBtn = findViewById(R.id.update_button);
        exitBtn = findViewById(R.id.exit_btn);
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
                                        first_name.setVisibility(View.VISIBLE);
                                        last_name.setVisibility(View.VISIBLE);
                                        warehouse_name.setVisibility(View.VISIBLE);
                                        first_name.setText(document.getString("first_name"));
                                        last_name.setText(document.getString("last_name"));
                                        warehouse_name.setText(document.getString("warehouse_name"));
                                        break;
                                    case "Заказчик":
                                        address.setVisibility(View.VISIBLE);
                                        address.setText(document.getString("address"));
                                        break;
                                    case "Курьер":
                                        first_name.setVisibility(View.VISIBLE);
                                        last_name.setVisibility(View.VISIBLE);
                                        first_name.setText(document.getString("first_name"));
                                        last_name.setText(document.getString("last_name"));
                                        break;
                                }
                            } else {
                                Toast.makeText(this, "Документ не найден", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        updateBtn.setOnClickListener(v -> updateUserProfile());

        exitBtn.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });
    }

    private void updateUserProfile() {
        String userId = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> userUpdates = new HashMap<>();

        String addressText = address.getText().toString().trim();
        String firstNameText = first_name.getText().toString().trim();
        String lastNameText = last_name.getText().toString().trim();
        String warehouseNameText = warehouse_name.getText().toString().trim();

        if (!addressText.isEmpty()) {
            userUpdates.put("address", addressText);
        }
        if (!firstNameText.isEmpty()) {
            userUpdates.put("first_name", firstNameText);
        }
        if (!lastNameText.isEmpty()) {
            userUpdates.put("last_name", lastNameText);
        }
        if (!warehouseNameText.isEmpty()) {
            userUpdates.put("warehouse_name", warehouseNameText);
        }

        if (!userUpdates.isEmpty()) {
            userRef.update(userUpdates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ProfileActivity.this, "No fields to update", Toast.LENGTH_SHORT).show();
        }
    }

}
