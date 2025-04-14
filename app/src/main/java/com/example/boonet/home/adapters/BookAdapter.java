package com.example.boonet.home.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boonet.R;
import com.example.boonet.core.entities.Book;
import com.example.boonet.core.utils.Utils;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;
    private final OnBookClickListener bookClickListener;

    public BookAdapter(List<Book> bookList, OnBookClickListener bookClickListener) {
        this.bookList = bookList;
        this.bookClickListener = bookClickListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        // Проверка наличия изображения у книги
        if (book != null) {
            holder.tvTitle.setText(book.getTitle());
            holder.tvAuthor.setText(book.getAuthor());

            // Проверка на null или пустое изображение
            if (book.getImage() != null && !book.getImage().isEmpty()) {
                Bitmap image = Utils.decodeBase64ToImage(book.getImage());
                // Здесь можно установить изображение, например, в ImageView:
                holder.ivBookImage.setImageBitmap(image); // Предполагаем, что у вас есть ImageView в макете
            } else {
                Log.e("BookAdapter", "Изображение для книги " + book.getTitle() + " отсутствует или пустое.");
                // Вы можете установить изображение по умолчанию, если оно отсутствует
                holder.ivBookImage.setImageResource(R.drawable.no_image);  // Убедитесь, что у вас есть default_book_image
            }
        } else {
            Log.e("BookAdapter", "Книга с индексом " + position + " равна null");
        }

        holder.itemView.setOnClickListener(v -> bookClickListener.onBookClick(book));
    }



    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public void setList(List<Book> books) {
        this.bookList = books;
        notifyDataSetChanged();  // Обновление адаптера
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor;
        ImageView ivBookImage;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_book_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            ivBookImage = itemView.findViewById(R.id.iv_book); // ImageView для книги
        }
    }

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }
}
