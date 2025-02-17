package com.example.delan;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";

    private ImageView productImage;
    private TextView productName, productDescription, productPrice;
    private Button buyButton, receivedButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        initFirebase();

        product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            displayProductDetails(product);
            checkOrderStatus(product);

            buyButton.setOnClickListener(v -> createOrder(product));
            receivedButton.setOnClickListener(v -> markProductReceived(product));
        } else {
            Log.e(TAG, "Product is null");
            Toast.makeText(this, "Продукт не найден", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        buyButton = findViewById(R.id.buy_button);
        receivedButton = findViewById(R.id.received_button);
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void displayProductDetails(Product product) {
        Glide.with(this).load(product.getImageUrl()).into(productImage);
        productName.setText(product.getName());
        productDescription.setText(product.getDescription());
        productPrice.setText(String.format("$%.2f", product.getPrice()));
    }

    private void createOrder(Product product) {
        findCurrentUser(new FindUserCallback() {
            @Override
            public void onUserFound(DocumentSnapshot userDocument) {
                String customerAddress = userDocument.getString("address");

                findUser(product.getSupplierId(), new FindUserCallback() {
                    @Override
                    public void onUserFound(DocumentSnapshot supplierDocument) {
                        String warehouseName = supplierDocument.getString("warehouse_name");

                        String productId = product.getProductId();
                        Map<String, Object> order = new HashMap<>();
                        order.put("productId", productId);
                        order.put("productName", product.getName());
                        order.put("warehouse", warehouseName);
                        order.put("customerId", auth.getCurrentUser().getUid());
                        order.put("customerAddress", customerAddress);
                        order.put("status", "Заказано");

                        db.collection("orders").add(order)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(ProductDetailActivity.this, "Заказ размещен", Toast.LENGTH_SHORT).show();
                                    updateOrderButtons(false, true);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Ошибка при размещении заказа", e);
                                    Toast.makeText(ProductDetailActivity.this, "Ошибка при размещении заказа", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onUserNotFound() {
                        Toast.makeText(ProductDetailActivity.this, "Ошибка: поставщик не найден", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUserNotFound() {
                Toast.makeText(ProductDetailActivity.this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markProductReceived(Product product) {
        db.collection("orders")
                .whereEqualTo("customerId", auth.getCurrentUser().getUid())
                .whereEqualTo("productId", product.getProductId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String orderId = document.getId();
                        String status = document.getString("status");

                        if ("Доставлено".equals(status)) {
                            // Если статус "Доставлено", меняем цвет кнопки на серый
                            receivedButton.setBackgroundTintList(getResources().getColorStateList(R.color.gray_button_background));

                            // Дополнительные действия по обработке получения товара
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("status", "Получено");
                            updates.put("notified", 0);

                            db.collection("orders").document(orderId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Товар получен", Toast.LENGTH_SHORT).show();
                                        updateOrderButtons(false, true);
                                        receivedButton.setEnabled(false);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Ошибка при обновлении статуса", e);
                                        Toast.makeText(this, "Ошибка при обновлении статуса", Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            Toast.makeText(this, "Товар ещё не доставлен", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка при проверке статуса заказа", e);
                    Toast.makeText(this, "Ошибка при проверке статуса заказа", Toast.LENGTH_SHORT).show();
                });
    }



    private void checkOrderStatus(Product product) {
        db.collection("orders")
                .whereEqualTo("customerId", auth.getCurrentUser().getUid())
                .whereEqualTo("productId", product.getProductId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean orderFound = false;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String status = document.getString("status");
                            Log.d(TAG, "Статус заказа: " + status);
                            orderFound = true;
                            if ("Получено".equals(status)) {
                                updateOrderButtons(false, true);
                                receivedButton.setEnabled(false);
                                return;
                            } else if ("Заказано".equals(status) || ("Доставлено".equals(status) || ("Товар принят".equals(status)))) {
                                updateOrderButtons(false, true);
                                receivedButton.setEnabled(true);
                                return;
                            }
                        }
                    }
                    if (!orderFound) {
                        updateOrderButtons(true, false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка при проверке статуса заказа", e);
                    Toast.makeText(this, "Ошибка при проверке статуса заказа", Toast.LENGTH_SHORT).show();
                    updateOrderButtons(true, false);
                });
    }

    private void updateOrderButtons(boolean showBuy, boolean showReceived) {
        buyButton.setVisibility(showBuy ? View.VISIBLE : View.GONE);
        receivedButton.setVisibility(showReceived ? View.VISIBLE : View.GONE);
    }

    private void findUser(String userId, FindUserCallback callback) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            callback.onUserFound(document);
                        } else {
                            Toast.makeText(this, "Документ не найден", Toast.LENGTH_SHORT).show();
                            callback.onUserNotFound();
                        }
                    } else {
                        Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                        callback.onUserNotFound();
                    }
                });
    }

    private void findCurrentUser(FindUserCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            findUser(currentUser.getUid(), callback);
        } else {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            callback.onUserNotFound();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (product != null) {
            checkOrderStatus(product);
        } else {
            Log.e(TAG, "Product is null in onResume");
        }
    }

    // Интерфейс для callback метода findCurrentUser
    interface FindUserCallback {
        void onUserFound(DocumentSnapshot userDocument);

        void onUserNotFound();
    }
}
