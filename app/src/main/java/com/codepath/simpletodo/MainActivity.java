//Janill Lema - jl4817
package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //EDIT ACTIVITY
    //numeric code to represent edit activity
    public final static int EDIT_REQUEST_CODE = 20;
    //keys used to pass data between activities
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION = "itemPosition";

    ArrayList<String> items;
    //wires model (list) to the view
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemsAdapter);

        //mock data
        //items.add("1st item ");
        //items.add("2nd item");

        //calls method when the app is created
        setupListViewListener();

    }

    //add item function
    public void onAddItem(View v){

        EditText etNewItem = (EditText) findViewById(R.id.etNewitem);
        String itemText = etNewItem.getText().toString();
        //adds string val directly to items adapter to update list
        itemsAdapter.add(itemText);
        //clears etNewItem field so user can enter next new string
        etNewItem.setText("");
        writeItems();
        //displays a notification to the user
        Toast.makeText(getApplicationContext(), "Item added to list", Toast.LENGTH_SHORT).show();

    }

    //invoked by a press and hold on an item in the list
    //private since calling directly and not called by the frame work
    private void setupListViewListener(){

        Log.i("MainActivity", "Setting up Listener on list view");
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //only called once the long click is initiated on the item
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("MainActivity", "Item removed from list:" + i);
                items.remove(i);
                //only modified underlying list so need to alert adapter
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });

        //set up the listener for editing using a regular click
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //only activated when user clicks on item
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //create the new activity
                Intent inst = new Intent(MainActivity.this, EditItemActivity.class);
                //pass the data being edited
                inst.putExtra(ITEM_TEXT, items.get(i));
                inst.putExtra(ITEM_POSITION, i);
                //display the activity
                startActivityForResult(inst,EDIT_REQUEST_CODE);
            }
        });
    }

    // handle results from edit activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the edit activity completed ok
        if (resultCode==RESULT_OK && requestCode == EDIT_REQUEST_CODE){
            //extract updated item text from the result intent extra
            String updatedItem = data.getExtras().getString(ITEM_TEXT);
            //extract original position of edited item
            int position = data.getExtras().getInt(ITEM_POSITION);
            //update model with new item text at the edited position
            items.set(position,updatedItem);
            //notify adapter that item changed
            itemsAdapter.notifyDataSetChanged();
            //persist the changed model
            writeItems();
            //notify user of the completed operation
            Toast.makeText(this, "ITEM UPDATED SUCCESSFULLY",Toast.LENGTH_SHORT ).show();

        }
    }


    //Code for file persistence

    //file that allows access to stored model
    private File getDataFile(){
        return new File(getFilesDir(), "todo.txt");
    }

    //reads from the file
    private void readItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(),Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error Reading File", e);
            items = new ArrayList<>();
        }
    }

    //writes lines to files
    private void writeItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing File", e);
        }
    }
}
