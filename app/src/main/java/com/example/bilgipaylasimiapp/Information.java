package com.example.bilgipaylasimiapp;

import java.util.HashMap;
import java.util.Map;

public class Information {
    private String id;
    private String title;
    private String information;
    private String category;
    private String authorId;
    private String status = "pending";


    // Boş constructor Firebase için gerekli
    public Information() {
    }

    public Information(String title, String information, String category, String authorId) {
        this.title = title;
        this.information = information;
        this.category = category;
        this.authorId = authorId;  // Bu satır eksikti
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Firebase'e gönderilmesini istemediğimiz alanları dışarıda bırakmak için toMap metodunu ekliyoruz
    public Map<String, Object> toMap() {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("title", title);
        postMap.put("information", information);
        postMap.put("category", category);
        postMap.put("status", status);
        postMap.put("authorId", authorId);
        // id'yi eklemiyoruz
        return postMap;
    }
}