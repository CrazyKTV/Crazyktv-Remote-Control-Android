package com.crazyktv.wcf.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentMenuWord extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private View view;
    private Bundle bundle;
    private Fragment fragmentSelf;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_word, container, false);
        fragmentSelf = this;
        bundle = getArguments();
        ActivityMain.TITLE_MAIN = getText(bundle.getInt(Util.LIST_TYPE));
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ActivityMain.TITLE_MAIN);
        view.findViewById(R.id.btn_word_1).setOnClickListener(wordOCL(1));
        view.findViewById(R.id.btn_word_2).setOnClickListener(wordOCL(2));
        view.findViewById(R.id.btn_word_3).setOnClickListener(wordOCL(3));
        view.findViewById(R.id.btn_word_4).setOnClickListener(wordOCL(4));
        view.findViewById(R.id.btn_word_5).setOnClickListener(wordOCL(5));
        view.findViewById(R.id.btn_word_6).setOnClickListener(wordOCL(6));
        view.findViewById(R.id.btn_word_7).setOnClickListener(wordOCL(7));
        view.findViewById(R.id.btn_word_8).setOnClickListener(wordOCL(8));
        view.findViewById(R.id.btn_word_9).setOnClickListener(wordOCL(9));
        view.findViewById(R.id.btn_word_10).setOnClickListener(wordOCL(10));
        view.findViewById(R.id.btn_menu_billboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bd = new Bundle();
                Fragment fragment = new FragmentListSong();
                bd.putInt(Util.LIST_TYPE, R.string.menu_billboard);
                bd.putString(Util.LIST_ARGUMENT, String.valueOf(bundle.getInt(Util.LIST_TYPE)));
                fragment.setArguments(bd);
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .hide(fragmentSelf)
                        .addToBackStack(Util.BACK_STACK_WORD)
                        .commit();
            }
        });
        view.findViewById(R.id.btn_menu_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bd = new Bundle();
                Fragment fragment = new FragmentListSong();
                bd.putInt(Util.LIST_TYPE, R.string.menu_new);
                bd.putString(Util.LIST_ARGUMENT, String.valueOf(bundle.getInt(Util.LIST_TYPE)));
                fragment.setArguments(bd);
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .hide(fragmentSelf)
                        .addToBackStack(Util.BACK_STACK_WORD)
                        .commit();
            }
        });
        return view;
    }
    private View.OnClickListener wordOCL(final int i){
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentListSong();
                bundle.putString(Util.LIST_ARGUMENT, String.valueOf(i));
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .hide(fragmentSelf)
                        .addToBackStack(Util.BACK_STACK_WORD)
                        .commit();
            }
        };
        return ocl;
    }

    @Override
    public void onResume() {
        super.onResume();
        //((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(bundle.getInt(Util.LIST_TYPE));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        ActivityMain.TITLE_MAIN = getText(bundle.getInt(Util.LIST_TYPE));
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ActivityMain.TITLE_MAIN);
    }
}
