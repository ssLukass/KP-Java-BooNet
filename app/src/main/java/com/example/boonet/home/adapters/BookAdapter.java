package com.example.boonet.home.adapters;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.boonet.R;
import com.example.boonet.core.adapters.BaseAdapter;
import com.example.boonet.core.entities.Book;
import com.example.boonet.core.utils.Utils;

import java.util.List;

public class BookAdapter extends BaseAdapter<Book, BookAdapter.BookViewHolder, BookAdapter.OnBookClickListener> {

    private static final String TAG = "BookAdapter";

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookAdapter(List<Book> books, OnBookClickListener listener) {
        super(books, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.book_item;
    }

    @Override
    protected BookViewHolder createViewHolder(View view) {
        return new BookViewHolder(view);
    }

    static class BookViewHolder extends BaseAdapter.BaseViewHolder<Book> {
        private final TextView tvTitle;
        private final TextView tvAuthor;
        private final ImageView ivBookImage;
        private final TextView tvSubscriptionStatus;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_book_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            ivBookImage = itemView.findViewById(R.id.iv_book);
            tvSubscriptionStatus = itemView.findViewById(R.id.tv_subscription_status);
        }

        @Override
        public void bind(Book book, Object listener) {
            if (book != null) {
                tvTitle.setText(book.getTitle());
                tvAuthor.setText(book.getAuthor());

                // Обрабатываем состояние подписки
                if (book.isSubscription()) {
                    tvSubscriptionStatus.setText("Подписка");
                } else {
                    tvSubscriptionStatus.setText("Бесплатно");
                }

                if (book.getImageBase64() != null && !book.getImageBase64().isEmpty()) {
                    Bitmap image = Utils.decodeBase64ToImage(book.getImageBase64());
                    if (image != null) {
                        ivBookImage.setImageBitmap(image);
                    } else {
                        Log.e(TAG, "Не удалось декодировать изображение для книги: " + book.getTitle());
                        ivBookImage.setImageResource(R.drawable.no_image);
                    }
                } else {
                    ivBookImage.setImageResource(R.drawable.no_image);
                }

                itemView.setOnClickListener(v -> {
                    if (listener instanceof OnBookClickListener) {
                        ((OnBookClickListener) listener).onBookClick(book);
                    }
                });
            }
        }
    }
}
