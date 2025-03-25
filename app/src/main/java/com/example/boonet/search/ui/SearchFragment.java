package com.example.boonet.search.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boonet.core.entities.Book;
import com.example.boonet.detailsBook.ui.DetailsBookActivity;
import com.example.boonet.search.adapters.SearchAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private EditText etSearch;
    private RecyclerView rvSearchResults;
    private SearchAdapter searchAdapter;
    private List<Book> bookList;

    private FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.example.boonet.R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(com.example.boonet.R.id.etSearch);
        rvSearchResults = view.findViewById(com.example.boonet.R.id.rvSearchResults);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        bookList = new ArrayList<>();
        searchAdapter = new SearchAdapter(bookList, new SearchAdapter.BookClickCallback() {
            @Override
            public void onClick(Book book) {
                Intent intent = new Intent(requireActivity(), DetailsBookActivity.class);
                intent.putExtra("PRODUCT_ID", book.getKey());
                startActivity(intent);
            }
        });
        rvSearchResults.setAdapter(searchAdapter);

        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchProducts(String query) {

        final String lowerCaseQuery = query.toLowerCase();
        DatabaseReference productsRef = db.getReference("products");
        productsRef.orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    if (book != null && book.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                        bookList.add(book);
                        Log.d(TAG, "Product found: " + book.getTitle());
                    } else {
                        Log.d(TAG, "Product does not match query: " + book.getTitle());
                    }
                }
                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
}
