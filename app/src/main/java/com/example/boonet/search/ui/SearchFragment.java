package com.example.boonet.search.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boonet.R;
import com.example.boonet.core.entities.Book;
import com.example.boonet.core.exceptions.BookException;
import com.example.boonet.core.utils.SubscriptionManager;
import com.example.boonet.core.utils.Utils;
import com.example.boonet.detailsBook.ui.DetailsBookActivity;
import com.example.boonet.search.adapters.SearchAdapter;
import com.example.boonet.subscribe.ui.SubscribeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private EditText etSearch;
    private RecyclerView rvSearchResults;
    private SearchAdapter searchAdapter;
    private List<Book> allBooks;
    private List<Book> filteredBooks;
    private RadioGroup sortGroup;
    private DatabaseReference booksRef;
    private SubscriptionManager subscriptionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        sortGroup = view.findViewById(R.id.sortGroup);
        
        // Инициализация списков
        allBooks = new ArrayList<>();
        filteredBooks = new ArrayList<>();
        
        // Настройка RecyclerView
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SearchAdapter(filteredBooks, this::onBookClick);
        rvSearchResults.setAdapter(searchAdapter);
        
        // Инициализация Firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
        booksRef = db.getReference("books");
        subscriptionManager = new SubscriptionManager(requireContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        loadBooks();
    }

    private void setupListeners() {
        // Слушатель изменения текста поиска
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchAndSortBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Слушатель изменения способа сортировки
        sortGroup.setOnCheckedChangeListener((group, checkedId) -> 
            searchAndSortBooks(etSearch.getText().toString()));
    }

    private void loadBooks() {
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allBooks.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        Book book = ds.getValue(Book.class);
                        if (book != null) {
                            book.setKey(ds.getKey());
                            Boolean isSubscribed = ds.child("subscription").getValue(Boolean.class);
                            if (isSubscribed != null) {
                                book.setSubscription(isSubscribed);
                            }
                            // Обработка изображения
                            String imageBase64 = ds.child("imageBase64").getValue(String.class);
                            if (imageBase64 != null && !imageBase64.isEmpty()) {
                                book.setImageBase64(imageBase64);
                                Bitmap bitmap = Utils.decodeBase64ToImage(imageBase64);
                                if (bitmap != null) {
                                    book.setImageBase64(Utils.encodeImageToBase64(bitmap));
                                }
                            }
                            allBooks.add(book);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing book: " + e.getMessage());
                    }
                }
                // После загрузки выполняем начальный поиск
                searchAndSortBooks(etSearch.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void searchAndSortBooks(String query) {
        filteredBooks.clear();
        String searchQuery = query.toLowerCase().trim();

        // Поиск книг
        for (Book book : allBooks) {
            if (bookMatchesSearch(book, searchQuery)) {
                filteredBooks.add(book);
            }
        }

        // Сортировка результатов
        sortBooksByCurrentCriteria();
        
        // Обновление списка
        searchAdapter.notifyDataSetChanged();
    }

    private boolean bookMatchesSearch(Book book, String query) {
        if (book == null || query == null) return false;

        return (book.getTitle() != null && book.getTitle().toLowerCase().contains(query)) ||
               (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(query)) ||
               (book.getDescription() != null && book.getDescription().toLowerCase().contains(query));
    }

    private void sortBooksByCurrentCriteria() {
        int selectedSortOption = sortGroup.getCheckedRadioButtonId();
        Comparator<Book> comparator;
        
        if (selectedSortOption == R.id.sortByTitle) {
            comparator = Comparator.comparing(
                Book::getTitle,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
        } else if (selectedSortOption == R.id.sortByAuthor) {
            comparator = Comparator.comparing(
                Book::getAuthor,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
        } else if (selectedSortOption == R.id.sortBySubscription) {
            comparator = (b1, b2) -> Boolean.compare(b2.isSubscription(), b1.isSubscription());
        } else {
            // По умолчанию сортируем по названию
            comparator = Comparator.comparing(
                Book::getTitle,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
        }
        
        Collections.sort(filteredBooks, comparator);
    }

    private void onBookClick(Book book) {
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
        Intent intent = new Intent(requireActivity(), DetailsBookActivity.class);
        intent.putExtra("BOOK_ID", book.getKey());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Очистка для предотвращения утечек памяти
        rvSearchResults.setAdapter(null);
    }
}
