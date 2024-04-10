package com.example.delan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private RadioGroup actionRadioGroup;
    private Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        actionRadioGroup = findViewById(R.id.actionRadioGroup);
        actionButton = findViewById(R.id.actionButton);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                int selectedId = actionRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedId);
                String action = selectedRadioButton.getText().toString();

                // Проверяем введенные данные
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Далее можно добавить логику для обработки входа или регистрации
                // Пока просто переходим на активность ChoiceActivity
                Intent intent = new Intent(MainActivity.this, MainActivityChoice.class);
                startActivity(intent);
            }
        });
    }
}
