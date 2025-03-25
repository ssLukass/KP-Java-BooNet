package com.example.boonet.home.interfaces;

import com.example.boonet.core.entities.Book;

import java.util.ArrayList;

public interface BookListCallback {
    void onListReceived(ArrayList<Book> books);
}
