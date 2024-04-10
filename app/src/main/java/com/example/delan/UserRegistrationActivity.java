package com.example.delan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRegistrationActivity extends AppCompatActivity {

    private static final String DB_URL = "jdbc:jtds:sqlserver://your_database_server:port/your_database_name";
    private static final String DB_USER = "your_database_username";
    private static final String DB_PASSWORD = "your_database_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_registration);

        // Пример вызова метода регистрации курьера
        registerCourier("courier_username", "courier_password");
    }

    private void registerCourier(String username, String password) {
        new RegisterCourierTask().execute(username, password);
    }

    private class RegisterCourierTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String sql = "INSERT INTO Couriers (username, password) VALUES (?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                int rowsInserted = pstmt.executeUpdate();
                pstmt.close();
                conn.close();
                return rowsInserted > 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(UserRegistrationActivity.this, "Courier registration successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserRegistrationActivity.this, "Courier registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
