package com.example.delan;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivityChoice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        Button supplierButton = findViewById(R.id.supplierButton);
        Button courierButton = findViewById(R.id.courierButton);
        Button userButton = findViewById(R.id.userButton);

        supplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход к экрану регистрации/входа для поставщика
                Intent intent = new Intent(MainActivityChoice.this, SupplierRegistrationActivity.class);
                startActivity(intent);
            }
        });

        courierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход к экрану регистрации/входа для курьера
                Intent intent = new Intent(MainActivityChoice.this, CourierRegistrationActivity.class);
                startActivity(intent);
            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход к экрану регистрации/входа для пользователя
                Intent intent = new Intent(MainActivityChoice.this, UserRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}

