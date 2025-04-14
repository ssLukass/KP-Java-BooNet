package com.example.boonet.admin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boonet.AddBook.ui.AddBookActivity;
import com.example.boonet.Catalog.ui.BookCatalogActivity;
import com.example.boonet.R;
import com.example.boonet.UserList.ui.UserListActivity;

public class AdminMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        Button addBookButton = findViewById(R.id.btn_add_book);
        Button viewCatalogButton = findViewById(R.id.btn_view_catalog);
        Button viewUsersButton = findViewById(R.id.btn_view_users);

        addBookButton.setOnClickListener( v->{
            Intent intent = new Intent(AdminMenuActivity.this, AddBookActivity.class);
            startActivity(intent);
        });

        viewCatalogButton.setOnClickListener(v->{
            Intent intent = new Intent(AdminMenuActivity.this, BookCatalogActivity.class);
            startActivity(intent);
        });

        viewUsersButton.setOnClickListener(v->{
            Intent intent = new Intent(AdminMenuActivity.this, UserListActivity.class);
            startActivity(intent);
        });
    }
}
