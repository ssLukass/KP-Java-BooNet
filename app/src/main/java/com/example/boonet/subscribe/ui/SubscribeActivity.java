package com.example.boonet.subscribe.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.boonet.R;

public class SubscribeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SubscriptionPrefs";
    private static final String KEY_SUBSCRIPTION = "subscription_period";

    private CardView selectedCard = null;
    private String selectedSubscription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subscribe);


        CardView cardMonthly = findViewById(R.id.cardMonthly);
        CardView cardThreeMonths = findViewById(R.id.cardThreeMonths);
        CardView cardSixMonths = findViewById(R.id.cardSixMonths);
        CardView cardYearly = findViewById(R.id.cardYearly);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Загрузка сохраненной подписки
        loadSubscription();

        // Установка обработчиков кликов
        cardMonthly.setOnClickListener(v -> selectSubscription(cardMonthly, "1 месяц"));
        cardThreeMonths.setOnClickListener(v -> selectSubscription(cardThreeMonths, "3 месяца"));
        cardSixMonths.setOnClickListener(v -> selectSubscription(cardSixMonths, "6 месяцев"));
        cardYearly.setOnClickListener(v -> selectSubscription(cardYearly, "12 месяцев"));

        btnSave.setOnClickListener(v -> saveSubscription());
        btnCancel.setOnClickListener(v -> clearSubscription());

    }

    private void selectSubscription(CardView card, String period) {
        if (selectedCard != null) {
            selectedCard.setCardBackgroundColor(Color.WHITE); // Сбрасываем предыдущий выбор
        }
        selectedCard = card;
        selectedSubscription = period;
        card.setCardBackgroundColor(Color.LTGRAY); // Подсвечиваем выбранный вариант
    }

    private void saveSubscription() {
        if (selectedSubscription != null) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putString(KEY_SUBSCRIPTION, selectedSubscription).apply();
        }
    }

    private void loadSubscription() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedPeriod = prefs.getString(KEY_SUBSCRIPTION, null);

        if (savedPeriod != null) {
            switch (savedPeriod) {
                case "1 месяц":
                    selectSubscription(findViewById(R.id.cardMonthly), "1 месяц");
                    break;
                case "3 месяца":
                    selectSubscription(findViewById(R.id.cardThreeMonths), "3 месяца");
                    break;
                case "6 месяцев":
                    selectSubscription(findViewById(R.id.cardSixMonths), "6 месяцев");
                    break;
                case "12 месяцев":
                    selectSubscription(findViewById(R.id.cardYearly), "12 месяцев");
                    break;
            }
        }
    }

    private void clearSubscription() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().remove(KEY_SUBSCRIPTION).apply();
        if (selectedCard != null) {
            selectedCard.setCardBackgroundColor(Color.WHITE);
            selectedCard = null;
        }
        selectedSubscription = null;
    }
}
