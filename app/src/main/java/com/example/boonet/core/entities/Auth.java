package com.example.boonet.core.entities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.boonet.core.interfaces.OnDataUserReceivedCallback;
import com.example.boonet.core.interfaces.OnSignInCallback;
import com.example.boonet.registration.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Auth {
    public static FirebaseDatabase db = FirebaseDatabase
            .getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
    public static DatabaseReference users = db.getReference("users");
    public static User currentUser;
    public static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void getDatabaseCurrentUser(OnDataUserReceivedCallback listener) {
        DatabaseReference users = db.getReference("users");
        FirebaseUser userFBAuth = auth.getCurrentUser();

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    Log.d("sllslslsls", user.getUserEmail());
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (userFBAuth != null ) {
            Log.d("ASKASKKASKAS", userFBAuth.getUid());
            users.orderByChild("uid").equalTo(userFBAuth.getUid()).limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("ASKASKKASKAS", snapshot.getKey());
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                User user = ds.getValue(User.class);
                                listener.onUserReceived(user); // Pass the user object to the listener
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("ASKASKKASKAS","inet off");
                        }
                    });
        }
    }

    public static FirebaseUser getCurrentUserFBAuth() {
        return auth.getCurrentUser();
    }

    public static void getUserByKey(String key, OnDataUserReceivedCallback listener) {
        users.orderByChild("key").equalTo(key).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            listener.onUserReceived(user);
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void signIn(Context context, String email, String passwd, OnSignInCallback l) {
        auth.signInWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Auth.getDatabaseCurrentUser(new OnDataUserReceivedCallback() {
                                @Override
                                public void onUserReceived(User user) {
                                    Log.d("inSignIn", "onUserReceived: " + user.getKey());
                                    SharedPreferences sp = context.getSharedPreferences("UserData",
                                            MODE_PRIVATE);
                                    sp.edit().putString("KEY", user.getKey()).apply();
                                    currentUser = user;
                                    l.onSignIn(true);
                                }
                            });

                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception ex) {
                                l.onSignIn(false);
                            }
                        }
                    }
                });
    }

    public static void updateUserInFireBase(User user) {
        users.child(user.getKey()).setValue(user);
        currentUser = user;
    }
}