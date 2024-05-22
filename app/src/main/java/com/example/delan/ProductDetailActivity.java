package com.example.delan;

import static java.lang.String.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {
    ImageView productImage;
    TextView productName, productDescription, productPrice;
    Button buyButton;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        buyButton = findViewById(R.id.buy_button);

        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            Glide.with(this).load(product.getImageUrl()).into(productImage);
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText(format("$%.2f", product.getPrice()));

            buyButton.setOnClickListener(v -> {
                // Логика покупки
            });
        }
    }
}
