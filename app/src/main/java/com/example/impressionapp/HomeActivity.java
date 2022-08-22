package com.example.impressionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    GoogleSignInClient signInClient;
    private FirebaseAuth fbAuth;
    private TextView userNameText;
    private ImageView userPfpImg;
    private Button playOnlineBtn, playFriendsBtn, settingsBtn, logOutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUser();
        initViews();

        playFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbAuth.signOut();
                //signInClient.signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void initUser() {
        fbAuth = FirebaseAuth.getInstance();
        if (fbAuth != null) {
            FirebaseUser user = fbAuth.getCurrentUser();
            if (user != null) {
                userNameText = findViewById(R.id.userNameText);
                userPfpImg = findViewById(R.id.userPfpImg);
                String iconURL = user.getPhotoUrl().toString();
                System.out.println(iconURL);
                userNameText.setText(user.getDisplayName());
                Toast.makeText(HomeActivity.this, "icon url: "+user.getPhotoUrl().toString(), Toast.LENGTH_LONG).show();
                Glide.with(HomeActivity.this).load(user.getPhotoUrl()).into(userPfpImg);
            }
        } else {
            Toast.makeText(HomeActivity.this, "back to main", Toast.LENGTH_LONG).show();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
    }

    private void initViews() {
        logOutBtn = findViewById(R.id.logOutBtn);
        playOnlineBtn = findViewById(R.id.playOnlineBtn);
        playFriendsBtn = findViewById(R.id.playFriendsBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
    }
}