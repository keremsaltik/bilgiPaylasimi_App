package com.example.bilgipaylasimiapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailInformationActivity extends AppCompatActivity {

    private  PostService postService;
    private TextView titleView, contentView, categoryView;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_information);


        titleView = findViewById(R.id.titleView);
        contentView = findViewById(R.id.contentView);
        categoryView = findViewById(R.id.categoryView);
        postService = new PostService();


        postId = getIntent().getStringExtra("postId");
        fetchPostDetail(postId);
    }

    private void fetchPostDetail(String postId) {
        postService.fetchPost(postId,
                post -> displayPostDetail(post),
                e -> showErrorAndFinish("Hata: " + e.getMessage())
        );
    }

    private void displayPostDetail(Post post) {
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
