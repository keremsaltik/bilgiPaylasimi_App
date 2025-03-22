package com.example.bilgipaylasimiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser cUser = mAuth.getCurrentUser();
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Information> postList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        recyclerView = findViewById(R.id.posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostAdapter(postList, this);  // 'this' burada ProfileActivity referansı
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);

        firestore = FirebaseFirestore.getInstance();
         String email = cUser.getEmail();
        String firstChar = email.substring(0, 1).toUpperCase();

        TextView roundIcon = findViewById(R.id.roundIcon);
        TextView userInfo = findViewById(R.id.userInfo);


        roundIcon.setText(firstChar);
        userInfo.setText(email);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fetchPosts();
    }


    protected void deletePost(Information post) {
        if (post == null || post.getId() == null) {
            Toast.makeText(this, "Silinecek gönderi bulunamadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("posts")
                .document(post.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Gönderi başarıyla silindi.", Toast.LENGTH_SHORT).show();
                    postList.remove(post);
                    fetchPosts();
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gönderi silinirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchPosts() {
        firestore.collection("posts")
                .whereEqualTo("authorId", cUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            postList.clear();
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                Information post = document.toObject(Information.class);
                                post.setId(document.getId());
                                postList.add(post);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Veri alınırken hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
