package com.crazyktv.wcf.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMenuLang extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private View view;
    private FragmentManager fragmentManager;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("LifeCycle", "onCreateView");
        view = inflater.inflate(R.layout.fragment_menu_lang, container, false);
        fragmentManager = getFragmentManager();
        bundle = getArguments();
        ActivityMain.TITLE_MAIN = (bundle.getInt(Util.LIST_TYPE) == R.string.menu_chorus
                || bundle.getInt(Util.LIST_TYPE) == R.string.menu_billboard
                || bundle.getInt(Util.LIST_TYPE) == R.string.menu_new)?getText(bundle.getInt(Util.LIST_TYPE)): getText(R.string.menu_lang);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ActivityMain.TITLE_MAIN);
        view.findViewById(R.id.btn_orderby_lang_chinese).setOnClickListener(langOCL(R.string.menu_lang_chinese));
        view.findViewById(R.id.btn_orderby_lang_taiwanese).setOnClickListener(langOCL(R.string.menu_lang_taiwanese));
        view.findViewById(R.id.btn_orderby_lang_cantonese).setOnClickListener(langOCL(R.string.menu_lang_cantonese));
        view.findViewById(R.id.btn_orderby_lang_japanese).setOnClickListener(langOCL(R.string.menu_lang_japanese));
        view.findViewById(R.id.btn_orderby_lang_english).setOnClickListener(langOCL(R.string.menu_lang_english));
        view.findViewById(R.id.btn_orderby_lang_korea).setOnClickListener(langOCL(R.string.menu_lang_korea));
        view.findViewById(R.id.btn_orderby_lang_hakka).setOnClickListener(langOCL(R.string.menu_lang_hakka));
        view.findViewById(R.id.btn_orderby_lang_aborigine).setOnClickListener(langOCL(R.string.menu_lang_aborigine));
        view.findViewById(R.id.btn_orderby_lang_children).setOnClickListener(langOCL(R.string.menu_lang_children));
        view.findViewById(R.id.btn_orderby_lang_other).setOnClickListener(langOCL(R.string.menu_lang_other));
        return view;
    }

    private View.OnClickListener langOCL(final int i){
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                if(bundle.getInt(Util.LIST_TYPE) == R.string.menu_chorus
                        || bundle.getInt(Util.LIST_TYPE) == R.string.menu_billboard
                        || bundle.getInt(Util.LIST_TYPE) == R.string.menu_new){
                    fragment = new FragmentListSong();
                    bundle.putString(Util.LIST_ARGUMENT, String.valueOf(i));
                }else{
                    fragment = new FragmentMenuWord();
                    bundle.putInt(Util.LIST_TYPE, i);
                }
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(Util.BACK_STACK_LANG).commit();
            }
        };
        return ocl;
    }
}
