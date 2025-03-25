package com.example.boonet.registration.ui;

import static com.example.boonet.core.entities.Auth.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boonet.MainActivity;
import com.example.boonet.R;
import com.example.boonet.registration.entities.User;
import com.example.boonet.core.entities.Auth;
import com.example.boonet.databinding.ActivityRegistrationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;

    public static Intent newIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerButton.setOnClickListener( v -> {
            validateData();
        });
    }

    private void validateData() {
        String userEmail = binding.etEmail.getText().toString();
        String userName = binding.etUsername.getText().toString();
        String createPassword = binding.etPassword.getText().toString();
        String repeatPassword = binding.etRepeatPassword.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, getString(R.string.Enter_Email), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, getString(R.string.Enter_User_Name), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(createPassword)) {
            Toast.makeText(this, getString(R.string.Enter_Create_Password), Toast.LENGTH_SHORT).show();
        } else if (!createPassword.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.Password_uncorrect), Toast.LENGTH_SHORT).show();
        } else {
            registerUser(userName, userEmail, createPassword);
        }
    }


    private void registerUser(String userName, String userEmail, String userPassword) {
        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(
                        userEmail, userPassword
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String UID = auth.getUid();
                        createAccount(userName, userEmail, UID);
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException ex) {
                            Toast.makeText(RegistrationActivity.this,
                                    getString(R.string.Not_New_Email),
                                    Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthInvalidCredentialsException ex) {
                            if (ex.getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                                Toast.makeText(RegistrationActivity.this,
                                        getString(R.string.Invalid_Email),
                                        Toast.LENGTH_SHORT).show();
                            } else if (ex.getErrorCode().equals("ERROR_WEAK_PASSWORD")) {
                                Toast.makeText(RegistrationActivity.this,
                                        getString(R.string.Weak_Password),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Log.d("kaslkdkladklad", ex.toString());
                            Toast.makeText(RegistrationActivity.this,
                                    getString(R.string.Undefined_Error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createAccount(String userName, String userEmail, String UID) {
        DatabaseReference users = FirebaseDatabase
                .getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .push();

        User user = new User(userName, userEmail, UID);
        String key = users.getKey();
        user.setKey(key);

        users.setValue(user);

        Auth.getDatabaseCurrentUser(us -> {
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}