package com.example.boonet.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SubscriptionManager {
    private static final String PREFS_NAME = "SubscriptionPrefs";
    private static final String KEY_SUBSCRIPTION = "subscription_period";
    private final SharedPreferences prefs;

    public SubscriptionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isUserSubscribed() {
        String subscription = prefs.getString(KEY_SUBSCRIPTION, null);
        return subscription != null && !subscription.isEmpty();
    }

    public void setSubscription(String subscriptionPeriod) {
        prefs.edit().putString(KEY_SUBSCRIPTION, subscriptionPeriod).apply();
    }

    public void clearSubscription() {
        prefs.edit().remove(KEY_SUBSCRIPTION).apply();
    }

    public String getSubscriptionPeriod() {
        return prefs.getString(KEY_SUBSCRIPTION, null);
    }
} 