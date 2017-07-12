package com.crazyktv.wcf.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class FragmentMenuSinger extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private View view;
    private Button btnSingerMale, btnSingerFemale, btnSingerGroup, btnSingerForeignMale, btnSingerForeignFemale, btnSingerForeignGroup, btnSingerOther;
    private Fragment fragment;
    private Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityMain.TITLE_MAIN = getText(R.string.menu_singer);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ActivityMain.TITLE_MAIN);
        view = inflater.inflate(R.layout.fragment_menu_singer, container, false);
        bundle = new Bundle();
        fragment = new FragmentListSinger();
        btnSingerMale = (Button) view.findViewById(R.id.btn_menu_singer_male);
        btnSingerMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt(Util.LIST_TYPE, R.string.menu_singer_male);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(Util.BACK_STACK_SINGER).commit();
            }
        });
        btnSingerFemale = (Button) view.findViewById(R.id.btn_menu_singer_female);
        btnSingerFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt(Util.LIST_TYPE, R.string.menu_singer_female);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(Util.BACK_STACK_SINGER).commit();
            }
        });
        btnSingerGroup = (Button) view.findViewById(R.id.btn_menu_singer_group);
        btnSingerGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt(Util.LIST_TYPE, R.string.menu_singer_group);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(Util.BACK_STACK_SINGER).commit();
            }
        });
        btnSingerForeignMale = (Button) view.findViewById(R.id.btn_menu_singer_foreign_male);
        btnSingerForeignMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt(Util.LIST_TYPE, R.string.menu_singer_foreign_male);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(Util.BACK_STACK_SINGER).commit();
            }
        });
        btnSingerForeignFemale = (Button) view.findViewById(R.id.btn_menu_singer_foreign_female);
        btnSingerForeignFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt(Util.LIST_TYPE, R.string.menu_singer_foreign_female);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(Util.BACK_STACK_SINGER).commit();
            }
        });
        btnSingerForeignGroup = (Button) view.findViewById(R.id.btn_menu_singer_foreign_group);
        btnSingerForeignGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt(Util.LIST_TYPE, R.string.menu_singer_foreign_group);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(Util.BACK_STACK_SINGER).commit();
            }
        });
        btnSingerOther = (Button) view.findViewById(R.id.btn_menu_singer_other);
        btnSingerOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt(Util.LIST_TYPE, R.string.menu_singer_other);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(Util.BACK_STACK_SINGER).commit();
            }
        });
        return view;
    }

}
