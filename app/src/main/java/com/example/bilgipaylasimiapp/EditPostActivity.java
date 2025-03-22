package com.example.bilgipaylasimiapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditPostActivity extends AppCompatActivity {
    private EditText editTitle, editInfo;
    private Button saveButton;
    private FirebaseFirestore firestore;
    private String postId;
    private Information post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_post);
        editTitle = findViewById(R.id.editTitle);
        editInfo = findViewById(R.id.editInfo);
        saveButton = findViewById(R.id.saveButton);
        firestore = FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        postId = intent.getStringExtra("postId"); // Post ID'sini al
        getPostData(postId);

        saveButton.setOnClickListener(v -> {
            updatePost(post);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void getPostData(String postId) {
        firestore.collection("posts").document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        post = documentSnapshot.toObject(Information.class);
                        if (post != null) {
                            editTitle.setText(post.getTitle());
                            editInfo.setText(post.getInformation());
                        }
                    }
                });
    }

    private void updatePost(Information post) {
        String updatedTitle = editTitle.getText().toString();
        String updatedInfo = editInfo.getText().toString();

        if (!updatedTitle.isEmpty() && !updatedInfo.isEmpty()) {
            post.setTitle(updatedTitle);
            post.setInformation(updatedInfo);

            firestore.collection("posts")
                    .document(postId)
                    .update( "title", updatedTitle,
                            "information", updatedInfo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditPostActivity.this, "Gönderi başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(this, HomePage.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditPostActivity.this, "Gönderi güncellenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
        }
    }
}