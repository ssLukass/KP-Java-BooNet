package com.example.boonet.AddBook.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boonet.R;
import com.example.boonet.core.entities.Book;
import com.example.boonet.detailsBook.ui.DetailsBookActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private ImageView ivBook;
    private EditText etNameBook, etDescriptionBook, etAuthor;
    private Switch swSubscribe;
    private Button bPublish;

    private Bitmap bookImage;

    // Launcher для открытия галереи и выбора изображения
    private final ActivityResultLauncher<Intent> openGalleryResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Uri selectedImageUri = intent.getData();
                            if (selectedImageUri != null) {
                                loadImage(selectedImageUri);
                            }
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_add_book);
        initializeUI();

        // Обработчик для нажатия на изображение
        ivBook.setOnClickListener(v -> openGallery());

        // Обработчик для кнопки "Опубликовать"
        bPublish.setOnClickListener(v -> saveBookToDatabase());
    }

    // Инициализация пользовательского интерфейса
    private void initializeUI() {
        ivBook = findViewById(R.id.ivBook);
        etNameBook = findViewById(R.id.etBookName);
        etDescriptionBook = findViewById(R.id.etDescriptionBook);
        etAuthor = findViewById(R.id.etProductCity);
        swSubscribe = findViewById(R.id.switchSubscription);
        bPublish = findViewById(R.id.bPublish);

        db = FirebaseDatabase.getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    // Метод для открытия галереи
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGalleryResult.launch(intent);
    }

    // Метод для загрузки изображения из галереи
    private void loadImage(Uri selectedImageUri) {
        try (InputStream is = getContentResolver().openInputStream(selectedImageUri)) {
            bookImage = BitmapFactory.decodeStream(is);
            ivBook.setImageBitmap(bookImage);
        } catch (Exception exception) {
            Log.e("ImageError", "Ошибка загрузки изображения", exception);
            Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
        }
    }

    // Сохранение книги в Firebase
    private void saveBookToDatabase() {
        String name = etNameBook.getText().toString().trim();
        String description = etDescriptionBook.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        boolean isSubscribed = swSubscribe.isChecked();

        // Проверка на пустые поля
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(author)) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаем уникальный ключ для книги в базе данных
        DatabaseReference booksRef = db.getReference("books").push();
        String bookKey = booksRef.getKey();

        // Конвертация изображения в Base64
        String encodedImage = null;
        if (bookImage != null) {
            encodedImage = encodeImageToBase64(bookImage);
        }

        // Сбор данных о книге
        Map<String, Object> bookData = new HashMap<>();
        bookData.put("title", name);  // Используем title вместо name для соответствия классу Book
        bookData.put("description", description);
        bookData.put("author", author);
        bookData.put("subscription", isSubscribed);  // Изменили isSubscribed на subscription
        if (encodedImage != null) {
            bookData.put("imageBase64", encodedImage);
        }


        // Сохранение книги в Firebase
        booksRef.setValue(bookData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Книга успешно сохранена", Toast.LENGTH_SHORT).show();
                clearFields();

                // Переход к деталям книги
                Intent intent = new Intent(AddBookActivity.this, DetailsBookActivity.class);
                intent.putExtra("BOOK_KEY", bookKey);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Ошибка при сохранении книги", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод для конвертации изображения в строку Base64
    private String encodeImageToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // Очистка полей формы
    private void clearFields() {
        etNameBook.setText("");
        etDescriptionBook.setText("");
        etAuthor.setText("");
        swSubscribe.setChecked(false);
        ivBook.setImageResource(R.drawable.no_image); // Плейсхолдер изображения
        bookImage = null;
    }
}
