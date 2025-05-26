package com.example.bilgipaylasimiapp;

import java.util.List;

public class TitleSearchStrategy implements SearchStrategy{

    /**
     * Sadece başlığa göre arama yapan strateji.
     * Kategori boş bırakılır.
     */
   private PostRepository postRepository = PostRepository.getInstance();

    @Override
    public void search(String query, String category, PostRepository.OnSuccessListener<List<Post>> onSuccess, PostRepository.OnFailureListener onFailureListener) {
        postRepository.searchPosts(query,"",onSuccess,onFailureListener);
    }
}
