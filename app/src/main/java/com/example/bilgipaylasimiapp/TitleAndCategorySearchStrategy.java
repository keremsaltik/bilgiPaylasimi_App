package com.example.bilgipaylasimiapp;

import java.util.List;

public class TitleAndCategorySearchStrategy implements  SearchStrategy{

    /**
     * Hem başlığa hem kategoriye göre arama yapan strateji.
     * Her iki alan da dolu olduğunda kullanılır.
     */
    private final PostRepository postRepository = PostRepository.getInstance();

    @Override
    public void search(String query, String category,
                       PostRepository.OnSuccessListener<List<Post>> onSuccess,
                       PostRepository.OnFailureListener onFailureListener) {
        postRepository.searchPosts(query, category, onSuccess, onFailureListener);
    }
}
