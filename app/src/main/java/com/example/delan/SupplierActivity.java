package com.example.delan;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SupplierActivity extends AppCompatActivity {
    private static final int JOB_ID = 123;
    TextInputEditText productName, productDescription, productPrice, productImageUrl;
    Button addProductBtn;
    FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        productImageUrl = findViewById(R.id.product_image_url);
        addProductBtn = findViewById(R.id.add_product_btn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        scheduleJob();

        addProductBtn.setOnClickListener(v -> {
            String name = Objects.requireNonNull(productName.getText()).toString().trim();
            String description = Objects.requireNonNull(productDescription.getText()).toString().trim();
            String priceString = Objects.requireNonNull(productPrice.getText()).toString().trim();
            String imageUrl = Objects.requireNonNull(productImageUrl.getText()).toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceString)) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceString);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Неправильный формат цены", Toast.LENGTH_SHORT).show();
                return;
            }

            String supplierId = auth.getCurrentUser().getUid();
            Product product = new Product("", name, imageUrl, description, price, supplierId);

            db.collection("products").add(product)
                    .addOnSuccessListener(documentReference -> {
                        String productId = documentReference.getId();
                        product.setProductId(productId);
                        db.collection("products").document(productId)
                                .set(product)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                                    productName.setText("");
                                    productDescription.setText("");
                                    productPrice.setText("");
                                    productImageUrl.setText("");
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при добавлении товара", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при добавлении товара", Toast.LENGTH_SHORT).show());
        });
    }

    private void scheduleJob() {
        ComponentName componentName = new ComponentName(this, OrderStatusJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)  // Каждую 1 минуту
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(jobInfo);
        }
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
