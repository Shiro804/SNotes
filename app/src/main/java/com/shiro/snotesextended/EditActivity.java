package com.shiro.snotesextended;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.HashSet;

public class EditActivity extends AppCompatActivity {

    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedThemePreferences = setSharedPreferences();

        setTheme(sharedThemePreferences);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditText editText = findViewById(R.id.editText);
        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);

        if(noteId != -1) {
            editText.setText(MainActivity.notes.get(noteId));
        } else {
            MainActivity.notes.add("");
            noteId = MainActivity.notes.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }

        editTextTextChangedListener(editText);

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

    private void editTextTextChangedListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0) {
                    return;
                } else {
                    MainActivity.notes.set(noteId, String.valueOf(s));
                    MainActivity.arrayAdapter.notifyDataSetChanged();
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.shiro.snotesextended", Context.MODE_PRIVATE);
                    HashSet<String> set = new HashSet<>(MainActivity.notes);
                    sharedPreferences.edit().putStringSet("notes", set).apply();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.shiro.snotesextended", Context.MODE_PRIVATE);
                HashSet<String> set;

                if (s.toString().trim().length() == 0) {
                    MainActivity.notes.remove(noteId);
                    MainActivity.arrayAdapter.notifyDataSetChanged();
                    set = new HashSet<>(MainActivity.notes);
                    sharedPreferences.edit().putStringSet("notes", set).apply();
                }
            }
        });
    }
}
