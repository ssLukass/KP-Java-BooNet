package com.example.boonet.home.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.boonet.R;
import com.example.boonet.core.entities.Book;
import com.example.boonet.core.exceptions.BookException;
import com.example.boonet.core.utils.SubscriptionManager;
import com.example.boonet.core.utils.Utils;
import com.example.boonet.detailsBook.ui.DetailsBookActivity;
import com.example.boonet.home.adapters.BookAdapter;
import com.example.boonet.subscribe.ui.SubscribeActivity;
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
    private SubscriptionManager subscriptionManager;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация SubscriptionManager
        subscriptionManager = new SubscriptionManager(requireContext());

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

                            Boolean isSubscribed = ds.child("subscription").getValue(Boolean.class);
                            if (isSubscribed != null) {
                                book.setSubscription(isSubscribed);
                            }

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
        try {
            validateBookAccess(book);
            openBookDetails(book);
        } catch (BookException e) {
            handleBookError(e);
        }
    }

    private boolean isUserSubscribed() {
        return subscriptionManager.isUserSubscribed();
    }

    private void validateBookAccess(Book book) throws BookException {
        if (book == null) {
            throw new BookException(BookException.BookErrorType.BOOK_NOT_FOUND);
        }

        if (book.isSubscription() && !isUserSubscribed()) {
            throw new BookException(BookException.BookErrorType.SUBSCRIPTION_REQUIRED);
        }
    }

    private void handleBookError(BookException e) {
        switch (e.getErrorType()) {
            case SUBSCRIPTION_REQUIRED:
                new AlertDialog.Builder(requireContext())
                        .setTitle("Требуется подписка")
                        .setMessage("Эта книга доступна только по подписке. Хотите оформить подписку?")
                        .setPositiveButton("Оформить подписку", (dialog, which) -> {
                            Intent intent = new Intent(requireActivity(), SubscribeActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
                break;

            case BOOK_NOT_FOUND:
                Toast.makeText(requireContext(), "Книга не найдена", Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openBookDetails(Book book) {
        if (book != null && book.getKey() != null) {
            Intent intent = new Intent(requireContext(), DetailsBookActivity.class);
            intent.putExtra("BOOK_ID", book.getKey());
            startActivity(intent);
        } else {
            Log.e(TAG, "Book key is null, cannot open details.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Очистка ссылок для предотвращения утечек памяти
        rvBooks.setAdapter(null);
    }
}