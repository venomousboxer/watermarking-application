package com.example.venomousboxer.collegespaceapplication;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/*
 * Created by venomousboxer on 19/01/18.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_watermark);
    }
}
