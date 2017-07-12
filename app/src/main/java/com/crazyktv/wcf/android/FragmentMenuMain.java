package com.crazyktv.wcf.android;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMenuMain extends Fragment {


    public FragmentMenuMain() {
        // Required empty public constructor
    }
    private FragmentManager fragmentManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if((fragmentManager.getBackStackEntryCount() == 0)){
                    ActivityMain.TITLE_MAIN = getText(R.string.title_main);
                    ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ActivityMain.TITLE_MAIN);
                }
            }
        });
    }

    private Bundle bundle;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_menu_main, container, false);
        bundle = new Bundle();
        findViews();
        return view;
    }

    private void findViews(){
        view.findViewById(R.id.btn_menu_singer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.container, new FragmentMenuSinger()).addToBackStack(Util.BACK_STACK_MAIN).commit();
            }
        });
        view.findViewById(R.id.btn_menu_lang).setOnClickListener(langOCL(R.string.menu_lang));
        view.findViewById(R.id.btn_menu_chorus).setOnClickListener(langOCL(R.string.menu_chorus));
        view.findViewById(R.id.btn_menu_billboard).setOnClickListener(langOCL(R.string.menu_billboard));
        view.findViewById(R.id.btn_menu_new).setOnClickListener(langOCL(R.string.menu_new));

        view.findViewById(R.id.btn_menu_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.container, new FragmentListFavorite()).addToBackStack(Util.BACK_STACK_MAIN).commit();
            }
        });
        view.findViewById(R.id.btn_menu_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.container, new FragmentMenuSearch()).addToBackStack(Util.BACK_STACK_MAIN).commit();
            }
        });
    }

    private View.OnClickListener langOCL(final int i){
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt(Util.LIST_TYPE, i);
                Fragment fragment = new FragmentMenuLang();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(Util.BACK_STACK_MAIN).commit();
            }
        };
        return ocl;
    }
}
