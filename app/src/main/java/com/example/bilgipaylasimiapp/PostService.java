package com.example.bilgipaylasimiapp;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class PostService {
    private  PostRepository postRepository;
    private  SearchStrategy searchStrategy;

    public PostService() {
        this.postRepository = PostRepository.getInstance();
        this.searchStrategy = new TitleSearchStrategy(postRepository); // Varsayılan Strateji
    }

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }
    public  void createPost(Information post, PostRepository.OnSuccessListener<String> onSuccess, PostRepository.OnFailureListener onFailure) {
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            onFailure.onFailure(new Exception("Başlık boş olamaz"));
            return;
        }
        if (post.getInformation() == null || post.getInformation().isEmpty()) {
            onFailure.onFailure(new Exception("İçerik boş olamaz"));
            return;
        }
        if (post.getCategory() == null || post.getCategory().isEmpty()) {
            onFailure.onFailure(new Exception("Kategori boş olamaz"));
            return;
        }

        postRepository.addPost(post,onSuccess,onFailure);
    }

    // Gönderiyi alma
    public void fetchPost(String postId, PostRepository.OnSuccessListener<Information> onSuccess, PostRepository.OnFailureListener onFailure) {
        postRepository.getPost(postId, onSuccess, onFailure);
    }

    // Gönderiyi güncelleme
    public void updatePost(String postId, Information post, PostRepository.OnSuccessListener<Void> onSuccess, PostRepository.OnFailureListener onFailure) {
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            onFailure.onFailure(new Exception("Başlık boş olamaz"));
            return;
        }
        if (post.getInformation() == null || post.getInformation().isEmpty()) {
            onFailure.onFailure(new Exception("Bilgi içeriği boş olamaz"));
            return;
        }

        postRepository.updatePost(postId, post, onSuccess, onFailure);
    }

    // Tüm gönderileri gerçek zamanlı alma
    public ListenerRegistration fetchAllPosts(PostRepository.OnPostsListener onPostsListener, PostRepository.OnFailureListener onFailure) {
        return postRepository.fetchAllPosts(onPostsListener, onFailure);
    }

    // Gönderileri arama/filtreleme
    public  void searchPosts(String query, String category, PostRepository.OnSuccessListener<List<Information>> onSuccess, PostRepository.OnFailureListener onFailure) {
        searchStrategy.search(query,category,onSuccess,onFailure);
    }


}
