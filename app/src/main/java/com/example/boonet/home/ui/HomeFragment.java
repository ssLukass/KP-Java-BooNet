package com.example.boonet.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.boonet.R;
import com.example.boonet.core.entities.Book;
import com.example.boonet.detailsBook.ui.DetailsBookActivity;
import com.example.boonet.home.adapters.BookAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {
    private FirebaseDatabase db;
    private RecyclerView rvBooks;
    private DatabaseReference books;
    private BookAdapter adapter;

    private void init(View view) {
        db = FirebaseDatabase.getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
        books = db.getReference("books");
        rvBooks = view.findViewById(R.id.rv_books);
        adapter = new BookAdapter(book -> {
            Intent intent = new Intent(requireActivity(), DetailsBookActivity.class);
            intent.putExtra("BOOK_ID", book.getKey());
            startActivity(intent);
        });

        rvBooks.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        rvBooks.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        getProductList();
    }

    private void getProductList() {
        books.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Book> bookList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    if (book != null) {
                        bookList.add(book);
                    }
                }
                Collections.reverse(bookList);
                adapter.setList(bookList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Логируем ошибку
            }
        });
    }
}
