package com.example.bilgipaylasimiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddInformationActivity extends AppCompatActivity {


    String[] items = {"Temel Bilgiler", "Bilim", "Teknoloji", "Sosyal Bilimler", "Sanat ve Kültür", "Tarih ve Coğrafya", "Sağlık ve Tıp", "İş ve Ekonomi", "Hukuk ve Politika", "Felsefe ve Mantık", "Spor", "Edebiyat ve Dil", "Çevre ve Doğa"};

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    private EditText titleText, informationText;
    private Button addButton;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_information);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        titleText = findViewById(R.id.titleField);
        informationText = findViewById(R.id.informationField);
        addButton = findViewById(R.id.addInfoButton);
        autoCompleteTextView = findViewById(R.id.auto_complete_field);


        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        autoCompleteTextView.setAdapter(adapterItems);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = titleText.getText().toString().trim();
                String information = informationText.getText().toString().trim();

                if (item == null || item.isEmpty() || title.isEmpty() || information.isEmpty()) {
                    Toast.makeText(AddInformationActivity.this,"Lütfen tüm alanları doldurun",Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    addData(title, information, item, userId);
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void addData(String title, String information, String category, String author) {
        Information info = new Information(title, information, category, author);


        db.collection("posts").document().set(info.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddInformationActivity.this, "Post başarıyla eklendi", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(AddInformationActivity.this, HomePage.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddInformationActivity.this, "Post eklenirken hata oluştu. Lütfen tekrar deneyiniz", Toast.LENGTH_LONG).show();
            }
        });
    }
}
