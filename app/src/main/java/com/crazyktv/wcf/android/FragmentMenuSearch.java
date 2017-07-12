package com.crazyktv.wcf.android;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.crazyktv.wcf.android.Util.LangResolve;

public class FragmentMenuSearch extends Fragment {
    private View view;
    private RadioGroup radioGroup;
    private EditText etArg;
    private ListView vListView;
    private LinearLayout vLoading;
    private TextView vStatus;

    private ArrayList<HashMap<String, String>> mylist;
    private SimpleAdapter adapter;
    private RequestQueue mQueue;
    public static final String QUEUE_TAG = "QueueTag";
    private Fragment fragment, fragmentSelf;
    private String requestURL;
    private boolean HAS_NEXT_PAGE = false, IS_LOADING;
    private int CURRENT_PAGE = 0, NEXT_PAGE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentSelf = this;
        fragment = new FragmentListSong();
        view = inflater.inflate(R.layout.fragment_menu_search, container, false);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioSearch);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //CURRENT_PAGE = 0;
                //mylist.clear();
                //if(etArg.getText().toString().length() == 0){
                   // vListView.setVisibility(View.INVISIBLE);
                //}
                getList();
            }
        });
        etArg = (EditText) view.findViewById(R.id.etArg);
        etArg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getList();
            }
        });
        mylist = new ArrayList<HashMap<String, String>>();
        vListView = (ListView) view.findViewById(R.id.vListView);
        vListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (HAS_NEXT_PAGE && !IS_LOADING && firstVisibleItem > (totalItemCount - 20)) {
                    sendRequest(radioGroup.getCheckedRadioButtonId(), CURRENT_PAGE);
                }
            }
        });
        vLoading = (LinearLayout) view.findViewById(R.id.vLoading);
        vStatus = (TextView) view.findViewById(R.id.vStatus);
        mQueue = Volley.newRequestQueue(getActivity());
        // only will trigger it if no physical keyboard is open
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        return view;
    }

    private void getList(){
        resetListView();
        Log.d("ARG",etArg.getText().toString());
        if(!(etArg.getText().toString().length() == 0)){
            vStatus.setVisibility(View.INVISIBLE);
            vLoading.setVisibility(View.VISIBLE);
            String arg = etArg.getText().toString();
            try{
                arg = Uri.encode(arg);
            }catch (Exception e){
            }
            if(radioGroup.getCheckedRadioButtonId() == R.id.radioSinger){
                requestURL = ActivityMain.PREF_URI +"QuerySinger?rows=" + Util.LIST_SONG_ROW +
                        "&sort=Singer_Name" +
                        "&condition=Singer_Name%20LIKE%20%27%25" + arg + "%25%27" +
                        "&page=";
                getSingerList();
            }else{
                requestURL = ActivityMain.PREF_URI +"QuerySong?rows=" + Util.LIST_SONG_ROW +
                        "&sort=Song_WordCount,Song_SongName" +
                        "&condition=Song_SongName%20LIKE%20'%25"+ arg +"%25'" +
                        "&page=";
                getSongList();
            }
        }
    }

    private void sendRequest(final int requestType, int page){
        IS_LOADING = true;
        final JsonArrayRequest jsonArrayRequest;
        Log.d("Request URL", requestURL + page);
        jsonArrayRequest = new JsonArrayRequest(requestURL + page,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        IS_LOADING = false;
                        Log.d("Response", response.toString());
                        vLoading.setVisibility(View.INVISIBLE);
                        if (response.length() == 0) {
                            if (requestType == R.id.radioSinger) {
                                vStatus.setText(R.string.string_singer_empty);
                            } else {
                                vStatus.setText(R.string.string_song_empty);
                            }
                            resetListView();
                            vStatus.setVisibility(View.VISIBLE);
                        } else {
                            vListView.setVisibility(View.VISIBLE);
                            setHasNextPage();
                            if (requestType == R.id.radioSinger) {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        JSONObject jsonObject = response.getJSONObject(i);
                                        map.put("title", getResources().getString(Util.SingerResolve(jsonObject.getString("Singer_Type"))));
                                        map.put("singer", jsonObject.getString("Singer_Name"));
                                        mylist.add(map);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        JSONObject jsonObject = response.getJSONObject(i);
                                        map.put("title", getResources().getText(LangResolve(jsonObject.getString("Song_Lang"))).toString());
                                        map.put("songId", jsonObject.getString("Song_Id"));
                                        map.put("songSinger", jsonObject.getString("Song_Singer"));
                                        map.put("songName", jsonObject.getString("Song_SongName"));
                                        mylist.add(map);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                IS_LOADING = false;
                vLoading.setVisibility(View.INVISIBLE);
                vListView.setVisibility(View.INVISIBLE);
                vStatus.setText(R.string.string_connection_error);
                vStatus.setVisibility(View.VISIBLE);
            }
        });
        jsonArrayRequest.setTag(QUEUE_TAG);
        mQueue.add(jsonArrayRequest);
    }
    private void getSingerList(){
        mylist.clear();
        vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String singer = ((TextView) view.findViewById(R.id.list_text_name)).getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString(Util.LIST_ARGUMENT, singer);
                bundle.putInt(Util.LIST_TYPE, R.string.search_singer);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .hide(fragmentSelf)
                        .addToBackStack(Util.BACK_STACK_SINGER_LIST)
                        .commit();
            }
        });

        adapter = new SimpleAdapter(getActivity(),
                mylist, R.layout.list_singer,
                new String[] { "title", "singer" },
                new int[] { R.id.list_title, R.id.list_text_name});
        vListView.setAdapter(adapter);
        sendRequest(R.id.radioSinger,0);
    }
    private void getSongList(){
        mylist.clear();
        vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songId = ((TextView) view.findViewById(R.id.list_text_id)).getText().toString();
                String songSinger = ((TextView) view.findViewById(R.id.list_text_singer)).getText().toString();
                String songName = ((TextView) view.findViewById(R.id.list_text_name)).getText().toString();
                Bundle requestBundle = new Bundle();
                requestBundle.putString("songId", songId);
                requestBundle.putString("songSinger", songSinger);
                requestBundle.putString("songName", songName);
                FragmentRequestDialog requestDialog = new FragmentRequestDialog();
                requestDialog.setArguments(requestBundle);
                requestDialog.show(getFragmentManager(), "EditNameDialog");
            }
        });
        adapter = new SimpleAdapter(getActivity(),
                mylist, R.layout.list_song,
                new String[] { "title", "songId", "songSinger", "songName" },
                new int[] { R.id.list_title, R.id.list_text_id, R.id.list_text_singer, R.id.list_text_name});
        vListView.setAdapter(adapter);
        sendRequest(R.id.radioSong, 0);
    }
    private void resetListView(){
        CURRENT_PAGE = 0;
        vStatus.setVisibility(View.INVISIBLE);
        vListView.setVisibility(View.INVISIBLE);
        HAS_NEXT_PAGE = false;
        mylist.clear();
        if(adapter != null) adapter.notifyDataSetChanged();
    }

    private void setHasNextPage(){
        NEXT_PAGE = CURRENT_PAGE + 1;
        Log.d("Request Next Page", requestURL + NEXT_PAGE);
        JsonArrayRequest jar = new JsonArrayRequest(requestURL + NEXT_PAGE,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() > 0){
                            Log.d("HAS_NEXT_PAGE","true");
                            HAS_NEXT_PAGE = true;
                            CURRENT_PAGE++;
                        }else{
                            Log.d("HAS_NEXT_PAGE","false");
                            HAS_NEXT_PAGE = false;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(jar);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_search);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        onResume();
    }
    @Override
    public void onStop () {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(QUEUE_TAG);
        }
    }
}
