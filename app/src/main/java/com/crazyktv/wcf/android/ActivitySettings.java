package com.crazyktv.wcf.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class ActivitySettings extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = true;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        //setupActionBar();
    }
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_network);

        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_ui);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_ui);

        // Add 'notifications' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_request);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_request);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_host)));
        bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_port)));
        bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_ui_lang)));
        bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_new_in_days)));
        bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_billboard_least_play_count)));
        bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_number_length)));
        bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_queue_refresh_frequency)));
        findPreference(getResources().getString(R.string.key_show_control_on_main)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Toast.makeText(preference.getContext(), R.string.string_restart_app_after_change, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_header, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
                if (preference.getKey().equals("key_ui_lang") && (index != ActivityMain.PREF_UI_LANG)) {
                    Toast.makeText(preference.getContext(), R.string.string_restart_app_after_change, Toast.LENGTH_SHORT).show();
                }
            }else if (preference.getKey().equals("key_port")) {
                if(Integer.parseInt(stringValue) > 65535){
                    Toast.makeText(preference.getContext(), R.string.toast_port_error, Toast.LENGTH_SHORT).show();
                    return false;
                }
                preference.setSummary(stringValue);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NetworkPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_network);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_host)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_port)));
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UIPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_ui);

            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_ui_lang)));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class RequestPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_request);

            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_new_in_days)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_billboard_least_play_count)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_number_length)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.key_queue_refresh_frequency)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String HOST = sp.getString(getResources().getString(R.string.key_host), getString(R.string.default_pref_host).toString());
        String PORT = sp.getString(getResources().getString(R.string.key_port), getString(R.string.default_pref_port).toString());
        ActivityMain.PREF_URI = "http://" + HOST + ":" + PORT + "/";
        ActivityMain.PREF_UI_LANG = Integer.valueOf(sp.getString(getResources().getString(R.string.key_ui_lang), "0"));
        ActivityMain.PREF_LONG_PRESS_CUT_SONG = sp.getBoolean(getResources().getString(R.string.key_long_press_cut_song), false);
        ActivityMain.PREF_LONG_PRESS_REPLAY_SONG = sp.getBoolean(getResources().getString(R.string.key_long_press_replay_song), false);
        ActivityMain.PREF_NEW_IN_DAYS = Integer.parseInt(sp.getString(getResources().getString(R.string.key_new_in_days), "30"));
        ActivityMain.PREF_NEW_IN_SEARCH_GUARANTEE = sp.getBoolean(getResources().getString(R.string.key_new_in_search_guarantee), true);
        ActivityMain.PREF_BILLBOARD_LEAST_PLAY_COUNT = Integer.parseInt(sp.getString(getResources().getString(R.string.key_billboard_least_play_count), "3"));
        ActivityMain.PREF_NUMBER_LENGTH = Integer.parseInt(sp.getString(getResources().getString(R.string.key_number_length), "5"));
        ActivityMain.PREF_AUTO_REFRESH_QUEUE = sp.getBoolean(getResources().getString(R.string.key_auto_refresh_queue), true);
        ActivityMain.PREF_QUEUE_REFRESH_FREQUENCY = Integer.parseInt(sp.getString(getResources().getString(R.string.key_queue_refresh_frequency), "180")) * 1000;
    }

}
