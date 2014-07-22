package com.liuxiong.earthquakereporter;


import android.preference.PreferenceActivity;
import android.os.Bundle;
import java.util.List;

/**
 * Created by liuxi_000 on 2014/7/16.
 */
public class UserPreferenceActivity extends PreferenceActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }
}
