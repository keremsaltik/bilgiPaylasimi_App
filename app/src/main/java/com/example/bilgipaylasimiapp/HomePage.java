package com.example.bilgipaylasimiapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private EditText searchEditText;
    private Button addButton, clearButton;
    Toolbar toolbar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Intent intent;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Information> postList = new ArrayList<>();
    private FirebaseFirestore firestore;
    String[] items = {"Temel Bilgiler", "Bilim", "Teknoloji", "Sosyal Bilimler", "Sanat ve Kültür", "Tarih ve Coğrafya", "Sağlık ve Tıp", "İş ve Ekonomi", "Hukuk ve Politika", "Felsefe ve Mantık", "Spor", "Edebiyat ve Dil", "Çevre ve Doğa"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        searchEditText = findViewById(R.id.searchField);
        addButton = findViewById(R.id.addButton);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        clearButton = findViewById(R.id.clearButton);
        recyclerView = findViewById(R.id.posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostAdapter(postList, this);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        autoCompleteTextView = findViewById(R.id.auto_complete_field);

        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            filterPostsByCategory(selectedCategory);
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPosts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        clearButton.setOnClickListener(view -> {
            autoCompleteTextView.setText("");
            searchEditText.setText("");
            fetchPosts();
        });

        addButton.setOnClickListener(view -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Intent intent = new Intent(HomePage.this, AddInformationActivity.class);
            startActivity(intent);
        });

        fetchPosts();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchPosts() {
        firestore.collection("posts")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(HomePage.this, "Veri alınırken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot != null && !snapshot.isEmpty()) {
                        postList.clear();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Information post = document.toObject(Information.class);
                            post.setId(document.getId());
                            postList.add(post);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuProfile) {
            intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.menuLogOut) {
            mAuth.signOut();
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    private void searchPosts(String query) {
        String selectedCategory = autoCompleteTextView.getText().toString();


        Query queryRef = firestore.collection("posts");

        if (!selectedCategory.isEmpty()) {
            queryRef = queryRef.whereEqualTo("category", selectedCategory);
        }
        if (!query.isEmpty()) {
            queryRef = queryRef.whereGreaterThanOrEqualTo("title", query)
                    .whereLessThanOrEqualTo("title", query + "\uf8ff");
        }


        queryRef.get().addOnCompleteListener(task -> {
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
                Exception e = task.getException();
                String errorMessage = "Arama başarısız oldu: " + (e != null ? e.getMessage() : "Bilinmeyen hata");
                Toast.makeText(HomePage.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.d("TAG", errorMessage);
            }
        });
    }

    private void filterPostsByCategory(String category) {
        String searchQuery = searchEditText.getText().toString(); // Arama metnini al


        if (!category.isEmpty() || !searchQuery.isEmpty()) {
            searchPosts(searchQuery);
        } else {

            fetchPosts();
        }
    }



}
