package com.example.bilgipaylasimiapp;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {
    private static PostRepository instance; // Tekil instance
    private FirebaseFirestore db;

    // Private constructor (dışarıdan new ile oluşturulamaz)
    private PostRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public  static  PostRepository getInstance() {
        if (instance == null) {
            synchronized (PostRepository.class) {
                if (instance == null) {
                    instance = new PostRepository();
                }
            }
        }
        return  instance;
    }
    // Gönderi Ekleme
    public void addPost(Information post, OnSuccessListener<String> onSuccess, OnFailureListener onFailure) {
        db.collection("posts")
                .add(post.toMap())
                .addOnSuccessListener(documentReference -> {
                    post.setId(documentReference.getId());
                    onSuccess.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(onFailure::onFailure);
    }

    // Gönderiyi alma
    public void getPost(String postId, OnSuccessListener<Information> onSuccess, OnFailureListener onFailure) {
        db.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Information post = documentSnapshot.toObject(Information.class);
                        if (post != null) {
                            post.setId(documentSnapshot.getId());
                            onSuccess.onSuccess(post);
                        }
                    } else {
                        onFailure.onFailure(new Exception("Gönderi bulunamadı"));
                    }
                })
                .addOnFailureListener(onFailure::onFailure);
    }

    // Gönderiyi güncelleme
    public void updatePost(String postId, Information post, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection("posts")
                .document(postId)
                .update("title", post.getTitle(), "information", post.getInformation())
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(onFailure::onFailure);
    }

    // Tüm gönderileri gerçek zamanlı alma
    public ListenerRegistration fetchAllPosts(OnPostsListener onPostsListener, OnFailureListener onFailure) {
        return db.collection("posts")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        onFailure.onFailure(e);
                        return;
                    }
                    if (snapshot != null && !snapshot.isEmpty()) {
                        List<Information> posts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : snapshot) {
                            Information post = document.toObject(Information.class);
                            post.setId(document.getId());
                            posts.add(post);
                        }
                        onPostsListener.onPostsUpdated(posts);
                    }
                });
    }

    // Gönderileri arama/filtreleme
    public void searchPosts(String query, String category, OnSuccessListener<List<Information>> onSuccess, OnFailureListener onFailure) {
        Query queryRef = db.collection("posts");

        if (category != null && !category.isEmpty()) {
            queryRef = queryRef.whereEqualTo("category", category);
        }
        if (query != null && !query.isEmpty()) {
            queryRef = queryRef.whereGreaterThanOrEqualTo("title", query)
                    .whereLessThanOrEqualTo("title", query + "\uf8ff");
        }

        queryRef.get()
                .addOnSuccessListener(snapshot -> {
                    List<Information> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshot) {
                        Information post = document.toObject(Information.class);
                        post.setId(document.getId());
                        posts.add(post);
                    }
                    onSuccess.onSuccess(posts);
                })
                .addOnFailureListener(onFailure::onFailure);
    }
    public interface OnSuccessListener<T> {
        void onSuccess(T result);

    }

    public interface OnFailureListener {
        void  onFailure(Exception e);
    }

    public interface OnPostsListener {
        void onPostsUpdated(List<Information> posts);
    }
}