package com.example.boonet.subscribe.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.boonet.R;
import com.example.boonet.subscribe.utils.SubscriptionManager;
import com.example.boonet.core.exceptions.PaymentException;

public class SubscribeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SubscriptionPrefs";
    private static final String KEY_SUBSCRIPTION = "subscription_period";
    private static final String CARD_PREF_NAME = "CardData";

    private SubscriptionManager subscriptionManager;
    private CardView selectedCard = null;
    private String selectedSubscription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subscribe);

        subscriptionManager = new SubscriptionManager(this);

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

    private boolean isCardLinked() {
        SharedPreferences cardPrefs = getSharedPreferences(CARD_PREF_NAME, MODE_PRIVATE);
        String cardNumber = cardPrefs.getString("cardNumber", "");
        return !cardNumber.isEmpty();
    }

    private void saveSubscription() {
        try {
            validateSubscription();
            activateSubscription();
            Toast.makeText(this, "Подписка успешно оформлена!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (PaymentException e) {
            handlePaymentError(e);
        }
    }

    private void validateSubscription() throws PaymentException {
        if (!isCardLinked()) {
            throw new PaymentException(PaymentException.PaymentErrorType.NO_CARD_LINKED);
        }
        
        if (hasActiveSubscription()) {
            throw new PaymentException(PaymentException.PaymentErrorType.SUBSCRIPTION_ALREADY_ACTIVE);
        }
    }

    private void handlePaymentError(PaymentException e) {
        switch (e.getErrorType()) {
            case NO_CARD_LINKED:
                new AlertDialog.Builder(this)
                        .setTitle("Ошибка")
                        .setMessage("Сначала привяжите банковскую карту в профиле")
                        .setPositiveButton("Перейти в профиль", (dialog, which) -> finish())
                        .setNegativeButton("Отмена", null)
                        .show();
                break;
            
            case SUBSCRIPTION_ALREADY_ACTIVE:
                Toast.makeText(this, "У вас уже есть активная подписка", Toast.LENGTH_SHORT).show();
                finish();
                break;
            
            default:
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void activateSubscription() {
        if (selectedSubscription != null) {
            subscriptionManager.activateSubscription(selectedSubscription);
        }
    }

    private boolean hasActiveSubscription() {
        return subscriptionManager.hasActiveSubscription();
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
        subscriptionManager.deactivateSubscription();
        if (selectedCard != null) {
            selectedCard.setCardBackgroundColor(Color.WHITE);
            selectedCard = null;
        }
        selectedSubscription = null;
        Toast.makeText(this, "Подписка отменена", Toast.LENGTH_SHORT).show();
    }
}
