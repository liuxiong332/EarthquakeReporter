package com.liuxiong.earthquakereporter;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by liuxi_000 on 2014/7/17.
 */
public class UserPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }
}
