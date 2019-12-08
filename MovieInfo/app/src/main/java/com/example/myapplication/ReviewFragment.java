package com.example.myapplication;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.os.ParcelFileDescriptor.MODE_APPEND;
import static java.lang.Integer.parseInt;


public class ReviewFragment extends Fragment {

    private String title;
    private String id;
    EditText userRev;
    Context context;


    public ReviewFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView =  inflater.inflate(R.layout.fragment_review, container, false);
        context = myView.getContext();
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        id = bundle.getString("id");

        userRev = myView.findViewById(R.id.userrev);

        TextView movieTitle = myView.findViewById(R.id.title);
        movieTitle.setText(title);
        return myView;
    }

    public void onPost(){
        String review = userRev.getText().toString();
        writeToFile(review);
        HomeFragment.reviews.add(review);
        MainActivity.home.addReview();
        String text = readFromFile();
        System.out.println("testing file");
        System.out.println(text);
        userRev.setText("");
    }

    // writing to file
    private void writeToFile(String data) {

        String prev = readFromFile();
        data = title + ": " + data;

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("reviews.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(prev + data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("reviews.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    if(receiveString != ""){
                        stringBuilder.append(receiveString + "--");
                    }
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }



}
