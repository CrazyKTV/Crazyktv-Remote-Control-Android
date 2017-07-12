package com.crazyktv.wcf.android;


import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.crazyktv.wcf.android.Util.LangResolve;
/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNumber extends Fragment {


    public FragmentNumber() {
        // Required empty public constructor
    }
    private View view;
    private RequestQueue mQueue;
    private String previewId, previewSinger, previewSong;
    private Boolean isPreview = false;
    private Vibrator myVibrator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_number, container, false);
        mQueue = Volley.newRequestQueue(getActivity());
        myVibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        findViews();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        numpadNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ActivityMain.PREF_NUMBER_LENGTH)});
    }

    private Button numpadPriority, numpadRequest;
    private ImageButton numpadBack;
    private ListView vListView;
    private LinearLayout vLoading;
    private TextView numpadNo, vStatus;

    private Toast toast;
    private void findViews(){
        vListView = (ListView) view.findViewById(R.id.vListView);
        vLoading = (LinearLayout) view.findViewById(R.id.vLoading);
        vStatus = (TextView) view.findViewById(R.id.vStatus);
        toast = Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT);
        numpadNo = (TextView) view.findViewById(R.id.numpad_no);
        numpadNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (numpadNo.length() > 0) {
                    vListView.setVisibility(View.INVISIBLE);
                    vStatus.setVisibility(View.INVISIBLE);
                    vLoading.setVisibility(View.VISIBLE);
                    getPreviewList(numpadNo.getText().toString());

                }else {
                    resetListView();
                }

            }
        });
        numpadBack = (ImageButton) view.findViewById(R.id.numpad_back);
        numpadBack.setEnabled(false);
        numpadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numpadNo.getText().length() > 0) {
                    numpadNo.setText(numpadNo.getText().subSequence(0, numpadNo.length() - 1));
                    if (numpadNo.getText().length() == 0) {
                        v.setEnabled(false);
                        numpadPriority.setEnabled(false);
                        numpadRequest.setEnabled(false);
                    }
                }
            }
        });
        ((Button) view.findViewById(R.id.numpad_1)).setOnClickListener(numpadOCL());
        ((Button) view.findViewById(R.id.numpad_2)).setOnClickListener(numpadOCL());
        ((Button) view.findViewById(R.id.numpad_3)).setOnClickListener(numpadOCL());
        ((Button) view.findViewById(R.id.numpad_4)).setOnClickListener(numpadOCL());
        ((Button) view.findViewById(R.id.numpad_5)).setOnClickListener(numpadOCL());
        ((Button) view.findViewById(R.id.numpad_6)).setOnClickListener(numpadOCL());
        ((Button) view.findViewById(R.id.numpad_7)).setOnClickListener(numpadOCL());
        ((Button) view.findViewById(R.id.numpad_8)).setOnClickListener(numpadOCL());
        ((Button) view.findViewById(R.id.numpad_9)).setOnClickListener(numpadOCL());
        ((Button) view.findViewById(R.id.numpad_0)).setOnClickListener(numpadOCL());
        numpadRequest = (Button) view.findViewById(R.id.numpad_request);
        numpadRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String arg = ActivityMain.PREF_URI + "OrderSong?value=" + String.valueOf(Integer.parseInt(numpadNo.getText().toString()));
                StringRequest stringRequest = new StringRequest(arg,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("RESPONSE", response.toString());
                                String toastResponse = "";
                                if (response.toString().equals("[]")) {
                                    toastResponse = getText(R.string.string_request_fail).toString();
                                } else {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        toastResponse = jsonObject.getString("Song_Singer");
                                        toastResponse += " - ";
                                        toastResponse += jsonObject.getString("Song_SongName") + " ";
                                    } catch (JSONException e) {

                                    }
                                    toastResponse += getText(R.string.string_request_success);
                                    numpadNo.setText("");
                                    numpadBack.setEnabled(false);
                                    numpadPriority.setEnabled(false);
                                    numpadRequest.setEnabled(false);
                                }
                                toast.setText(toastResponse);
                                toast.show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("RESPONSE", error.getMessage(), error);
                                toast.setText(getText(R.string.string_request_fail));
                                toast.show();
                            }
                        });
                mQueue.add(stringRequest);
                myVibrator.vibrate(150);
            }
        });
        numpadPriority = (Button) view.findViewById(R.id.numpad_priority);
        numpadPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(numpadNo.length(); numpadNo.length() < ActivityMain.PREF_NUMBER_LENGTH; numpadNo.length())
                    numpadNo.setText("0" + numpadNo.getText().toString());
                String arg = ActivityMain.PREF_URI + "DoCrazyKTV_Action?state=Insert&value=" + numpadNo.getText().toString();
                StringRequest stringRequest = new StringRequest(arg,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                                Log.d("RESPONSE", response.toString());
                                Toast.makeText(getActivity(), getText(R.string.string_request_success), Toast.LENGTH_SHORT).show();
                                numpadNo.setText("");
                                numpadBack.setEnabled(false);
                                numpadPriority.setEnabled(false);
                                numpadRequest.setEnabled(false);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("TAG", error.getMessage(), error);
                                Toast.makeText(getActivity(), getText(R.string.string_request_fail), Toast.LENGTH_SHORT).show();
                            }
                        });
                mQueue.add(stringRequest);
                myVibrator.vibrate(150);
            }
        });



    }
    private View.OnClickListener numpadOCL(){
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button numpad = (Button) v;
                if(numpadNo.getText().length() == 0){
                    numpadBack.setEnabled(true);
                    numpadPriority.setEnabled(true);
                    numpadRequest.setEnabled(true);
                }
                numpadNo.append(numpad.getText().toString());
            }
        };
        return ocl;
    }

    private ArrayList<HashMap<String, String>> mylist;
    private ListAdapter adapter;

    private void getPreviewList(String no){
        mylist = new ArrayList<HashMap<String, String>>();
        adapter = new SimpleAdapter(getActivity(),
                mylist, R.layout.list_song_with_id,
                new String[] { "title", "songId", "songSinger", "songName" },
                new int[] { R.id.list_title, R.id.list_text_id, R.id.list_text_singer, R.id.list_text_name});
        String arg = ActivityMain.PREF_URI + "QuerySong?rows=11&condition=Song_Id=" + no + "%20or%20Song_Id%20like%20%27" + no + "*%27";
        Log.d("Request URL", arg);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(arg,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        vLoading.setVisibility(View.INVISIBLE);
                        if (response.length() == 0) {
                            vStatus.setText(R.string.string_song_empty);
                            vStatus.setVisibility(View.VISIBLE);
                            resetListView();
                        }else {
                            vStatus.setVisibility(View.INVISIBLE);
                            try{
                                for(int i=0;i<response.length();i++){
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    map.put("title",getResources().getText(LangResolve(jsonObject.getString("Song_Lang"))).toString());
                                    map.put("songId",jsonObject.getString("Song_Id"));
                                    map.put("songSinger", jsonObject.getString("Song_Singer"));
                                    map.put("songName", jsonObject.getString("Song_SongName"));
                                    mylist.add(map);
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            vListView.setAdapter(adapter);
                            vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String songId = ((TextView) view.findViewById(R.id.list_text_id)).getText().toString();
                                    String songSinger = ((TextView) view.findViewById(R.id.list_text_singer)).getText().toString();
                                    String songName = ((TextView) view.findViewById(R.id.list_text_name)).getText().toString();
                                    Bundle requestBundle = new Bundle();
                                    requestBundle.putString("songId",songId);
                                    requestBundle.putString("songSinger",songSinger);
                                    requestBundle.putString("songName", songName);
                                    FragmentRequestDialog requestDialog = new FragmentRequestDialog();
                                    requestDialog.setArguments(requestBundle);
                                    requestDialog.show(getFragmentManager(), "EditNameDialog");
                                }
                            });
                            vListView.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                vLoading.setVisibility(View.INVISIBLE);
                vStatus.setText(R.string.string_connection_error);
                vStatus.setVisibility(View.VISIBLE);
            }
        });
        mQueue.add(jsonArrayRequest);
    }
    private void resetListView(){
        mylist = new ArrayList<HashMap<String, String>>();
        adapter = new SimpleAdapter(getActivity(),
                mylist, R.layout.list_song_with_id,
                new String[] { "title", "songId", "songSinger", "songName" },
                new int[] { R.id.list_title, R.id.list_text_id, R.id.list_text_singer, R.id.list_text_name});
        vListView.setAdapter(adapter);
    }

}
