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
    private PostService postService;
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
        postService = new PostService();


        Intent intent = getIntent();
        postId = intent.getStringExtra("postId"); // Post ID'sini al
        fetchPostData(postId);

        saveButton.setOnClickListener(v -> {
            String updatedTitle = editTitle.getText().toString();
            String updatedInfo = editInfo.getText().toString();
            if(post != null){
                post.setTitle(updatedTitle);
                post.setInformation(updatedInfo);
                updatePost(postId,post);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void fetchPostData(String postId) {
        postService.fetchPost(postId,
                fetchedPost -> {
                    post = fetchedPost;
                    editTitle.setText(post.getTitle());
                    editInfo.setText(post.getInformation());
                },
                e -> Toast.makeText(this, "Gönderi yüklenirken hata: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void updatePost(String postId, Information post) {
        postService.updatePost(postId, post,
                aVoid -> {
                    Toast.makeText(this, "Gönderi başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(this, HomePage.class);
                    startActivity(intent);
                },
                e -> Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}