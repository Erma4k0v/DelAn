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

import java.util.Arrays;

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

        loadOrders();

        orderActionButton.setOnClickListener(v -> handleOrderAction());
    }

    private void loadOrders() {
        db.collection("orders")
                .whereIn("status", Arrays.asList("Заказано", "Заказ принят"))
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Ошибка при получении заказов", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot document = dc.getDocument();
                        orderId = document.getId();
                        String productId = document.getString("productId");
                        String customerAddress = document.getString("customerAddress");
                        String supplierWarehouse = document.getString("warehouse");

                        fetchProductNameAndDisplayOrderDetails(productId, customerAddress, supplierWarehouse, document.getString("status"));
                    }
                });
    }

    private void fetchProductNameAndDisplayOrderDetails(String productId, String customerAddress, String supplierWarehouse, String status) {
        db.collection("products").document(productId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String productName = task.getResult().getString("name");
                        displayOrderDetails(orderId, productName, customerAddress, supplierWarehouse, status);
                    } else {
                        Toast.makeText(this, "Ошибка при получении данных продукта", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleOrderAction() {
        db.collection("orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String currentStatus = documentSnapshot.getString("status");
                    if ("Заказано".equals(currentStatus)) {
                        updateOrderStatus(orderId, "Заказ принят");
                    } else if ("Заказ принят".equals(currentStatus)) {
                        updateOrderStatus(orderId, "Доставлено");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при получении статуса заказа", Toast.LENGTH_SHORT).show());
    }

    private void updateOrderStatus(String orderId, String status) {
        db.collection("orders").document(orderId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Статус обновлен: " + status, Toast.LENGTH_SHORT).show();
                    if ("Доставлено".equals(status)) {
                        orderActionButton.setText("Заказ выполнен");
                        orderActionButton.setEnabled(false);
                    } else if ("Заказ принят".equals(status)) {
                        orderActionButton.setText("Доставить заказ");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при обновлении статуса", Toast.LENGTH_SHORT).show());
    }

    private void displayOrderDetails(String orderId, String productName, String customerAddress, String supplierWarehouse, String status) {
        orderDetailsTextView.setText("Заказ: " + productName + "\nАдрес клиента: " + customerAddress + "\nСклад поставщика: " + supplierWarehouse);
        if ("Заказано".equals(status)) {
            orderActionButton.setText("Принять заказ");
        } else if ("Заказ принят".equals(status)) {
            orderActionButton.setText("Доставить заказ");
        }
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
