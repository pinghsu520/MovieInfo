package com.example.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.R;

import java.util.ArrayList;


public class ContactFragment extends Fragment {

    // Variables used throughout the program
    private Activity containerActivity = null;

    private ListView contactsListView;
    ArrayAdapter<String> contactsAdapter = null;
    private ArrayList<String> contacts = new ArrayList<String>();
    ListView contactList;
    String email;
    String overview;
    String title;
    private boolean populated = false;

    public ContactFragment() { }

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }


    @Override
    // This method is called when the view is created and defines the parent view as well as
    // assigns the view that is the list the adapter will populate
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        contactList = v.findViewById(R.id.contact_list_view);
        Bundle bundle = getArguments();
        overview = bundle.getString("overview");
        title = bundle.getString("title");
        return v;
    }

    @Override
    // calling get contacts method on fragment creation
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        getContacts();
    }

    @Override
    // This method is called just before the UI becomes available ot the user
    // it first calls setupContactsAdapter() which populates the list view with all the
    // contacts then defines the on click for each contact which starts the intent to share
    // the image with a specific contact
    public void onResume() {
        super.onResume();

        if (!populated){
            setupContactsAdapter(); // populating list view

            // defining on click for each contact in the adapter
            contactList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3){
                    // creating bundle and launching next fragment

                    // getting the email for a contact
                    String contactId = Integer.toString(position + 1);
                    System.out.println(contactId);
                    Cursor emails = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID +
                                    " = " + contactId, null, null);
                    if (emails.moveToNext()) {
                        email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

                    }
                    emails.close();
                    // creating intent to start email activity
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("vnd.android.cursor.dir/email");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
                    intent.putExtra(Intent.EXTRA_TEXT, overview);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Check out: " + title);
                    startActivity(intent);

                }

            });
        }

    }
    // This method updates a list of contacts with all contacts obtained through the
    // content provider
    public void getContacts() {
        contacts.clear();
        Cursor cursor = containerActivity.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String given = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contact = given + " :: " + contactId;
            contacts.add(contact);
        }
        cursor.close();

    }

    // This method uses the list of contacts to update the contact list via adapter
    private void setupContactsAdapter() {
        contactsListView =
                (ListView)containerActivity.findViewById(R.id.contact_list_view);
        contactsAdapter = new
                ArrayAdapter<String>(containerActivity, R.layout.text_row,
                R.id.text_row_text_view, contacts);
        contactsListView.setAdapter(contactsAdapter);
    }

}
