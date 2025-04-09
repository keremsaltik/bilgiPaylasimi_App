package com.example.bilgipaylasimiapp;

import java.util.List;

public interface SearchStrategy {
    void search(String query, String category, PostRepository.OnSuccessListener<List<Information>> onSuccess,  PostRepository.OnFailureListener onFailureListener);
}
