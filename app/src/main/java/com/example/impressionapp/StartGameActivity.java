package com.example.impressionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StartGameActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth fbAuth;
    private String userUID;

    private TextView gameRoomTxt, gameCodeTxt;

    private Button startGameBtn, cancelGameBtn, leaveGameBtn;


    /*
    private Socket socket;
    {
        try {
            socket = IO.socket("https://impression-app-server.herokuapp.com/");
        } catch (URISyntaxException e) {
            Toast.makeText(StartGameActivity.this, "Socket Connection Error", Toast.LENGTH_LONG).show();
        }
    }
    */


    List<PlayerClass> players = new ArrayList<>();

    StartGameRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);
        Intent intent = getIntent();
        String gameCode = intent.getStringExtra("gameCode");

        initUser();

        //socket.connect();
        //socketListenersOn();
        // if the gameCode is not empty then we know we are playing a friends mode game
        if (!gameCode.isEmpty()) {
            //socket.emit("getPlayers", gameCode);
            initFriendModeViews(gameCode);
            String playerType = intent.getStringExtra("playerType");
            if (playerType.equals("host")) {
                initHostViews(gameCode);
            } else initGuestViews(gameCode);
        } // here would be else where we handle random multiplayer mode (add later)

        database = FirebaseDatabase.getInstance();

        initPlayersDB(gameCode);


        adapter = new StartGameRVAdapter(this, players);

        //RECYCLER
        RecyclerView rv = findViewById(R.id.joinedPlayersRView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());

        //SET ADAPTER
        rv.setAdapter(adapter);

    }

    private void initUser() {
        fbAuth = FirebaseAuth.getInstance();
        if (fbAuth != null) {
            FirebaseUser user = fbAuth.getCurrentUser();
            if (user != null) {
                userUID = user.getUid();
            }
        } else {
            Toast.makeText(StartGameActivity.this, "back to main", Toast.LENGTH_LONG).show();
            startActivity(new Intent(StartGameActivity.this, MainActivity.class));
        }
    }

    private void initPlayersDB(String gameCode) {
        database.getReference().child("gameRooms").child(gameCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // should be <"userUID", true>
                Map<String, Object> playersList = (HashMap<String, Object>) dataSnapshot.getValue();
                // if the list does not exists this means we have just cancelled the game so the app is already joining the friends game activity and we can return out of this event listener
                if (playersList == null) { return; }
                for (String playerUID : playersList.keySet()) {
                    // for loop runs multiple get requests but doesnt wait until they complete before moving on
                    database.getReference().child("users").child(playerUID).get().addOnCompleteListener(playerData -> {
                        if (playerData.isSuccessful()) {
                            Map<String, Object> player = (HashMap<String, Object>) playerData.getResult().getValue();
                            Map.Entry<String, Object> entry = player.entrySet().iterator().next();
                            String playerName = entry.getKey();
                            String iconURL = entry.getValue().toString();
                            // the google iconURL does not contain and image file ending (ex: .jpg) so the GLIDE import cannot display it. Using this stock goose image for now
                            players.add(new PlayerClass("https://art.pixilart.com/d7495f504f0167a.png", playerName));
                            // notify the adapter that the data in our last has changed to it should re initialize (refresh)
                            adapter.notifyDataSetChanged();
                        } //else {} // this shouldn't happen just pass by if it does in some odd case

                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(StartGameActivity.this, "get players in gameRoom was unsuccessful", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initFriendModeViews(String gameCode) {
        gameRoomTxt = findViewById(R.id.gameRoomTxt);
        gameCodeTxt = findViewById(R.id.gameCodeTxt);
        gameRoomTxt.setVisibility(View.VISIBLE);
        gameCodeTxt.setVisibility(View.VISIBLE);
        gameCodeTxt.setText(gameCode);
    }

    private void initHostViews(String gameCode) {
        startGameBtn = findViewById(R.id.startGameBtn);
        cancelGameBtn = findViewById(R.id.cancelGameBtn);
        startGameBtn.setEnabled(true);
        cancelGameBtn.setEnabled(true);
        startGameBtn.setVisibility(View.VISIBLE);
        cancelGameBtn.setVisibility(View.VISIBLE);


        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(StartGameActivity.this, LoadImpressionActivity.class);
                //startActivity(intent);
            }
        });

        cancelGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //socket.emit("cancelGame", gameCode);

                database.getReference().child("gameRooms").child(gameCode).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(StartGameActivity.this, FriendsActivity.class);
                            intent.putExtra("toastMsg", "You cancelled the game");
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(StartGameActivity.this, "Game cancellation failed", Toast.LENGTH_LONG).show();
                        });
            }
        });

    }
    private void initGuestViews(String gameCode) {
        leaveGameBtn = findViewById(R.id.leaveGameBtn);
        leaveGameBtn.setEnabled(true);
        leaveGameBtn.setVisibility(View.VISIBLE);

        leaveGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //socket.emit("leftGame", gameCode);

                database.getReference().child("gameRooms").child(gameCode).child(userUID).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(StartGameActivity.this, FriendsActivity.class);
                            intent.putExtra("toastMsg", "You left the game");
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(StartGameActivity.this, "Game leave failed", Toast.LENGTH_LONG).show();
                        });


            }
        });
    }

    /*
    private void socketListenersOn() {

        // for now just remove all players from recy view and update from allPlayers json from server
        // prob more efficient to have individual playerAdded and playerRemoved socket but for now full reset will do (less buggy)
        socket.on("updatePlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String jsonString = args[0].toString();
                //TODO: make server just send players array instead of {players:[], score:{}}
                JSONObject gameOBJ = null;
                try {
                    gameOBJ = new JSONObject(jsonString);
                    JSONArray jsonPlayers = gameOBJ.getJSONArray("players"); // `"players": [...]`
                    // remove all the previous players in the player list as we are refreshing the list
                    players.clear();
                    ArrayList<PlayerClass> updatedPlayers = new ArrayList<>();
                    //iterate over jsonPlayers and add each player to updatedPlayers ArrayList
                    for (int i=0; i < jsonPlayers.length(); i++) {
                        JSONObject jsonPlayer = jsonPlayers.getJSONObject(i);
                        String jsonIconURL = jsonPlayer.getString("iconURL");
                        String jsonUserName = jsonPlayer.getString("userName");
                        updatedPlayers.add(new PlayerClass(jsonIconURL, jsonUserName));
                    }
                    // add all the fetched players that have joined the gameroom to the players List
                    players.addAll(updatedPlayers);
                    // notify the adapter that the data in our last has changed to it should re initialize (refresh)
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("gameCancelled", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(StartGameActivity.this, FriendsActivity.class);
                intent.putExtra("toastMsg", "The game was cancelled");
                startActivity(intent);
            }
        });
    }
    */
}

