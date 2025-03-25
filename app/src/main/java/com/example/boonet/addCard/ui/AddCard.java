package com.example.boonet.addCard.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boonet.R;
import java.util.Calendar;

public class AddCard extends AppCompatActivity {

    private EditText cardNumberEditText, cardHolderEditText, expiryDateEditText, cvvEditText;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "CardData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        cardHolderEditText = findViewById(R.id.cardHolderEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        Button payButton = findViewById(R.id.payButton);
        Button unlinkCardButton = findViewById(R.id.unlinkCardButton);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        setupCardNumberFormatting();
        setupCardHolderValidation();
        setupExpiryDateValidation();

        loadSavedCardData();

       payButton.setOnClickListener(v -> saveCardData());
        unlinkCardButton.setOnClickListener(v -> clearCardData());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("card_added", true);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }

    // 1️⃣ Авто-форматирование номера карты: "1234 5678 9000 0000"
    private void setupCardNumberFormatting() {
        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String cleanText = s.toString().replaceAll("\\s", "");
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < cleanText.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(cleanText.charAt(i));
                }

                cardNumberEditText.setText(formatted.toString());
                cardNumberEditText.setSelection(formatted.length());

                isEditing = false;
            }
        });
    }

    // 2️⃣ Валидация имени владельца (только латиница, авто-заглавные буквы)
    private void setupCardHolderValidation() {
        cardHolderEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String newText = s.toString().replaceAll("[^A-Za-z ]", "").toUpperCase();
                if (!s.toString().equals(newText)) {
                    cardHolderEditText.setText(newText);
                    cardHolderEditText.setSelection(newText.length());
                }
            }
        });
    }

    // 3️⃣ Валидация даты (формат MM/YY, не раньше текущего месяца)
    private void setupExpiryDateValidation() {
        expiryDateEditText.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String cleanText = s.toString().replaceAll("[^0-9]", "");
                if (cleanText.length() > 4) {
                    cleanText = cleanText.substring(0, 4);
                }

                String formatted = "";
                if (cleanText.length() >= 2) {
                    formatted = cleanText.substring(0, 2) + "/";
                    if (cleanText.length() > 2) {
                        formatted += cleanText.substring(2);
                    }
                } else {
                    formatted = cleanText;
                }

                expiryDateEditText.setText(formatted);
                expiryDateEditText.setSelection(formatted.length());

                isEditing = false;
            }
        });

        expiryDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateExpiryDate();
        });
    }

    private void validateExpiryDate() {
        String expiryText = expiryDateEditText.getText().toString();
        if (!expiryText.matches("\\d{2}/\\d{2}")) {
            expiryDateEditText.setError("Неверный формат (MM/YY)");
            return;
        }

        int month = Integer.parseInt(expiryText.substring(0, 2));
        int year = Integer.parseInt(expiryText.substring(3, 5)) + 2000;
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1;

        if (month < 1 || month > 12) {
            expiryDateEditText.setError("Неверный месяц");
        } else if (year < currentYear || (year == currentYear && month < currentMonth)) {
            expiryDateEditText.setError("Карта уже недействительна");
        }
    }

    // 4️⃣ Сохранение данных карты
    private void saveCardData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cardNumber", cardNumberEditText.getText().toString());
        editor.putString("cardHolder", cardHolderEditText.getText().toString());
        editor.putString("expiryDate", expiryDateEditText.getText().toString());
        editor.putString("cvv", cvvEditText.getText().toString());
        editor.apply();
        Toast.makeText(this, "Карта сохранена", Toast.LENGTH_SHORT).show();
    }

    // 5️⃣ Загрузка сохраненных данных
    private void loadSavedCardData() {
        cardNumberEditText.setText(preferences.getString("cardNumber", ""));
        cardHolderEditText.setText(preferences.getString("cardHolder", ""));
        expiryDateEditText.setText(preferences.getString("expiryDate", ""));
        cvvEditText.setText(preferences.getString("cvv", ""));
    }

    // 6️⃣ Очистка данных (отвязка карты)
    private void clearCardData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        cardNumberEditText.setText("");
        cardHolderEditText.setText("");
        expiryDateEditText.setText("");
        cvvEditText.setText("");

        Toast.makeText(this, "Карта отвязана", Toast.LENGTH_SHORT).show();
    }
}
