package com.example.tio.callnumber;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // ListView contact
    private ListView lstNames;

    // Array for contact
    ArrayList<DataModel> contacts;

    // Adapter Custom
    private static CustomAdapter adapter;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the list view
        this.lstNames = (ListView) findViewById(R.id.lstNames);

        // Read and show the contacts
        showContacts();
    }

    /**
     * Read the name & number of all the contacts.
     *
     * @return a list of names & numbers.
     */
    private ArrayList<DataModel> getContactNames() {
        // inisialisasi object contact
        contacts = new ArrayList<>();

        // Get the ContentResolver
        ContentResolver cr = getContentResolver();

        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {

            // Iterate through the cursor
            do {
                // Get the contacts id
                String id =cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                // Get the contacts number
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                // create object phoneNumber
                String phoneNumber = "-";
                Uri pURI = Uri.parse("https://tpc.googlesyndication.com/simgad/7257550639006830488");
                if (hasPhone.equalsIgnoreCase("1")) {


                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                            null, null);
                    phones.moveToFirst();

                    //image
//                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
//                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));
                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                    phoneNumber = phones.getString(phones.getColumnIndex("data1"));
//                    Bitmap photo = null;
//                    if (inputStream != null) {
//                        photo = BitmapFactory.decodeStream(inputStream);
//                    }

                }

                // Get the contacts name
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                // add to array
                contacts.add(new DataModel(name,phoneNumber,pURI));
            } while (cursor.moveToNext());
        }

        // Close the curosor
        cursor.close();

        return contacts;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            final ArrayList<DataModel> dataModels = getContactNames();
            adapter = new CustomAdapter(dataModels,getApplicationContext());
            lstNames.setAdapter(adapter);

            lstNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DataModel dataModel= dataModels.get(position);
                    Toast.makeText(getApplicationContext(), dataModel.getName()+"\n Phone Number: "+dataModel.getNumber(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
