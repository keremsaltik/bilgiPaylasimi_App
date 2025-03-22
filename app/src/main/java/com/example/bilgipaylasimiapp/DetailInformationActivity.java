package com.example.bilgipaylasimiapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class DetailInformationActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private TextView titleView, contentView, categoryView;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_information);


        titleView = findViewById(R.id.titleView);
        contentView = findViewById(R.id.contentView);
        categoryView = findViewById(R.id.categoryView);
        firestore = FirebaseFirestore.getInstance();


        postId = getIntent().getStringExtra("postId");
        fetchPostDetail(postId);
    }

    private void fetchPostDetail(String postId) {
        firestore.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Information post = documentSnapshot.toObject(Information.class);
                        if (post != null) {
                            displayPostDetail(post);
                        } else {
                            showErrorAndFinish("Gönderi bulunamadı.");
                        }
                    } else {
                        showErrorAndFinish("Gönderi bulunamadı.");
                    }
                })
                .addOnFailureListener(e -> {
                    showErrorAndFinish("Hata: " + e.getMessage());
                });
    }

    private void displayPostDetail(Information post) {
        // Başlık ve içeriği TextView'lere set et
        titleView.setText(post.getTitle());
        contentView.setText(post.getInformation());
        categoryView.setText(post.getCategory());
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}
