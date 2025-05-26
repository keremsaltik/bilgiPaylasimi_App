package com.example.bilgipaylasimiapp;

import java.util.List;

/**
 * Arama işlemlerinde kullanılacak strateji arayüzüdür.
 * Farklı arama senaryolarını (başlığa, kategoriye, ikisine göre) tanımlar.
 */

public interface SearchStrategy {
    void search(String query, String category, PostRepository.OnSuccessListener<List<Post>> onSuccess, PostRepository.OnFailureListener onFailureListener);
}
