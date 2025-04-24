package com.example.boonet.search.adapters;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.boonet.R;
import com.example.boonet.core.adapters.BaseAdapter;
import com.example.boonet.core.entities.Book;
import com.example.boonet.core.utils.Utils;

import java.util.List;

public class SearchAdapter extends BaseAdapter<Book, SearchAdapter.SearchViewHolder, SearchAdapter.BookClickCallback> {

    private static final String TAG = "SearchAdapter";

    public interface BookClickCallback {
        void onClick(Book book);
    }

    public SearchAdapter(List<Book> books, BookClickCallback callback) {
        super(books, callback);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.book_item_search;
    }

    @Override
    protected SearchViewHolder createViewHolder(View view) {
        return new SearchViewHolder(view);
    }

    static class SearchViewHolder extends BaseAdapter.BaseViewHolder<Book> {
        private final ImageView ivProductImage;
        private final TextView tvTitle;
        private final TextView tvAuthor;
        private final TextView tvSubscription;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvTitle = itemView.findViewById(R.id.tvProductTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvSubscription = itemView.findViewById(R.id.tvSubscription);
        }

        @Override
        public void bind(Book book, Object listener) {
            if (book != null) {
                tvTitle.setText(book.getTitle());
                tvAuthor.setText(book.getAuthor() != null ? book.getAuthor() : "Автор не указан");

                // Установка статуса подписки
                if (book.isSubscription()) {
                    tvSubscription.setText("По подписке");
                    tvSubscription.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.premium_color));
                } else {
                    tvSubscription.setText("Бесплатно");
                    tvSubscription.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.free_color));
                }

                // Обработка изображения
                if (book.getImageBase64() != null && !book.getImageBase64().isEmpty()) {
                    Bitmap image = Utils.decodeBase64ToImage(book.getImageBase64());
                    if (image != null) {
                        ivProductImage.setImageBitmap(image);
                    } else {
                        Log.e(TAG, "Не удалось декодировать изображение для книги: " + book.getTitle());
                        ivProductImage.setImageResource(R.drawable.no_image);
                    }
                } else {
                    ivProductImage.setImageResource(R.drawable.no_image);
                }

                itemView.setOnClickListener(v -> {
                    if (listener instanceof BookClickCallback) {
                        ((BookClickCallback) listener).onClick(book);
                    }
                });
            }
        }
    }
}