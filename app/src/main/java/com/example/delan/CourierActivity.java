package com.example.delan;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CourierActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Button orderActionButton;
    private TextView orderDetailsTextView;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        orderActionButton = findViewById(R.id.order_action_button);
        orderDetailsTextView = findViewById(R.id.order_details_text_view);

        db.collection("orders")
                .whereEqualTo("status", "Order Placed")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Ошибка при получении заказов", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot document = dc.getDocument();
                        orderId = document.getId();
                        String productName = document.getString("productName");
                        String customerAddress = document.getString("customerAddress");
                        String supplierWarehouse = document.getString("supplierWarehouse");

                        // Отображаем сообщение о новом заказе
                        displayOrderDetails(orderId, productName, customerAddress, supplierWarehouse);
                    }
                });

        orderActionButton.setOnClickListener(v -> handleOrderAction());
    }

    private void handleOrderAction() {
        db.collection("orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String currentStatus = documentSnapshot.getString("status");
                    if ("Order Placed".equals(currentStatus)) {
                        updateOrderStatus(orderId, "Courier Accepted");
                    } else if ("Courier Accepted".equals(currentStatus)) {
                        updateOrderStatus(orderId, "Delivered");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при получении статуса заказа", Toast.LENGTH_SHORT).show());
    }

    private void updateOrderStatus(String orderId, String status) {
        db.collection("orders").document(orderId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Статус обновлен: " + status, Toast.LENGTH_SHORT).show();
                    if ("Delivered".equals(status)) {
                        orderActionButton.setText("Заказ выполнен");
                        orderActionButton.setEnabled(false);
                    } else if ("Courier Accepted".equals(status)) {
                        orderActionButton.setText("Заказ выполнен");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при обновлении статуса", Toast.LENGTH_SHORT).show());
    }

    private void displayOrderDetails(String orderId, String productName, String customerAddress, String supplierWarehouse) {
        orderDetailsTextView.setText("Заказ: " + productName + "\nАдрес клиента: " + customerAddress + "\nСклад поставщика: " + supplierWarehouse);
        orderActionButton.setText("Принять заказ");
        orderActionButton.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return false;
    }
}
