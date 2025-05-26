package com.example.bilgipaylasimiapp;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class PostService {
    private  final PostRepository postRepository = PostRepository.getInstance();
    private  SearchStrategy searchStrategy;

    public PostService() {
        this.searchStrategy = new TitleSearchStrategy(); // Varsayılan Strateji
    }

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    /**
     * Yeni gönderi oluşturur.
     */
    public  void createPost(Post post, PostRepository.OnSuccessListener<String> onSuccess, PostRepository.OnFailureListener onFailure) {
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

    /**
     * Görüntülenecek olan gönderiyi ID parametresine bakarak getirir.
     */
    public void fetchPost(String postId, PostRepository.OnSuccessListener<Post> onSuccess, PostRepository.OnFailureListener onFailure) {
        postRepository.getPost(postId, onSuccess, onFailure);
    }

    /**
     * Gönderiyi günceller.
     */
    public void updatePost(String postId, Post post, PostRepository.OnSuccessListener<Void> onSuccess, PostRepository.OnFailureListener onFailure) {
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

    /**
     * Tüm gönderileri gerçek zamanlı olarak alır.
     */
    public ListenerRegistration fetchAllPosts(PostRepository.OnPostsListener onPostsListener, PostRepository.OnFailureListener onFailure) {
        return postRepository.fetchAllPosts(onPostsListener, onFailure);
    }

    /**
     * Başlık ve/veya kategoriye göre arama yapar.
     * Arama stratejisi dinamik olarak belirlenir.
     */
    public void searchPosts(String query, String category,
                            PostRepository.OnSuccessListener<List<Post>> onSuccess,
                            PostRepository.OnFailureListener onFailure) {

        if (query != null && !query.isEmpty() && category != null && !category.isEmpty()) {
            setSearchStrategy(new TitleAndCategorySearchStrategy());
        } else if (query != null && !query.isEmpty()) {
            setSearchStrategy(new TitleSearchStrategy());
        } else if (category != null && !category.isEmpty()) {
            setSearchStrategy(new CategorySearchStrategy());
            fetchAllPosts(onSuccess::onSuccess, onFailure);
            return;
        }

        searchStrategy.search(query, category, onSuccess, onFailure);
    }



}
