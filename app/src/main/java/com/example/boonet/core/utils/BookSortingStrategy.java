package com.example.boonet.core.utils;

import com.example.boonet.core.entities.Book;
import java.util.Comparator;
import com.example.boonet.core.utils.SortType;

public class BookSortingStrategy<T extends Book> {

    public Comparator<T> getSortingComparator(SortType sortType) {
        switch (sortType) {
            case TITLE:
                return (b1, b2) -> {
                    if (b1.getTitle() == null) return (b2.getTitle() == null) ? 0 : 1;
                    if (b2.getTitle() == null) return -1;
                    return b1.getTitle().compareToIgnoreCase(b2.getTitle());
                };
            case AUTHOR:
                return (b1, b2) -> {
                    if (b1.getAuthor() == null) return (b2.getAuthor() == null) ? 0 : 1;
                    if (b2.getAuthor() == null) return -1;
                    return b1.getAuthor().compareToIgnoreCase(b2.getAuthor());
                };
            case SUBSCRIPTION:
                return (b1, b2) -> Boolean.compare(b2.isSubscription(), b1.isSubscription());
            case DATE:
                return (b1, b2) -> Long.compare(b2.getCreationDate(), b1.getCreationDate());
            default:
                return (b1, b2) -> 0;
        }
    }
} 