package com.liuxiong.earthquakereporter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * Created by liuxi_000 on 2014/7/16.
 */
public class PreferencesActivity extends Activity {
    CheckBox  autoUpdateCheckbox;
    Spinner updateFreqSpinner;
    Spinner magnitudeSpinner;
    SharedPreferences prefs;

    public static final String USER_PREFERENCE = "USE_PREFERENCE";
    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    public static final String PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX";
    public static final String PREF_UPDATE_FREQ_INDEX = "PREF_UPDATE_FREQ_INDEX";
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        autoUpdateCheckbox = (CheckBox)findViewById(R.id.auto_update_checkbox);
        updateFreqSpinner = (Spinner)findViewById(R.id.update_freq_spinner);
        magnitudeSpinner = (Spinner)findViewById(R.id.quake_mag_spinner);

        populateSpinners();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        updateUIFromPreference();

        Button okButton = (Button)findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    savePreferences();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        );
        Button cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    void savePreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_AUTO_UPDATE, autoUpdateCheckbox.isChecked());
        editor.putInt(PREF_UPDATE_FREQ_INDEX, updateFreqSpinner.getSelectedItemPosition());
        editor.putInt(PREF_MIN_MAG_INDEX, magnitudeSpinner.getSelectedItemPosition());
        editor.apply();
    }
    void populateSpinners() {
        ArrayAdapter<CharSequence> fAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.update_freq_options,
                        android.R.layout.simple_spinner_item);
        updateFreqSpinner.setAdapter(fAdapter);

        ArrayAdapter<CharSequence>  mAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.magnitude_options,
                        android.R.layout.simple_spinner_item);
        magnitudeSpinner.setAdapter(mAdapter);
    }

    void updateUIFromPreference() {
        boolean autoUpdateChecked = prefs.getBoolean(PREF_AUTO_UPDATE, false);
        int updateFreqIndex = prefs.getInt(PREF_UPDATE_FREQ_INDEX, 2);
        int minMagIndex = prefs.getInt(PREF_MIN_MAG_INDEX, 0);
        autoUpdateCheckbox.setChecked(autoUpdateChecked);
        updateFreqSpinner.setSelection(updateFreqIndex);
        magnitudeSpinner.setSelection(minMagIndex);
    }
}
