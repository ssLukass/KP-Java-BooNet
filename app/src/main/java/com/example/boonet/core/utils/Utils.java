package com.example.boonet.core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class Utils {

    // Метод для конвертации изображения в Base64 строку
    public static String encodeImageToBase64(Bitmap image) {
        if (image == null) {
            Log.e("askaskaskask", "Image is null");
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        String base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.d("askaskaskask", "Image encoded to Base64");

        return base64String;
    }

    // Метод для конвертации строки Base64 обратно в Bitmap
    public static Bitmap decodeBase64ToImage(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            Log.e("askaskaskask", "Base64 string is null or empty");
            return null;
        }

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        if (bitmap == null) {
            Log.e("askaskaskask", "Failed to decode Base64 string to Bitmap");
        } else {
            Log.d("askaskaskask", "Image decoded from Base64");
        }

        return bitmap;
    }
}
