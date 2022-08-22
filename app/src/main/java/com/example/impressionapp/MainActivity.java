package com.example.impressionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SignInButton googleSignInBtn;
    GoogleSignInClient signInClient;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // TODO: put webclient in R.string get somewhere
                .requestIdToken("1087116543407-g2jr3dbigooulbaop995hfsold8gpnci.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);

        fbAuth = FirebaseAuth.getInstance();
        if (fbAuth != null) {
            //FirebaseUser fbUser = fbAuth.getCurrentUser();
            if (fbAuth.getCurrentUser() != null) {
                Toast.makeText(MainActivity.this, "fb user exists", Toast.LENGTH_LONG).show();

                // check if user already in database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseUser user = fbAuth.getCurrentUser();
                String userUID = user.getUid();

                database.getReference().child("users").child(userUID).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // user is not found in database so upload them
                        Map<String, String> userInfo = new HashMap<String, String>();
                        userInfo.put(user.getDisplayName(), user.getPhotoUrl().toString());
                        Map<String, Object> userKey = new HashMap<String, Object>();
                        userKey.put(userUID, userInfo);

                        database.getReference().child("users").updateChildren(userKey)
                                .addOnSuccessListener(aVoid -> {
                                    // user is already logged in and uploading of user data to database is successful then redirect to home page
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(MainActivity.this, "Already logged in on google but database user upload is unsuccessful retrying..", Toast.LENGTH_LONG).show();
                                    // the user db upload is unsuccessful then redirect back to main activity page to try db upload again
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                    finish();
                                });

                    }
                    else {
                        // user is found in database so redirect to home page
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    }
                });

            }
        }


        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "google clicked", Toast.LENGTH_LONG).show();
                Intent intent = signInClient.getSignInIntent();
                loginActivityResultLauncher.launch(intent);
            }
        });
    }


    ActivityResultLauncher<Intent> loginActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        //Log.w(TAG, "Google sign in failed", e);
                    }
                }
            }
    );

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fbAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, authResult -> {
                /*
                    users: {
                        "uid1": {
                            {userName, iconURL}
                        },
                        "uid2": {},
                        etc..
                    }
                 */

                    //upload user to database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseUser user = fbAuth.getCurrentUser();
                    Map<String, String> userInfo = new HashMap<String, String>();
                    userInfo.put(user.getDisplayName(), user.getPhotoUrl().toString());
                    Map<String, Object> userKey = new HashMap<String, Object>();
                    userKey.put(user.getUid(), userInfo);

                    database.getReference().child("users").updateChildren(userKey)
                            .addOnSuccessListener(aVoid -> {
                                // google login is successful and uploading of user data to database is successful then redirect to home page
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // if the google login is successful but the user db upload is unsuccessful then redirect back to main activity page to try db upload again
                                Toast.makeText(MainActivity.this, "Google login success but database user upload is unsuccessful retrying..", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                finish();
                            });

                })
                .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this, "Firebase Authentication failed.",
                        Toast.LENGTH_SHORT).show());
    }


    private void initViews() {
        googleSignInBtn = findViewById(R.id.googleSignInBtn);
    }


}