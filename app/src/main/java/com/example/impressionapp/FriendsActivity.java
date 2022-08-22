package com.example.impressionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

public class FriendsActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;

    private String userUID;
    //private String userIconURL;
    //private String userName;

    private Button createGameBtn, joinGameBtn;
    private EditText joinGameTxt;




    //IO.Options options = IO.Options.builder()
            //.setForceNew(false)
            //.build();

    //private Socket socket = IO.socket(URI.create("https://impression-app-server.herokuapp.com/"));

    /*private Socket socket;
    {
        try {
            socket = IO.socket("https://impression-app-server.herokuapp.com/");
        } catch (URISyntaxException e) {
            Toast.makeText(FriendsActivity.this, "Socket Connection Error", Toast.LENGTH_LONG).show();
        }
    }*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        //socket.connect();
        Intent intent = getIntent();
        String toastMsg = intent.getStringExtra("toastMsg");
        // if the user cancels, leaves or gets kicked out of a game lobby and pushed back to this friends activity toast the message of what happened
        if (toastMsg != null) {
            if (!toastMsg.isEmpty()) {
                Toast.makeText(FriendsActivity.this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }

        initUser();
        initViews();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //joinedPlayers = database.getReference("joinedPlayers");

        //socket.connect();
        //socketListenersOn();

        createGameBtn.setOnClickListener(new View.OnClickListener() {
            //socket.connect();
            @Override
            public void onClick(View view) {

                Toast.makeText(FriendsActivity.this, "Creating game...", Toast.LENGTH_LONG).show();
                /*
                gameRooms: {
                    123a: {
                        players: {
                            // could maybe add player score instead of true (not intuitive, but more efficient)
                            "uid...": true,
                            "uid2...", true,
                            etc..
                        },
                        score: {}
                    },
                    d2d2: {},
                    etc..
                }
                users: {
                    "uid1": {
                        {iconURL, userName}
                    },
                    "uid2": {},
                    etc..
                }
                 */



                Map<String, Boolean> playersMap = new HashMap<>();
                playersMap.put(userUID, true);


                //String createGameData = String.format("{'user':{'iconUrl':%s, 'userName':%s}}", userIconURL, userName);
                //socket.emit("createFriendGame", createGameData);
                Toast.makeText(FriendsActivity.this, "Creating game...", Toast.LENGTH_LONG).show();

                String gameCode = "123a";

                database.getReference().child("gameRooms").child(gameCode).setValue(playersMap)
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(FriendsActivity.this, StartGameActivity.class);
                            intent.putExtra("gameCode", gameCode);
                            intent.putExtra("playerType", "host");
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(FriendsActivity.this, "Game creation failed", Toast.LENGTH_LONG).show();
                        });
            }
        });

        joinGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputGameCode = joinGameTxt.getText().toString();

                // if the join Game input is empty then exit onClick code early
                if (inputGameCode.isEmpty()) { return; }

                Toast.makeText(FriendsActivity.this, "Searching for game: "+inputGameCode, Toast.LENGTH_LONG).show();
                //socket.connect();
                joinGameTxt.setText("");

                database.getReference().child("gameRooms").child(inputGameCode).get().addOnCompleteListener(foundGame -> {
                    if (!foundGame.isSuccessful()) {
                        Toast.makeText(FriendsActivity.this, "Game Room "+inputGameCode+" does not exist", Toast.LENGTH_LONG).show();
                    } else {
                        // wont let set map to <String, Boolean> for some reason when using updateChildren
                        Map<String, Object> newPlayerMap = new HashMap<>();
                        newPlayerMap.put(userUID, true);
                        database.getReference().child("gameRooms").child(inputGameCode).updateChildren(newPlayerMap)
                                .addOnSuccessListener(aVoid -> {
                                    Intent intent = new Intent(FriendsActivity.this, StartGameActivity.class);
                                    intent.putExtra("gameCode", inputGameCode);
                                    intent.putExtra("playerType", "guest");
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e-> {
                                    Toast.makeText(FriendsActivity.this, "Game Room "+inputGameCode+" exists but join failed", Toast.LENGTH_LONG).show();
                                });
                    }

                });



                // might need to import some JSON class and create as obj if server cant parse this
                //String joinData = String.format("{'gameCode':%s, 'user':{'iconUrl':%s, 'userName':%s}}", inputGameCode, userIconURL, userName);

                //socket.emit("joinFriendGame", joinData);
            }
        });
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off("disconnect");
    }*/

    private void initUser() {
        fbAuth = FirebaseAuth.getInstance();
        if (fbAuth != null) {
            FirebaseUser user = fbAuth.getCurrentUser();
            if (user != null) {
                userUID = user.getUid();
            }
        } else {
            Toast.makeText(FriendsActivity.this, "back to main", Toast.LENGTH_LONG).show();
            startActivity(new Intent(FriendsActivity.this, MainActivity.class));
        }
    }

    private void initViews() {
        createGameBtn = findViewById(R.id.createGameBtn);
        joinGameBtn = findViewById(R.id.joinGameBtn);
        joinGameTxt = findViewById(R.id.joinGameTxt);
    }



    /*
    private void socketListenersOn() {

        socket.on("joinedFriendGame", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                FriendsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String gameCode = args[0].toString();
                        Intent intent = new Intent(FriendsActivity.this, StartGameActivity.class);
                        intent.putExtra("gameCode", gameCode);
                        intent.putExtra("playerType", "guest");
                        startActivity(intent);
                    }
                });
            }
        });

        socket.on("invalidGame", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Toast.makeText(FriendsActivity.this, "Invalid game room code", Toast.LENGTH_SHORT).show();
            }
        });

        socket.on("createdFriendGame", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String gameCode = args[0].toString();
                Intent intent = new Intent(FriendsActivity.this, StartGameActivity.class);
                intent.putExtra("gameCode", gameCode);
                intent.putExtra("playerType", "host");
                startActivity(intent);
            }
        });

    }*/

}