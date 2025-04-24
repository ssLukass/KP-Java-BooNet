package com.example.boonet.screen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boonet.admin.ui.AdminMenuActivity;
import com.example.boonet.login.ui.LoginActivity;
import com.example.boonet.MainActivity;
import com.example.boonet.R;
import com.example.boonet.registration.entities.UserType;
import com.example.boonet.core.entities.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    private static final int SPLASH_SCREEN_TIME = 2500;


    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private boolean isUserAuthorized() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null;
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

        new Handler().postDelayed(() -> {
            Log.d(TAG, "Checking user authorization...");

            if (isUserAuthorized()) {
                Log.d(TAG, "User is authorized, retrieving database user...");

                Auth.getDatabaseCurrentUser(user -> {
                    Log.d("ASDLKAKLSDKLASDK", "ne doshli");
                    if (user == null) {
                        Log.e(TAG, "User is null, redirecting to LoginActivity");
                        startLoginActivity();
                        return;
                    }

                    Log.d(TAG, "UserType: " + user.getUserType());

                    if (user.getUserType() != UserType.ADMIN) {
                        startMainActivity();
                    } else {
                        startAdminActivity();
                    }
                });
            } else {
                Log.d(TAG, "User is not authorized, redirecting to LoginActivity");
                startLoginActivity();
            }
        }, SPLASH_SCREEN_TIME);
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startAdminActivity() {
        if (isFinishing()) return;
        Log.d(TAG, "Admin detected. TODO: переход на админскую активность.");
        Intent intent = new Intent(SplashScreenActivity.this, AdminMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity() {
        if (isFinishing()) return;
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}