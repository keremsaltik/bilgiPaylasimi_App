package com.example.bilgipaylasimiapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mailField, passwordField;
    private Button loginButton, registerButton;
   private Intent intent;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        mailField = findViewById(R.id.mailField);
        passwordField = findViewById(R.id.sifreField);
        loginButton = findViewById(R.id.girisYapButton);
        registerButton = findViewById(R.id.kayitOlButton);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            intent = new Intent(LoginActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mailField.getText().toString().trim();
                String sifre = passwordField.getText().toString().trim();
                if(mail.isEmpty() || sifre.isEmpty() )
                {
                    Toast.makeText(LoginActivity.this, "Lütfen her alanı doldurun", Toast.LENGTH_SHORT).show();
                }
                else{
                    login(mail, sifre);
                }


            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    private void login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            intent = new Intent(LoginActivity.this, HomePage.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(LoginActivity.this, "Bilgileriniz yanlış lütfen tekrar deneyiniz", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }
}