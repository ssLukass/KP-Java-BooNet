package com.example.boonet.Screen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boonet.Login.ui.LoginActivity;
import com.example.boonet.MainActivity;
import com.example.boonet.R;
import com.example.boonet.Registration.Model.UserType;
import com.example.boonet.Registration.ui.RegistrationActivity;
import com.example.boonet.core.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_SCREEN_TIME = 2500;

    private FirebaseDatabase db = FirebaseDatabase
            .getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private boolean isUserAuthorized(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isUserAuthorized()) {
                    Auth.getDatabaseCurrentUser(user -> {
                        if(user.getUserType() != UserType.ADMIN) {
                            Intent intent = new Intent(
                                    SplashScreen.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // TODO: сделать переход на другую активити для админа
                        }
                    });
                } else {
                    Intent intent = new Intent(
                            SplashScreen.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_SCREEN_TIME);

    }
}