package com.example.delan;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourierActivity extends AppCompatActivity implements OrderAdapter.OnOrderActionClickListener {
    private FirebaseFirestore db;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier);

        db = FirebaseFirestore.getInstance();

        RecyclerView ordersRecyclerView = findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList, this);
        ordersRecyclerView.setAdapter(orderAdapter);

        loadOrders();
    }

    private void loadOrders() {
        db.collection("orders")
                .whereIn("status", Arrays.asList("Заказано", "Товар принят"))
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Ошибка при получении заказов", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    orderList.clear();
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot document = dc.getDocument();
                        Order order = new Order();
                        order.setId(document.getId());
                        order.setProductName(document.getString("productName"));
                        order.setCustomerAddress(document.getString("customerAddress"));
                        order.setWarehouseAddress(document.getString("warehouse"));
                        order.setStatus(document.getString("status"));

                        orderList.add(order);
                    }
                    orderAdapter.notifyDataSetChanged();

                    // Проверка на пустой список заказов
                    if (orderList.isEmpty()) {
                        Toast.makeText(this, "Нет заказов с текущими статусами", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onOrderActionClick(Order order) {
        handleOrderAction(order);
    }

    private void handleOrderAction(Order order) {
        db.collection("orders").document(order.getId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String currentStatus = documentSnapshot.getString("status");
                    if ("Заказано".equals(currentStatus)) {
                        updateOrderStatus(order.getId(), "Товар принят");
                    } else if ("Товар принят".equals(currentStatus)) {
                        updateOrderStatus(order.getId(), "Доставлено");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при получении статуса заказа", Toast.LENGTH_SHORT).show());
    }

    private void updateOrderStatus(String orderId, String status) {
        db.collection("orders").document(orderId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Статус обновлен: " + status, Toast.LENGTH_SHORT).show();
                    loadOrders();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при обновлении статуса", Toast.LENGTH_SHORT).show());
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

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}
