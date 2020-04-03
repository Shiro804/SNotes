package com.shiro.snotesextended;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    //public static boolean isCurrentModeDark;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        SharedPreferences sharedPreferences = setSharedPreferences();

        if (item.getItemId() == R.id.change_theme) {
            if (!sharedPreferences.getBoolean("isDarkMode", false)) {
                sharedPreferences.edit().putBoolean("isDarkMode", true).apply();
                restartApp();
            } else {
                sharedPreferences.edit().putBoolean("isDarkMode", false).apply();
                restartApp();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedThemePreferences = setSharedPreferences();

        setTheme(sharedThemePreferences);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        FloatingActionButton addNotesButton = findViewById(R.id.addNotesButton);

        SharedPreferences sharedPreferences = setSharedPreferences();

        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("notes", null);

        set = setAndNotesNullCheck(sharedPreferences, set);

        notes = new ArrayList<>(set);

        notesLengthEqualsZeroCheck(sharedPreferences);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, notes);

        listView.setAdapter(arrayAdapter);

        listViewItemOnClickListener(listView);

        listViewItemOnLongClickListener(listView);

        addNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(intent);
            }
        });

    }

    private SharedPreferences setSharedPreferences() {
        return getApplicationContext().getSharedPreferences("com.shiro.snotesextended", MODE_PRIVATE);
    }

    private void setTheme(SharedPreferences sharedThemePreferences) {
        if (sharedThemePreferences.getBoolean("isDarkMode", false)) {
            setTheme(R.style.AppThemeDark);
            sharedThemePreferences.edit().putBoolean("isDarkMode", true).apply();
        } else {
            setTheme(R.style.AppTheme);
            sharedThemePreferences.edit().putBoolean("isDarkMode", false).apply();
        }
    }

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void listViewItemOnLongClickListener(ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(MainActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Are you sure?").setMessage("Do you want to delete this note?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notes.remove(position);
                        arrayAdapter.notifyDataSetChanged();

                        SharedPreferences sharedPreferences = setSharedPreferences();
                        HashSet<String> set = new HashSet<>(notes);
                        sharedPreferences.edit().putStringSet("notes", set).apply();
                    }
                })
                        .setNegativeButton("No", null).show();

                return true;
            }
        });
    }

    private void listViewItemOnClickListener(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("noteId", position);
                startActivity(intent);
            }
        });
    }

    private void notesLengthEqualsZeroCheck(SharedPreferences sharedPreferences) {
        HashSet<String> set;
        if (notes.size() == 0) {
            notes.add("Welcome!");
            set = new HashSet<>(notes);
            sharedPreferences.edit().putStringSet("notes", set).apply();
        }
    }

    private HashSet<String> setAndNotesNullCheck(SharedPreferences sharedPreferences, HashSet<String> set) {
        if (set == null) {
            if (notes != null) {
                set = new HashSet<>(notes);
            } else {
                notes = new ArrayList<>();
                notes.add("Welcome!");
                set = new HashSet<>(notes);
                sharedPreferences.edit().putStringSet("notes", set).apply();
            }
        }
        return set;
    }
}