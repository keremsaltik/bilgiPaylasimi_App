package com.example.bilgipaylasimiapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    // UI bileşenleri
    private EditText searchEditText;
    private Button addButton, clearButton;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AutoCompleteTextView autoCompleteTextView;

    // Adapter ve veri listesi
    private PostAdapter adapter;
    private final List<Post> postList = new ArrayList<>();
    private ArrayAdapter<String> adapterItems;

    // Veritabanı ve servis
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ListenerRegistration postListener;
    private final PostService postService = new PostService();

    // Kategori verileri
    private final String[] items = {
            "Temel Bilgiler", "Bilim", "Teknoloji", "Sosyal Bilimler",
            "Sanat ve Kültür", "Tarih ve Coğrafya", "Sağlık ve Tıp",
            "İş ve Ekonomi", "Hukuk ve Politika", "Felsefe ve Mantık",
            "Spor", "Edebiyat ve Dil", "Çevre ve Doğa"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSearchFields();
        setupButtons();
        fetchPosts(); // Tüm gönderileri getir
    }

    // UI bileşenlerini başlat
    private void initViews() {
        searchEditText = findViewById(R.id.searchField);
        addButton = findViewById(R.id.addButton);
        clearButton = findViewById(R.id.clearButton);
        recyclerView = findViewById(R.id.posts);
        autoCompleteTextView = findViewById(R.id.auto_complete_field);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(postList, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchFields() {
        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            String currentQuery = searchEditText.getText().toString();
            searchPosts(currentQuery, selectedCategory);
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPosts(s.toString(), autoCompleteTextView.getText().toString());
            }
        });
    }

    private void setupButtons() {
        addButton.setOnClickListener(view -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(HomePage.this, AddInformationActivity.class));
            }
        });

        clearButton.setOnClickListener(view -> {
            autoCompleteTextView.setText("");
            searchEditText.setText("");
            fetchPosts();
        });
    }

    /**
     * Veritabanından tüm gönderileri gerçek zamanlı olarak çeker.
     */
    private void fetchPosts() {
        if (postListener != null) {
            postListener.remove(); // Önceki dinleyiciyi kaldır
        }

        postListener = postService.fetchAllPosts(
                posts -> {
                    postList.clear();
                    postList.addAll(posts);
                    adapter.notifyDataSetChanged();
                },
                e -> Toast.makeText(this, "Veri alınırken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Arama kriterlerine göre gönderileri getirir (başlık ve/veya kategori).
     */
    private void searchPosts(String query, String category) {
        if (postListener != null) {
            postListener.remove(); // Gerçek zamanlı dinlemeyi durdur
        }

        postService.searchPosts(query, category,
                posts -> {
                    postList.clear();
                    postList.addAll(posts);
                    adapter.notifyDataSetChanged();
                },
                e -> Toast.makeText(this, "Arama hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postListener != null) {
            postListener.remove();
            postListener = null;
        }
    }

    // Menü seçenekleri
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuProfile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (itemId == R.id.menuLogOut) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
