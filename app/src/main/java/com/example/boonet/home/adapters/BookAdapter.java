package com.example.boonet.home.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boonet.R;
import com.example.boonet.home.interfaces.BookClickCallback;
import com.example.boonet.core.entities.Book;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private BookClickCallback callback;

    private ArrayList<Book> books = new ArrayList<>();


    public BookAdapter(BookClickCallback callback) {
        this.callback = callback;
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        TextView price;


        BookViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_book_title);
            image = itemView.findViewById(R.id.iv_book_image);
            price = itemView.findViewById(R.id.tv_book_price);
        }
    }

    public void setList(ArrayList<Book> products) {
        this.books = products;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.book_item,
                parent,
                false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.title.setText(book.getTitle());
        holder.price.setText(book.getPrice() + " ₸");
        if (TextUtils.isEmpty(book.getImage())) {
            Glide.with(holder.itemView.getContext()).load(R.drawable.no_image).into(holder.image);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(book.getImage())
                    .into(holder.image);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(book);
            }
        });

    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}