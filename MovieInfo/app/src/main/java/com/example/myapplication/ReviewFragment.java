/*
 * @author: Mario Verdugo. Ping Hsu, Nathon Smith
 * @description: This is the review fragment. this is used to review a movie. a edittext and
 * button are displayed
 */
package com.example.myapplication;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private EditText userRev;
    private Context context;


    public ReviewFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView =  inflater.inflate(R.layout.fragment_review, container, false);

        //setting the onclick of the button
        Button button = myView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onPost();
            }
        });

        //getting the title and id from the bundle
        context = myView.getContext();
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        id = bundle.getString("id");

        //getting the editetext
        userRev = myView.findViewById(R.id.userrev);

        //getting the movietitle and setting it
        TextView movieTitle = myView.findViewById(R.id.title);
        movieTitle.setText(title);
        return myView;
    }

    /**
     * the onclick funciton that transitions to the original home fragment.
     */
    public void onPost(){
        //getting the text of the review and writing it to the file.
        String review = userRev.getText().toString();
        writeToFile(review);
        //adding the review to the home fragment
        MainActivity.home.addReview(review);
        userRev.setText("");

        Helpers.transition(this, MainActivity.home);
        //switching fragments
        FragmentManager manager = MainActivity.manager;
        manager.beginTransaction().replace(R.id.main_layout, MainActivity.home).addToBackStack(null).commit();

    }

    /**
     * writing the new review to the file
     * @param data the review.
     */
    private void writeToFile(String data) {

        //prev is all the other contents of the file
        String prev = Helpers.readFromFile(context);
        data = title + ": " + data;

        //writing to the file
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("reviews.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(prev + data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


}
