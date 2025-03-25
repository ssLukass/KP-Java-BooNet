package com.example.boonet.detailsBook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.boonet.R;
import com.example.boonet.core.entities.Book;
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
                if (TextUtils.isEmpty(book.getImage())) {
                    Glide.with(this).load(R.drawable.no_image).into(ivBookImage);
                } else {
                    Glide.with(this)
                            .load(book.getImage())
                            .into(ivBookImage);
                }
                tvBookTitle.setText(book.getTitle());
                tvBookPrice.setText(String.format("₸%d", book.getPrice()));
                tvBookAuthor.setText(book.getAuthor());
                tvBookDescription.setText(book.getDescription());
            }
        });
    }

    private void getBookByKey(String key, OnBookReceivedCallback callback) {
        books.orderByChild("key").equalTo(key).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                            Book book = bookSnapshot.getValue(Book.class);
                            callback.onBookReceived(book);
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error fetching book details", error.toException());
                    }
                });
    }
}
