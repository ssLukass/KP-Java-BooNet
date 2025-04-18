package com.example.boonet.profile.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.boonet.MainActivity;
import com.example.boonet.addCard.ui.AddCard;
import com.example.boonet.login.ui.LoginActivity;

import com.example.boonet.R;
import com.example.boonet.core.interfaces.OnDataUserReceivedCallback;
import com.example.boonet.registration.entities.User;
import com.example.boonet.subscribe.ui.SubscribeActivity;
import com.example.boonet.subscribe.utils.SubscriptionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfileFragment extends Fragment {


    private ImageView ivAvatar;
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private TextView userName;
    private TextView userEmail, tvAddCard, tvStatus, subscriptionStatus;
    private User myUser;
    private SubscriptionManager subscriptionManager;

    private final ActivityResultLauncher<Intent> openGalleryResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Uri imageUri = intent.getData();
                        try {
                            // Загрузка изображения с помощью Glide
                            Glide.with(ProfileFragment.this)
                                    .asBitmap()
                                    .load(imageUri)
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            ivAvatar.setImageBitmap(resource);
                                          //  uploadImage(resource);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        
        subscriptionManager = new SubscriptionManager(requireContext());
        subscriptionStatus = view.findViewById(R.id.subscription_status);
        
        // Обновляем статус подписки
        updateSubscriptionStatus();

        tvAddCard.setOnClickListener(v -> {
            Log.d("ProfileFragment", "tvAddCard нажата!");
            Intent intent = new Intent(requireContext(), AddCard.class);
            startActivity(intent);
            Log.d("ProfileFragment", "startActivity вызван!");
        });

        tvStatus.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SubscribeActivity.class);
            startActivity(intent);
        });

        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            openGalleryResult.launch(intent);
        });

        Button buttonExit = view.findViewById(R.id.button_exit);
        buttonExit.setOnClickListener(v -> logoutUser());

    }

    @Override
    public void onResume() {
        super.onResume();
        updateSubscriptionStatus(); // Обновляем статус при возвращении на экран
    }

   /* private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

       StorageReference profileImages = storage.getReference("avatars");
        StorageReference currentImage = profileImages.child(System.currentTimeMillis() + "");
        UploadTask uploadTask = currentImage.putBytes(bytes);

        uploadTask.continueWithTask(task -> currentImage.getDownloadUrl())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        myUser.setAvatar(task.getResult().toString());
                        updateUserInFireBase(myUser);
                    }
                });
    }*/

    private void logoutUser() {

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("UserData", getContext().MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();


        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void updateUserInFireBase(User user) {
        DatabaseReference users = db.getReference("users");
        users.child(user.getKey()).setValue(user);
    }

    public void getDatabaseCurrentUser(OnDataUserReceivedCallback listener) {
        DatabaseReference users = db.getReference("users");
        FirebaseUser userFBAuth = auth.getCurrentUser();

        if (userFBAuth != null) {
            users.orderByChild("uid").equalTo(userFBAuth.getUid()).limitToFirst(1)
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
    }

    private void init(View view) {
        ivAvatar = view.findViewById(R.id.imageview_profile);
        db = FirebaseDatabase.getInstance("https://boonet-74b71-default-rtdb.europe-west1.firebasedatabase.app/");
        auth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.textview_userName);
        userEmail = view.findViewById(R.id.textview_userEmail);
        tvAddCard = view.findViewById(R.id.add_card);
        tvStatus = view.findViewById(R.id.status);

        getDatabaseCurrentUser(new OnDataUserReceivedCallback() {
            @Override
            public void onUserReceived(User user) {
                if (user != null) {
                    myUser = user;
                    userName.setText(user.getUserName());
                    userEmail.setText(user.getUserEmail());
                    if (user.getAvatar() != null) {
                        Glide.with(ProfileFragment.this)
                                .load(user.getAvatar())
                                .centerCrop()
                                .into(ivAvatar);
                    }
                }
            }
        });
    }

    private void updateSubscriptionStatus() {
        if (subscriptionManager.isSuperReader() && subscriptionManager.hasActiveSubscription()) {
            subscriptionStatus.setText("Статус: Super Reader ⭐");
            subscriptionStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            subscriptionStatus.setText("Статус: Обычный пользователь");
            subscriptionStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }
}
