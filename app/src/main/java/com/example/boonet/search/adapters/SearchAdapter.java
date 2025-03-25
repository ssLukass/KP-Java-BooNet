package com.example.boonet.search.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boonet.core.entities.Book;
import com.example.boonet.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ProductViewHolder> {

    public interface BookClickCallback {
        void onClick(Book book);
    }

    private  List<Book> bookList;
    private BookClickCallback clickCallback;

    public SearchAdapter(List<Book> bookList, BookClickCallback clickCallback) {
        this.bookList = bookList;
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_search, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvPrice.setText(String.format("%s ₸", book.getPrice())); // Format the price with the currency symbol
        if (book.getImage() != null && !book.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getImage())
                    .into(holder.ivProductImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCallback.onClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice;
        ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvProductTitle);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}
