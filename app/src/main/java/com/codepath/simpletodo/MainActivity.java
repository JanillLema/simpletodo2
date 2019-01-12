//Janill Lema - jl4817
package com.codepath.simpletodo;

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
