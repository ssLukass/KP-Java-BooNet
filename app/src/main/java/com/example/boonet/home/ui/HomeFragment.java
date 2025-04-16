package com.example.boonet.home.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.boonet.R;
import com.example.boonet.core.entities.Book;
import com.example.boonet.core.utils.Utils;
import com.example.boonet.home.adapters.BookAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final int SPAN_COUNT = 2;

    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private DatabaseReference booksRef;
    private final List<Book> bookList = new ArrayList<>();

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация RecyclerView
        rvBooks = view.findViewById(R.id.recyclerView);
        rvBooks.setLayoutManager(new StaggeredGridLayoutManager(SPAN_COUNT, LinearLayoutManager.VERTICAL));

        // Инициализация адаптера с пустым списком
        adapter = new BookAdapter(bookList, this::onBookClicked);
        rvBooks.setAdapter(adapter);

        // Инициализация Firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
        booksRef = db.getReference("books");

        // Загружаем книги из Firebase
        loadBooks();
    }

    private void loadBooks() {
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        Book book = ds.getValue(Book.class);
                        if (book != null) {
                            book.setKey(ds.getKey()); // Устанавливаем ключ книги

                            // Обработка изображения
                            String imageBase64 = ds.child("imageBase64").getValue(String.class);
                            if (imageBase64 != null && !imageBase64.isEmpty()) {
                                book.setImageBase64(imageBase64);
                                // Декодируем и кодируем обратно для проверки валидности
                                Bitmap bitmap = Utils.decodeBase64ToImage(imageBase64);
                                if (bitmap != null) {
                                    String processedBase64 = Utils.encodeImageToBase64(bitmap);
                                    book.setImageBase64(processedBase64);
                                }
                            }
                            bookList.add(book);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing book data: " + e.getMessage(), e);
                    }
                }

                Log.d(TAG, "Loaded books: " + bookList.size());

                if (bookList.isEmpty()) {
                    Log.d(TAG, "Book list is empty");
                    // Здесь можно показать сообщение об отсутствии книг
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage(), error.toException());
                // Здесь можно показать сообщение об ошибке
            }
        });
    }

    private void onBookClicked(Book book) {
        // Обработка клика по книге
        // Например, открытие детальной информации о книге
        Log.d(TAG, "Book clicked: " + book.getTitle());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Очистка ссылок для предотвращения утечек памяти
        rvBooks.setAdapter(null);
    }
}