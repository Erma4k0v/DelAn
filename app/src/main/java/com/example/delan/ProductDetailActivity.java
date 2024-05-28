package com.example.delan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";

    ImageView productImage;
    TextView productName, productDescription, productPrice;
    Button buyButton, receivedButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String currentProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        buyButton = findViewById(R.id.buy_button);
        receivedButton = findViewById(R.id.received_button);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            currentProductId = product.getId();
            Glide.with(this).load(product.getImageUrl()).into(productImage);
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText(String.format("$%.2f", product.getPrice()));

            checkOrderStatus(product);

            buyButton.setOnClickListener(v -> placeOrder(product));
            receivedButton.setOnClickListener(v -> markProductReceived(product));
        } else {
            Log.e(TAG, "Product is null");
        }
    }

    private void placeOrder(Product product) {
        String customerId = auth.getCurrentUser().getUid();
        String orderId = db.collection("orders").document().getId();
        String productId = product.getId();
        String productName = product.getName();
        String productUrl = product.getImageUrl();
        String customerAddress = "exampleCustomerAddress";  // Замените на фактический адрес клиента из профиля
        String supplierId = product.getSupplierId();
        String supplierWarehouse = "exampleSupplierWarehouse";  // Замените на фактический склад поставщика из профиля
        String courierId = "";
        String status = "Order Placed";

        Map<String, Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("productId", productId);
        order.put("productName", productName);
        order.put("productUrl", productUrl);
        order.put("customerId", customerId);
        order.put("customerAddress", customerAddress);
        order.put("supplierId", supplierId);
        order.put("supplierWarehouse", supplierWarehouse);
        order.put("courierId", courierId);
        order.put("status", status);

        db.collection("orders").document(orderId).set(order)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Заказ размещен", Toast.LENGTH_SHORT).show();
                    saveOrderId(productId, orderId); // Сохраняем идентификатор заказа
                    saveOrderStatus(productId, "Order Placed");
                    buyButton.setVisibility(View.GONE);
                    receivedButton.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка при размещении заказа", e);
                    Toast.makeText(this, "Ошибка при размещении заказа", Toast.LENGTH_SHORT).show();
                });
    }

    private void markProductReceived(Product product) {
        String orderId = getSavedOrderId(product.getId());
        Log.d(TAG, "Order ID: " + orderId);
        if (orderId != null) {
            db.collection("orders").document(orderId)
                    .update("status", "Product Received")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Товар получен", Toast.LENGTH_SHORT).show();
                        saveOrderStatus(product.getId(), "Product Received");
                        receivedButton.setEnabled(false);
                        buyButton.setVisibility(View.VISIBLE);
                        receivedButton.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Ошибка при обновлении статуса", e);
                        Toast.makeText(this, "Ошибка при обновлении статуса", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e(TAG, "Ошибка: не найден идентификатор заказа для продукта с ID: " + product.getId());
            Toast.makeText(this, "Ошибка: не найден идентификатор заказа", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkOrderStatus(Product product) {
        String customerId = auth.getCurrentUser().getUid();
        String productId = product.getId();

        db.collection("orders")
                .whereEqualTo("customerId", customerId)
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String status = document.getString("status");
                            saveOrderId(productId, document.getId()); // Сохраняем идентификатор заказа для продукта
                            Log.d(TAG, "Order Status: " + status);
                            if ("Product Received".equals(status)) {
                                buyButton.setVisibility(View.VISIBLE);
                                receivedButton.setVisibility(View.GONE);
                                receivedButton.setEnabled(false);
                                return;
                            } else if ("Order Placed".equals(status)) {
                                buyButton.setVisibility(View.GONE);
                                receivedButton.setVisibility(View.VISIBLE);
                                receivedButton.setEnabled(true);
                                return;
                            }
                        }
                    }
                    buyButton.setVisibility(View.VISIBLE);
                    receivedButton.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка при проверке статуса заказа", e);
                    Toast.makeText(this, "Ошибка при проверке статуса заказа", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveOrderStatus(String productId, String status) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("orderStatus_" + productId, status);
        editor.apply();
    }

    private String getSavedOrderStatus(String productId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("orderStatus_" + productId, null);
    }

    private void saveOrderId(String productId, String orderId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("orderId_" + productId, orderId);
        editor.apply();
    }

    private String getSavedOrderId(String productId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("orderId_" + productId, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            checkOrderStatus(product);
        }
    }
}

