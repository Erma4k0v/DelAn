package com.example.delan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SupplierActivity extends AppCompatActivity {
    TextInputEditText productName, productDescription, productPrice, productImageUrl;
    Button addProductBtn;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        productImageUrl = findViewById(R.id.product_image_url);
        addProductBtn = findViewById(R.id.add_product_btn);

        db = FirebaseFirestore.getInstance();

        addProductBtn.setOnClickListener(v -> {
            String name = Objects.requireNonNull(productName.getText()).toString().trim();
            String description = Objects.requireNonNull(productDescription.getText()).toString().trim();
            String priceString = Objects.requireNonNull(productPrice.getText()).toString().trim();
            String imageUrl = Objects.requireNonNull(productImageUrl.getText()).toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(imageUrl)) {
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

            Product product = new Product(name, imageUrl, description, price);

            db.collection("products").add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                        productName.setText("");
                        productDescription.setText("");
                        productPrice.setText("");
                        productImageUrl.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при добавлении товара", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile){
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return false;
    }
}

