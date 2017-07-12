package com.crazyktv.wcf.android;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

public class FragmentListSinger extends Fragment {

    private View view;
    private Bundle bundle;
    private ListView vListView;
    private LinearLayout vLoading;
    private TextView vStatus;
    private Fragment fragment, fragmentSelf;
    private String requestURL, title;
    private ArrayList<HashMap<String, String>> mylist;
    private SimpleAdapter adapter;
    RequestQueue mQueue;
    public static final String QUEUE_TAG = "QueueTag";
    private Boolean HAS_NEXT_PAGE = false, IS_LOADING = true;
    private int CURRENT_PAGE = 0;
    private String LIST_SORT;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentSelf = this;
        fragment = new FragmentListSong();
        bundle = getArguments();
        setHasOptionsMenu(true);
        LIST_SORT = "&sort=Singer_Name";

        if (bundle != null){

            switch (bundle.getInt(Util.LIST_TYPE)){
                case R.string.menu_singer_male:
                    requestURL = ActivityMain.PREF_URI +
                            "QuerySinger?rows=" + Util.LIST_SONG_ROW +
                            "&condition=Singer_Type=0";
                    break;
                case R.string.menu_singer_female:
                    requestURL = ActivityMain.PREF_URI +
                            "QuerySinger?rows=" + Util.LIST_SONG_ROW +
                            "&condition=Singer_Type=1";
                    break;
                case R.string.menu_singer_group:
                    requestURL = ActivityMain.PREF_URI +
                            "QuerySinger?rows=" + Util.LIST_SONG_ROW +
                            "&condition=Singer_Type=2";
                    break;
                case R.string.menu_singer_foreign_male:
                    requestURL = ActivityMain.PREF_URI +
                            "QuerySinger?rows=" + Util.LIST_SONG_ROW +
                            "&condition=Singer_Type=4";
                    break;
                case R.string.menu_singer_foreign_female:
                    requestURL = ActivityMain.PREF_URI +
                            "QuerySinger?rows=" + Util.LIST_SONG_ROW +
                            "&condition=Singer_Type=5";
                    break;
                case R.string.menu_singer_foreign_group:
                    requestURL = ActivityMain.PREF_URI  +
                            "QuerySinger?rows=" + Util.LIST_SONG_ROW +
                            "&condition=Singer_Type=6";
                    break;
                case R.string.menu_singer_other:
                    requestURL = ActivityMain.PREF_URI +
                            "QuerySinger?rows=" + Util.LIST_SONG_ROW +
                            "&sort=Singer_Name" +
                            "&condition=Singer_Type>6";
                    break;
                default:
                    requestURL = ActivityMain.PREF_URI;
            }
        }
        view = inflater.inflate(R.layout.fragment_list, container, false);
        vLoading = (LinearLayout) view.findViewById(R.id.vLoading);
        vStatus = (TextView) view.findViewById(R.id.vStatus);
        mylist = new ArrayList<HashMap<String, String>>();
        adapter = new SimpleAdapter(getActivity(), mylist, R.layout.list_singer, new String[] { "title", "singer" },new int[] { R.id.list_title, R.id.list_text_name});
        vListView = (ListView) view.findViewById(R.id.vListView);
        vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String singer = ((TextView) view.findViewById(R.id.list_text_name)).getText().toString();
                bundle.putString(Util.LIST_ARGUMENT, singer);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .hide(fragmentSelf)
                        .addToBackStack(Util.BACK_STACK_SINGER_LIST)
                        .commit();
            }
        });
        vListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (HAS_NEXT_PAGE && !IS_LOADING && firstVisibleItem > (totalItemCount - 20)) {
                    getSingerList();
                }
            }
        });
        vListView.setAdapter(adapter);
        getSingerList();
        if(bundle.getInt(Util.LIST_TYPE) == R.string.search_singer){
            ActivityMain.TITLE_MAIN = title;
        }else {
            ActivityMain.TITLE_MAIN = getText(bundle.getInt(Util.LIST_TYPE));
        }
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ActivityMain.TITLE_MAIN);
        return view;
    }

    private void getSingerList(){
        IS_LOADING = true;
        vLoading.setVisibility(View.VISIBLE);
        Log.d("Request URL", requestURL + "&page=" + CURRENT_PAGE + LIST_SORT);
        mQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(requestURL + "&page=" + CURRENT_PAGE + LIST_SORT,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        IS_LOADING = false;
                        Log.d("Response", response.toString());
                        vLoading.setVisibility(View.INVISIBLE);
                        if(response.length() == 0){
                            vStatus.setText(R.string.string_singer_empty);
                            vStatus.setVisibility(View.VISIBLE);
                        }else{
                            try{
                                for(int i=0;i<response.length();i++){
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    if(bundle.getInt(Util.LIST_TYPE) == R.string.search_singer){
                                        map.put("title", getResources().getString(Util.SingerResolve(jsonObject.getString("Singer_Type"))));
                                    }else {
                                        if(jsonObject.getString("Singer_Strokes").equals("0")){
                                            map.put("title", jsonObject.getString("Singer_Name").substring(0, 1));
                                        }else{
                                            map.put("title", jsonObject.getString("Singer_Strokes"));
                                        }
                                    }
                                    map.put("singer", jsonObject.getString("Singer_Name"));
                                    mylist.add(map);
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                            setHasNextPage();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                vStatus.setText(R.string.string_connection_error);
                vLoading.setVisibility(View.INVISIBLE);
                vStatus.setVisibility(View.VISIBLE);
                IS_LOADING = false;
            }
        });
        jsonArrayRequest.setTag(QUEUE_TAG);
        mQueue.add(jsonArrayRequest);
    }
    private void setHasNextPage(){
        int nextPage = CURRENT_PAGE + 1;
        Log.d("Request URL", requestURL + "&page=" + nextPage);
        JsonArrayRequest jar = new JsonArrayRequest(requestURL + "&page=" + nextPage,
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(bundle.getInt(Util.LIST_TYPE) == R.string.search_singer){
            ActivityMain.TITLE_MAIN = title;
        }else {
            ActivityMain.TITLE_MAIN = getText(bundle.getInt(Util.LIST_TYPE));
        }
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ActivityMain.TITLE_MAIN);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onStop () {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(QUEUE_TAG);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_list_singer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sort_singer_name_asc:
                Log.d("SORT","Singer name ASC");
                LIST_SORT = "&sort=Singer_Name";
                item.setChecked(true);
                resetList();
                return true;
            case R.id.action_sort_singer_name_desc:
                Log.d("SORT","Singer name DESC");
                LIST_SORT = "&sort=Singer_Name%20DESC";
                item.setChecked(true);
                resetList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void resetList(){
        vListView.setSelectionAfterHeaderView();
        CURRENT_PAGE = 0;
        mylist.clear();
        getSingerList();
    }
}
