package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager;
    private SearchFragment search;
    private HomeFragment home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //my fragments that i will use
        home =  new HomeFragment();
        search = new SearchFragment();

        manager = this.getSupportFragmentManager();

        FragmentTransaction fTransaction = manager.beginTransaction();

        //adding it so that it will show
        fTransaction.add(R.id.main_layout, home).addToBackStack(null);
        fTransaction.commit();
    }

    public void onSearchClicked(View v){
        home.onSearchPressed(manager, search);
    }
}
