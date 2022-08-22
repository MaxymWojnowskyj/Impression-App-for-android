package com.example.impressionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    // returns an array of PersonClass instances
    private PersonClass[] getPeople() {
        PersonClass[] people = {
                new PersonClass("Peter Griffin",
                        "https://www.liveabout.com/thmb/D4sWEMjpo8wXqcQJJ5mGbsPBxII=/1500x1200/filters:no_upscale():max_bytes(150000):strip_icc()/peter_2008_v2F_hires1-56a00f083df78cafda9fdcb6.jpg"
                ),
                new PersonClass("Joe Swanson",
                        "https://www.pngitem.com/pimgs/m/159-1598041_family-guy-png-joe-swanson-png-transparent-png.png"
                )
        };
        return people;
    }

    // predefine adapter before onCreate
    SettingsRecViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        adapter = new SettingsRecViewAdapter(this, getPeople());

        //RECYCLER
        RecyclerView rv = findViewById(R.id.peopleList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());

        //SET ADAPTER
        rv.setAdapter(adapter);

    }

}