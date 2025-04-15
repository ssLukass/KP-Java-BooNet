package com.example.boonet.home.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
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
    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private FirebaseDatabase db;
    private DatabaseReference books;

    public HomeFragment() {
        super(R.layout.fragment_home);  // Указываем layout для фрагмента
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация Firebase
        db = FirebaseDatabase.getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
        books = db.getReference("books");

        // Инициализация RecyclerView
        rvBooks = view.findViewById(R.id.recyclerView);
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
                        book.setKey(ds.getKey()); // Устанавливаем ключ книги
                        if (book.getImageBase64() != null) {
                            // Декодируем изображение из Base64 в Bitmap
                            Bitmap bitmap = Utils.decodeBase64ToImage(book.getImageBase64());
                            // Конвертируем обратно в Base64 после обработки, если необходимо
                            String base64Image = Utils.encodeImageToBase64(bitmap);
                            book.setImageBase64(base64Image); // Устанавливаем конвертированное изображение обратно
                        }
                        bookList.add(book);
                    }
                }

                // Логирование для проверки количества книг
                Log.d("HomeFragment", "Получены книги: " + bookList.size());

                // Проверяем, что список не пустой
                if (bookList.isEmpty()) {
                    Log.d("HomeFragment", "Нет данных для отображения.");
                } else {
                    // Создаем адаптер и передаем список книг и слушатель кликов
                    adapter = new BookAdapter(bookList, book -> {
                        // Ваш код для обработки клика по книге
                    });

                    // Устанавливаем адаптер в RecyclerView
                    rvBooks.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Ошибка при получении данных: " + error.getMessage());
            }
        });
    }
}
