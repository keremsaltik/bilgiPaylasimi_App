package com.example.bilgipaylasimiapp;

import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostHolderView> {

    private List<Information> postList;
    private AppCompatActivity activity;


    public static class PostContextMenuInfo implements ContextMenu.ContextMenuInfo {
        int position;
    }

    public PostAdapter(List<Information> postList, AppCompatActivity activity) {
        this.postList = postList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PostHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        return new PostHolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolderView holder, int position) {
        Information post = postList.get(position);
        holder.postTitle.setText(post.getTitle());
        holder.postInfo.setText(post.getInformation());
        //holder.postAuthor.setText(post.getAuthorId());

        if (activity instanceof ProfileActivity) {
            holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                int adapterPosition = holder.getAdapterPosition();

                menu.setHeaderTitle("Seçenekler");
                menu.add(0, v.getId(), 0, "Düzenle").setOnMenuItemClickListener(item -> {
                    Information selectedPost = postList.get(adapterPosition);
                    if (activity instanceof ProfileActivity) {
                        Intent intent = new Intent(activity, EditPostActivity.class);
                        intent.putExtra("postId", selectedPost.getId());
                        activity.startActivity(intent);
                    }
                    return true;
                });
                menu.add(0, v.getId(), 1, "Sil").setOnMenuItemClickListener(item -> {
                    if (activity instanceof ProfileActivity) {
                        Information selectedPost = postList.get(adapterPosition);
                        ((ProfileActivity) activity).deletePost(selectedPost);
                    }
                    return true;
                });
            });
        }
        else if(activity instanceof HomePage){
            holder.itemView.setOnClickListener(v -> {
                Information selectedPost = postList.get(holder.getAdapterPosition());
                String postId = selectedPost.getId();
                if (postId != null && !postId.isEmpty()) {
                    Intent intent = new Intent(activity, DetailInformationActivity.class);
                    intent.putExtra("postId", postId);
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, "Gönderi ID'si bulunamadı", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
