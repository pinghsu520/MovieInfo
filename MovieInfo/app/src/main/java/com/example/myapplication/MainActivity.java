package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager manager;
    public static HomeFragment home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //the manager to use for everything
        manager = this.getSupportFragmentManager();

        //we need an instance of the home fragment for the review fragment
        home = new HomeFragment();
        FragmentTransaction fTransaction = manager.beginTransaction();
        //adding it so that it will show the home screen
        fTransaction.add(R.id.main_layout, home);
        fTransaction.commit();
    }
}
