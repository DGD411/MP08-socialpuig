package com.example.motorbike_puig;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileFragment extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 1;
    private ImageView profileImageView;
    private EditText nameEditText, emailEditText;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        profileImageView = view.findViewById(R.id.profile_image);
        nameEditText = view.findViewById(R.id.name);
        emailEditText = view.findViewById(R.id.email);

        // Set existing user information
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        nameEditText.setText(user.getDisplayName());
        emailEditText.setText(user.getEmail());

        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl().toString())
                    .circleCrop()
                    .into(profileImageView);
        }

        // Set click listeners for updating user information
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        Button updateButton = view.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            updateUserPhoto(imageUri);
        }
    }

    private void updateUserPhoto(Uri imageUri) {
        user.updateProfile(new UserProfileChangeRequest.Builder()
                        .setPhotoUri(imageUri)
                        .build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EditProfileFragment", "User photo updated");
                        }
                    }
                });
    }

    private void updateUserInfo() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EditProfileFragment", "User email updated");
                            updateUserName(name);
                        } else {
                            // Handle errors
                        }
                    }
                });
    }

    private void updateUserName(String name) {
        user.updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EditProfileFragment", "User name updated");
                            // Save user info to Firestore
                            saveUserInfoToFirestore(name, user.getEmail());
                            // Navigate back to the profile fragment
                            Navigation.findNavController(requireView()).navigateUp();
                        } else {
                            // Handle errors
                        }
                    }
                });
    }

    private void saveUserInfoToFirestore(String name, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Create a new user document in the "users" collection
        CollectionReference usersRef = db.collection("users");
        usersRef.document(user.getUid()).set(new User(name, email, user.getPhotoUrl().toString()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MainActivity", "User information saved to Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "Error saving user information to Firestore", e);
                    }
                });
    }
}