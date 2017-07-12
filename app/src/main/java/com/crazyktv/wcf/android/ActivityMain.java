package com.crazyktv.wcf.android;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Locale;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class ActivityMain extends ActionBarActivity implements MaterialTabListener {
    static String PREF_URI;
    static CharSequence TITLE_MAIN;
    static boolean IS_TABLET, PREF_LONG_PRESS_CUT_SONG, PREF_LONG_PRESS_REPLAY_SONG, PREF_SHOW_CONTROL_ON_MAIN, PREF_AUTO_REFRESH_QUEUE, PREF_NEW_IN_SEARCH_GUARANTEE;
    static int PREF_UI_LANG, PREF_NEW_IN_DAYS, PREF_BILLBOARD_LEAST_PLAY_COUNT, PREF_NUMBER_LENGTH, PREF_QUEUE_REFRESH_FREQUENCY;
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;
    private MaterialTabHost tabHost;
    private Resources res;
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private Button btnCut;
    private RequestQueue mQueue;
    static Vibrator myVibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        res = this.getResources();
        getAppPreferences();

        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        this.setSupportActionBar(toolbar);
        mQueue = Volley.newRequestQueue(this);
        myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        getIsTablet();
        if(IS_TABLET) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
            pager = (ViewPager) this.findViewById(R.id.pager);
            pager.setOffscreenPageLimit(2);
            // init view pager
            pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(pagerAdapter);
            pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    // when user do a swipe the selected tab change
                    tabHost.setSelectedNavigationItem(position);
                    toolbar.setTitle(pagerAdapter.getPageTitle(position));
                    //on change tab hide soft keyboard by search fragment arg
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if(inputMethodManager.isActive()){
                        Log.d("imm", "open");
                        try
                        {
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        catch (Exception e)
                        {
                            // Ignore exceptions if any
                            Log.e("KeyBoardUtil", e.toString(), e);
                        }
                    }
                }
            });
            // insert all tabs from pagerAdapter data
            for (int i = 0; i < pagerAdapter.getCount(); i++) {
                tabHost.addTab(
                        tabHost.newTab()
                                .setIcon(getIcon(i))
                                .setTabListener(this)
                );
            }
            pager.setCurrentItem(1);
            if(PREF_SHOW_CONTROL_ON_MAIN) {
                btnCut = (Button) findViewById(R.id.btn_cut);
                ((Button) findViewById(R.id.btn_vocal)).setOnClickListener(controlOCL());
                ((Button) findViewById(R.id.btn_fixed)).setOnClickListener(controlOCL());
            }else{
                LinearLayout ll = ((LinearLayout) this.findViewById(R.id.linear_control));
                ((ViewGroup) ll.getParent()).removeView(ll);
            }
        }
        TITLE_MAIN = getText(R.string.title_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(this, ActivitySettings.class);
                startActivity(intent);
                return true;
            //case R.id.action_about:
                //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }
    FragmentQueue fragmentQueue = new FragmentQueue();
    FragmentMenu fragmentMenu = new FragmentMenu();
    FragmentNumber fragmentNumber = new FragmentNumber();
    FragmentControl fragmentControl = new FragmentControl();
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public Fragment getItem(int num) {
            switch(num) {
                case 0: return fragmentQueue;
                case 1: return fragmentMenu;
                case 2: return fragmentNumber;
                case 3: return fragmentControl;
                default: return null;
            }
        }
        @Override
        public int getCount() {
            return 4;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return getText(R.string.title_queue);
                case 1: return TITLE_MAIN;
                case 2: return getText(R.string.title_number);
                case 3: return getText(R.string.title_control);
                default: return null;
            }
        }
    }
  /*
    * It doesn't matter the color of the icons, but they must have solid colors
    */
    private Drawable getIcon(int position) {
        switch(position) {
            case 0:
                return res.getDrawable(R.mipmap.ic_queue_music_black_24dp);
            case 1:
                return res.getDrawable(R.mipmap.ic_book_24dp);
            case 2:
                return res.getDrawable(R.mipmap.ic_number_24dp);
            case 3:
                return res.getDrawable(R.mipmap.ic_tune_black_24dp);
        }
        return null;
    }

    private void getIsTablet(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double size = Math.sqrt(x+y);
        Log.d("Screen Size", String.valueOf(size));
        if(size > 7){
            IS_TABLET = true;
            PREF_SHOW_CONTROL_ON_MAIN = false;
        }else {
            IS_TABLET = false;
        }
    }

    private void getAppPreferences(){
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        String HOST = sp.getString(getResources().getString(R.string.key_host), getString(R.string.default_pref_host).toString());
        String PORT = sp.getString(getResources().getString(R.string.key_port), getString(R.string.default_pref_port).toString());
        PREF_URI = "http://" + HOST + ":" + PORT + "/";
        PREF_UI_LANG = Integer.valueOf(sp.getString(getResources().getString(R.string.key_ui_lang), "0"));
        if(PREF_UI_LANG != 0){
            Locale locale;
            switch (PREF_UI_LANG){
                case 2:
                    locale = new Locale("es");
                    break;
                case 3:
                    locale = Locale.CHINA;
                    break;
                case 4:
                    locale = Locale.TAIWAN;
                    break;
                default:
                    locale = Locale.ENGLISH;
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
        }
        PREF_LONG_PRESS_CUT_SONG = sp.getBoolean(getResources().getString(R.string.key_long_press_cut_song), false);
        PREF_LONG_PRESS_REPLAY_SONG = sp.getBoolean(getResources().getString(R.string.key_long_press_replay_song), false);
        PREF_SHOW_CONTROL_ON_MAIN = sp.getBoolean(getResources().getString(R.string.key_show_control_on_main), false);
        PREF_NEW_IN_DAYS = Integer.parseInt(sp.getString(getResources().getString(R.string.key_new_in_days), "30"));
        PREF_NEW_IN_SEARCH_GUARANTEE = sp.getBoolean(getResources().getString(R.string.key_new_in_search_guarantee), true);
        PREF_BILLBOARD_LEAST_PLAY_COUNT = Integer.parseInt(sp.getString(getResources().getString(R.string.key_billboard_least_play_count), "3"));
        PREF_NUMBER_LENGTH = Integer.parseInt(sp.getString(getResources().getString(R.string.key_number_length), "5"));
        PREF_AUTO_REFRESH_QUEUE = sp.getBoolean(getResources().getString(R.string.key_auto_refresh_queue), true);
        PREF_QUEUE_REFRESH_FREQUENCY = Integer.parseInt(sp.getString(getResources().getString(R.string.key_queue_refresh_frequency), "180")) * 1000;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(btnCut != null){
            if(ActivityMain.PREF_LONG_PRESS_CUT_SONG){
                btnCut.setOnClickListener(null);
                btnCut.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        String arg = "DoCrazyKTV_Action?state=Cut";
                        arg = ActivityMain.PREF_URI + arg;
                        Log.d("Control", arg);
                        StringRequest stringRequest = new StringRequest(arg,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("RESPONSE", response.toString());
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("TAG", error.getMessage(), error);
                                    }
                                });
                        mQueue.add(stringRequest);
                        myVibrator.vibrate(150);
                        return false;
                    }
                });
            }else {
                btnCut.setOnLongClickListener(null);
                btnCut.setOnClickListener(controlOCL());
            }
        }
    }

    private long exitTime = 0;
    @Override
    public void onBackPressed() {

        if((IS_TABLET || (pager.getCurrentItem() == 1)) && (getSupportFragmentManager().getBackStackEntryCount() != 0)){
                super.onBackPressed();
        }else{
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(),
                        R.string.string_click_back_one_more_time_to_quit, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else {
                super.onBackPressed();
            }
        }

    }
    private View.OnClickListener controlOCL(){
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String arg = "";
                switch (v.getId()){
                    case R.id.btn_cut:
                        arg = "DoCrazyKTV_Action?state=Cut";
                        break;
                    case R.id.btn_vocal:
                        arg = "DoCrazyKTV_Control?state=Channel";
                        break;
                    case R.id.btn_fixed:
                        arg = "DoCrazyKTV_Control?state=Fixed";
                        break;
                    default:
                }
                arg = ActivityMain.PREF_URI + arg;
                Log.d("Control", arg);
                StringRequest stringRequest = new StringRequest(arg,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                                Log.d("RESPONSE", response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("TAG", error.getMessage(), error);
                            }
                        });
                mQueue.add(stringRequest);
                myVibrator.vibrate(150);
            }
        };

        return ocl;
    }
}
