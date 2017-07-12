package com.crazyktv.wcf.android;


import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentControl extends Fragment {
    private View view;
    private RequestQueue mQueue;
    private Vibrator myVibrator;

    private Button btnCut;
    private ImageButton btnReplay;

    public FragmentControl() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_control, container, false);
        myVibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        mQueue = Volley.newRequestQueue(getActivity());
        ((ImageButton) view.findViewById(R.id.btn_tone_male)).setOnClickListener(controlOCL());
        ((ImageButton) view.findViewById(R.id.btn_tone_female)).setOnClickListener(controlOCL());
        btnReplay = (ImageButton) view.findViewById(R.id.btn_replay);
        ((ImageButton) view.findViewById(R.id.btn_tone_rise)).setOnClickListener(controlOCL());
        ((ImageButton) view.findViewById(R.id.btn_tone_natural)).setOnClickListener(controlOCL());
        ((ImageButton) view.findViewById(R.id.btn_tone_fall)).setOnClickListener(controlOCL());
        ((ImageButton) view.findViewById(R.id.btn_play_ff)).setOnClickListener(controlOCL());
        ((ImageButton) view.findViewById(R.id.btn_play_pause)).setOnClickListener(controlOCL());
        ((ImageButton) view.findViewById(R.id.btn_play_fr)).setOnClickListener(controlOCL());
        ((ImageButton) view.findViewById(R.id.btn_vol_up)).setOnClickListener(controlOCL());
        ((ImageButton) view.findViewById(R.id.btn_vol_down)).setOnClickListener(controlOCL());
        ((ImageButton) view.findViewById(R.id.btn_mute)).setOnClickListener(controlOCL());
        if(ActivityMain.IS_TABLET || !ActivityMain.PREF_SHOW_CONTROL_ON_MAIN){
            btnCut = (Button) view.findViewById(R.id.btn_cut);
            ((Button) view.findViewById(R.id.btn_vocal)).setOnClickListener(controlOCL());
            ((Button) view.findViewById(R.id.btn_fixed)).setOnClickListener(controlOCL());
        }else if(ActivityMain.PREF_SHOW_CONTROL_ON_MAIN){
            view.findViewById(R.id.linear_control).setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onResume() {
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
                        ActivityMain.myVibrator.vibrate(150);
                        return false;
                    }
                });
            }else {
                btnCut.setOnLongClickListener(null);
                btnCut.setOnClickListener(controlOCL());
            }
        }
        if(ActivityMain.PREF_LONG_PRESS_REPLAY_SONG){
            btnReplay.setOnClickListener(null);
            btnReplay.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String arg = "DoCrazyKTV_Action?state=RsetPlay";
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
                    return false;
                }
            });
        }else {
            btnReplay.setOnLongClickListener(null);
            btnReplay.setOnClickListener(controlOCL());
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
                    case R.id.btn_mute:
                        arg = "DoCrazyKTV_Control?state=Mute";
                        break;
                    case R.id.btn_vol_up:
                        arg = "DoCrazyKTV_Control?state=Volume&value=1";
                        break;
                    case R.id.btn_vol_down:
                        arg = "DoCrazyKTV_Control?state=Volume&value=-1";
                        break;
                    case R.id.btn_play_ff:
                        arg = "DoCrazyKTV_Action?state=Forward";
                        break;
                    case R.id.btn_play_fr:
                        arg = "DoCrazyKTV_Action?state=Back";
                        break;
                    case R.id.btn_play_pause:
                        arg = "DoCrazyKTV_Action?state=PlayPause";
                        break;
                    case R.id.btn_tone_rise:
                        arg = "DoCrazyKTV_Control?state=Pitch&value=1";
                        break;
                    case R.id.btn_tone_fall:
                        arg = "DoCrazyKTV_Control?state=Pitch&value=-1";
                        break;
                    case R.id.btn_tone_natural:
                        arg = "DoCrazyKTV_Control?state=DefaultPitch";
                        break;
                    case R.id.btn_tone_male:
                        arg = "DoCrazyKTV_Control?state=MaleVoice";
                        break;
                    case R.id.btn_tone_female:
                        arg = "DoCrazyKTV_Control?state=WomanVoice";
                        break;
                    case R.id.btn_replay:
                        arg = "DoCrazyKTV_Action?state=RsetPlay";
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
