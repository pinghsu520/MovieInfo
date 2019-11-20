package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //my fragments that i will use
        HomeFragment home =  new HomeFragment();

        //getting my transaction and beginning
        FragmentManager transaction = this.getSupportFragmentManager();
        FragmentTransaction fTransaction = transaction.beginTransaction();

        //adding it so that it will show
        fTransaction.add(R.id.main_layout, home).addToBackStack(null);
        fTransaction.commit();
    }
}
