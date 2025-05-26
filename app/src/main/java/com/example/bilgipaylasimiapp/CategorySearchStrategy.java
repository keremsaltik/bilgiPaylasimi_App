package com.example.bilgipaylasimiapp;

import java.util.List;

public class CategorySearchStrategy implements SearchStrategy{

    /**
     * Sadece kategoriye göre arama yapan strateji.
     * Başlık boş bırakılır.
     */
    private final PostRepository postRepository = PostRepository.getInstance();
    @Override
    public void search(String query, String category, PostRepository.OnSuccessListener<List<Post>> onSuccess, PostRepository.OnFailureListener onFailureListener) {
        postRepository.searchPosts("", category, onSuccess, onFailureListener);
    }
}
