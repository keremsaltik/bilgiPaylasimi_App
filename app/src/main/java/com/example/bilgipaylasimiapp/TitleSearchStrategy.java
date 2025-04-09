package com.example.bilgipaylasimiapp;

import java.util.List;

public class TitleSearchStrategy implements SearchStrategy{
   private PostRepository postRepository;
    public TitleSearchStrategy(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    @Override
    public void search(String query, String category, PostRepository.OnSuccessListener<List<Information>> onSuccess, PostRepository.OnFailureListener onFailureListener) {
        postRepository.searchPosts(query,category,onSuccess,onFailureListener);
    }
}
