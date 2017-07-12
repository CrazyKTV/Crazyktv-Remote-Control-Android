package com.crazyktv.wcf.android;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
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

import static com.crazyktv.wcf.android.Util.LangResolve;
/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentQueue extends Fragment {

    private ListView vListView;
    private View view;
    private RequestQueue mQueue;
    private SimpleAdapter queueAdapter;
    private ArrayList<HashMap<String, String>> mylist;
    private TextView vStatus;
    private SwipeRefreshLayout vSwipeRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_queue, container, false);
        mQueue = Volley.newRequestQueue(getActivity());
        findViews();
        return view;
    }
    private void findViews(){
        vStatus = (TextView) view.findViewById(R.id.vStatus);
        vSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.vSwipeRefresh);
        vSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vSwipeRefresh.setRefreshing(true);
                getQueue(0);
            }
        });
        vSwipeRefresh.setColorSchemeResources(
                R.color.accent_color,
                R.color.green_200);
        vSwipeRefresh.setRefreshing(true);

        mylist = new ArrayList<HashMap<String, String>>();
        queueAdapter = new SimpleAdapter(getActivity(),
                mylist, R.layout.list_song,
                new String[] { "title", "songId", "songSinger", "songName" },
                new int[] { R.id.list_title, R.id.list_text_id, R.id.list_text_singer, R.id.list_text_name});
        vListView = (ListView) view.findViewById(R.id.vListView);
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
                requestBundle.putBoolean("isQueue", true);
                FragmentRequestDialog requestDialog = new FragmentRequestDialog();
                requestDialog.setArguments(requestBundle);
                requestDialog.show(getFragmentManager(), "EditNameDialog");
            }
        });
        vListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (vListView == null || vListView.getChildCount() == 0) ?
                                0 : vListView.getChildAt(0).getTop();
                vSwipeRefresh.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
        vListView.setEmptyView(vStatus);

    }
    private void getQueue(int i){
        Log.d("Request", requestArg(i));
        cd.cancel();
        mylist.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(requestArg(i),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("RESPONSE", response.toString());
                        vListView.setVisibility(View.VISIBLE);
                        vSwipeRefresh.setRefreshing(false);
                        if(response.length() == 0){
                            vStatus.setText(R.string.string_song_empty);
                        }else {
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
                            vListView.setAdapter(queueAdapter);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                vSwipeRefresh.setRefreshing(false);
                vListView.setVisibility(View.INVISIBLE);
                vStatus.setText(R.string.string_connection_error);
                vStatus.setVisibility(View.VISIBLE);
            }
        });
        mQueue.add(jsonArrayRequest);
        if(cd != null) cd.start();
        Log.d("cd", "start");
    }
    private String requestArg(int i){
        return ActivityMain.PREF_URI+"ViewSong?rows=30&page="+String.valueOf(i);
    }

    private SharedPreferences sp;
    private CountDownTimer cd;
    private int eachTime;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        //set Auto Refresh timer
        eachTime = Integer.parseInt(sp.getString(getResources().getString(R.string.key_queue_refresh_frequency), "15"))*1000;
        cd = new CountDownTimer(eachTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if(ActivityMain.PREF_AUTO_REFRESH_QUEUE){
                    getQueue(0);
                }
            }
        };
        getQueue(0);
    }
    @Override
    public void onPause() {
        super.onPause();
        if(cd != null) cd.cancel();
        Log.d("cd", "pause");
    }


}
