package com.example.boonet.Login.ui;

import static com.example.boonet.core.Auth.updateUserInFireBase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boonet.MainActivity;
import com.example.boonet.R;
import com.example.boonet.Registration.Model.User;
import com.example.boonet.Registration.Model.UserType;
import com.example.boonet.Registration.ui.RegistrationActivity;
import com.example.boonet.core.Auth;
import com.example.boonet.core.OnDataUserReceivedCallback;
import com.example.boonet.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseDatabase db = FirebaseDatabase
            .getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private boolean isUserAuthorized(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvDontHaveAccount.setOnClickListener(v -> {
            Intent intent = RegistrationActivity.newIntent(this);
            startActivity(intent);
        });

        binding.loginButton.setOnClickListener(v -> {
            validateData();
        });
    }

    private void validateData() {
        String userEmail = binding.emailInput.getText().toString();
        String userPassword = binding.passwordInput.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, getString(R.string.Enter_Email), Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, getString(R.string.Enter_Password), Toast.LENGTH_SHORT).show();
        } else {
            loginUser(userEmail, userPassword);
        }
    }


    private void loginUser(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Auth.getDatabaseCurrentUser(user -> {
                    updateUserInFireBase(user);

                    // Save user is logged in
                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidCredentialsException ex) {
                    Toast.makeText(LoginActivity.this,
                            getString(R.string.Incorrect_Email_Or_Password),
                            Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(LoginActivity.this,
                            getString(R.string.Undefined_Error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}