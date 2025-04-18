package com.example.boonet.subscribe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SubscriptionManager {
    private static final String PREFS_NAME = "SubscriptionPrefs";
    private static final String KEY_SUBSCRIPTION = "subscription_period";
    private static final String KEY_IS_SUPER_READER = "is_super_reader";
    private static final String KEY_SUBSCRIPTION_START = "subscription_start";
    private static final String KEY_SUBSCRIPTION_END = "subscription_end";

    private final SharedPreferences prefs;

    public SubscriptionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void activateSubscription(String period) {
        SharedPreferences.Editor editor = prefs.edit();
        long currentTime = System.currentTimeMillis();
        long endTime = calculateEndTime(currentTime, period);
        
        editor.putString(KEY_SUBSCRIPTION, period);
        editor.putBoolean(KEY_IS_SUPER_READER, true);
        editor.putLong(KEY_SUBSCRIPTION_START, currentTime);
        editor.putLong(KEY_SUBSCRIPTION_END, endTime);
        editor.apply();
    }

    public boolean isSuperReader() {
        return prefs.getBoolean(KEY_IS_SUPER_READER, false);
    }

    public boolean hasActiveSubscription() {
        long endTime = prefs.getLong(KEY_SUBSCRIPTION_END, 0);
        return endTime > System.currentTimeMillis();
    }

    private long calculateEndTime(long startTime, String period) {
        long daysToAdd;
        switch (period) {
            case "1 месяц":
                daysToAdd = 30;
                break;
            case "3 месяца":
                daysToAdd = 90;
                break;
            case "6 месяцев":
                daysToAdd = 180;
                break;
            case "12 месяцев":
                daysToAdd = 365;
                break;
            default:
                daysToAdd = 0;
                break;
        }
        return startTime + (daysToAdd * 24 * 60 * 60 * 1000);
    }

    public void deactivateSubscription() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_SUPER_READER, false);
        editor.remove(KEY_SUBSCRIPTION);
        editor.remove(KEY_SUBSCRIPTION_START);
        editor.remove(KEY_SUBSCRIPTION_END);
        editor.apply();
    }
} 