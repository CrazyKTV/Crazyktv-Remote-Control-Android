package com.crazyktv.wcf.android;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMenu extends Fragment {

    public FragmentMenu() {
        // Required empty public constructor
    }
    private FragmentManager fm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_menu, container, false);
            fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();

        /*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */
            transaction.replace(R.id.container, new FragmentMenuMain());
            transaction.commit();
            return view;
    }
}
