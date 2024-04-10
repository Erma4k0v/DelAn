package com.example.delan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private static final String DB_URL = "jdbc:jtds:sqlserver://your_database_server:port/your_database_name";
    private static final String DB_USER = "your_database_username";
    private static final String DB_PASSWORD = "your_database_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Пример вызова метода авторизации
        authenticateUser("username", "password");
    }

    private void authenticateUser(String username, String password) {
        new AuthenticateUserTask().execute(username, password);
    }

    private class AuthenticateUserTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet resultSet = pstmt.executeQuery();
                boolean exists = resultSet.next();
                resultSet.close();
                pstmt.close();
                conn.close();
                return exists;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isAuthenticated) {
            if (isAuthenticated) {
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
