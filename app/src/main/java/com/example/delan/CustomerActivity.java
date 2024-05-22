package com.example.delan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity {
    Button exitBtn;
    FirebaseAuth auth;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        exitBtn = findViewById(R.id.exit_btn);
        recyclerView = findViewById(R.id.recycler_view);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, product -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);

        loadProducts();

        exitBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProducts() {
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Ошибка загрузки продуктов", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
