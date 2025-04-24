package com.example.boonet.detailsBook.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boonet.R;
import com.example.boonet.core.entities.Book;
import com.example.boonet.core.utils.Utils;
import com.example.boonet.detailsBook.interfaces.OnBookReceivedCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsBookActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference books;

    private ImageView ivBookImage;
    private TextView tvBookTitle;
    private TextView tvBookPrice;
    private TextView tvBookAuthor;
    private TextView tvBookDescription;


    private void initViews() {
        ivBookImage = findViewById(R.id.iv_book_image);
        tvBookTitle = findViewById(R.id.tv_book_title);
        tvBookPrice = findViewById(R.id.tv_book_price);
        tvBookAuthor = findViewById(R.id.tv_book_author);
        tvBookDescription = findViewById(R.id.tv_book_description);
    }

    private void init() {
        database = FirebaseDatabase.getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
        books = database.getReference("books");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_book);

        initViews();
        init();

        Intent intent = getIntent();
        String bookKey = intent.getStringExtra("BOOK_ID");
        Log.d("BookKey", bookKey);

        getBookByKey(bookKey, book -> {
            if (book != null) {
                if (TextUtils.isEmpty(book.getImageBase64())) {
                    ivBookImage.setImageResource(R.drawable.no_image);
                } else {
                    Bitmap bitmap = Utils.decodeBase64ToImage(book.getImageBase64());
                    if (bitmap != null) {
                        ivBookImage.setImageBitmap(bitmap);
                    } else {
                        ivBookImage.setImageResource(R.drawable.no_image);
                    }
                }


                tvBookTitle.setText(book.getTitle());
                tvBookAuthor.setText(book.getAuthor());
                tvBookDescription.setText(book.getDescription());

                // Отображаем информацию о подписке
                if (book.isSubscription()) {
                    tvBookPrice.setText("Доступен по подписке");
                } else {
                    tvBookPrice.setText("Бесплатно");
                }
            }
        });

    }

    private void getBookByKey(String key, OnBookReceivedCallback callback) {
        books.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Book book = snapshot.getValue(Book.class);
                callback.onBookReceived(book);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching book details", error.toException());
            }
        });

    }
}
