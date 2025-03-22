package com.example.bilgipaylasimiapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostHolderView extends RecyclerView.ViewHolder {

    public TextView postTitle,postInfo,postAuthor;

    public PostHolderView(@NonNull View itemView) {
        super(itemView);
        postTitle = itemView.findViewById(R.id.title);
        postInfo = itemView.findViewById(R.id.information);
        //postAuthor = itemView.findViewById(R.id.author);
        /*
        * <TextView
        android:id="@+id/author"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Author"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/information"
        android:textColor="@color/black"
        tools:layout_editor_absoluteX="378dp"
        tools:layout_editor_absoluteY="39dp" />*/
    }
}
