package com.example.boonet.Catalog.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.boonet.R;
import com.example.boonet.core.entities.Book;
import com.example.boonet.home.adapters.BookAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookCatalogActivity extends AppCompatActivity {
    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private FirebaseDatabase db;
    private DatabaseReference books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_catalog);

        // Инициализация Firebase
        db = FirebaseDatabase.getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
        books = db.getReference("books");

        // Инициализация RecyclerView
        rvBooks = findViewById(R.id.recyclerView);
        rvBooks.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));

        // Загружаем книги из Firebase
        getBookList();
    }

    private void getBookList() {
        books.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Book> bookList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    if (book != null) {
                        book.setKey(ds.getKey());

                        // Явно устанавливаем subscription из данных Firebase
                        Boolean isSubscribed = ds.child("subscription").getValue(Boolean.class);
                        if (isSubscribed != null) {
                            book.setSubscription(isSubscribed);
                        }

                        // Обработка изображения
                        String imageBase64 = ds.child("imageBase64").getValue(String.class);
                        if (imageBase64 != null) {
                            book.setImageBase64(imageBase64);
                        }

                        bookList.add(book);
                    }
                }

                // Логирование для проверки количества книг
                Log.d("HomeFragment", "Получены книги: " + bookList.size());

                // Проверяем, что список не пустой
                if (!bookList.isEmpty()) {
                    adapter = new BookAdapter(bookList, book -> {
                        // Ваш код для обработки клика по книге
                    });
                    rvBooks.setAdapter(adapter);  // Устанавливаем адаптер в RecyclerView
                } else {
                    Log.d("HomeFragment", "Список книг пуст");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Ошибка при получении данных: " + error.getMessage());
            }
        });
    }
}