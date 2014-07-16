package com.liuxiong.earthquakereporter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class EarthquakeActivity extends Activity {

    static final int MENU_PREFERENCES = Menu.FIRST + 1;
    static final int MENU_UPDATES = Menu.FIRST + 2;
    static final int PREFERENCE_REQUCODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.earthquake, menu);
        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
        menu.add(0, MENU_UPDATES, Menu.NONE, R.string.menu_update);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        switch (id) {
            case MENU_PREFERENCES: {
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivityForResult(i, PREFERENCE_REQUCODE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
