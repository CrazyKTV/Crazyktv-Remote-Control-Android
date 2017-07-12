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
import android.view.SubMenu;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import static com.crazyktv.wcf.android.Util.LangResolve;

public class FragmentListSong extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private View view;
    private Bundle bundle;
    private ListView vListView;
    private LinearLayout vLoading;
    private TextView vStatus;
    private String requestURL, listArgument;
    private int listType;
    private ArrayList<HashMap<String, String>> mylist;
    private SimpleAdapter adapter;
    RequestQueue mQueue;
    public static final String QUEUE_TAG = "QueueTag";
    private Boolean HAS_NEXT_PAGE = false, IS_LOADING = true;
    private int CURRENT_PAGE = 0;
    private String LIST_SORT;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mQueue = Volley.newRequestQueue(getActivity());
        bundle = getArguments();
        listType = bundle.getInt(Util.LIST_TYPE);
        listArgument = bundle.getString(Util.LIST_ARGUMENT);
        setHasOptionsMenu(true);
        LIST_SORT = "&sort=Song_WordCount,Song_SongName";
        Log.d("submenu","onCreate");
        switch (bundle.getInt(Util.LIST_TYPE)){
            case R.string.search_song:
                ActivityMain.TITLE_MAIN = getResources().getString(R.string.search_song) + "：" + listArgument;
                try{
                    listArgument = Uri.encode(listArgument);
                }catch (Exception e){
                }
                requestURL = "QuerySong?rows=" + Util.LIST_SONG_ROW +
                        "&condition=Song_SongName%20LIKE%20'%25"+ listArgument +"%25'" +
                        "&page=";
                break;
            case R.string.menu_singer_male:
            case R.string.menu_singer_female:
            case R.string.menu_singer_group:
            case R.string.menu_singer_foreign_female:
            case R.string.menu_singer_foreign_male:
            case R.string.menu_singer_foreign_group:
            case R.string.menu_singer_other:
            case R.string.search_singer:
                ActivityMain.TITLE_MAIN = listArgument;
                try{
                    listArgument = Uri.encode(listArgument);
                }catch (Exception e){
                }
                requestURL = "QuerySong?rows=" + Util.LIST_SONG_ROW +
                        "&condition=Song_Singer='" + listArgument + "'"+
                        "%20or%20Song_Singer%20like%20'%25%26" + listArgument + "'"+
                        "%20or%20Song_Singer%20like%20'" + listArgument + "%26%25'"+
                        "%20or%20Song_Singer%20like%20'%25%26" + listArgument + "%26%25'";
                break;
            case R.string.menu_lang_chinese:
            case R.string.menu_lang_taiwanese:
            case R.string.menu_lang_cantonese:
            case R.string.menu_lang_japanese:
            case R.string.menu_lang_english:
            case R.string.menu_lang_hakka:
            case R.string.menu_lang_children:
            case R.string.menu_lang_aborigine:
            case R.string.menu_lang_korea:
            case R.string.menu_lang_other:
                LIST_SORT = "&sort=Song_SongName,Song_WordCount";
                if(Integer.parseInt(listArgument)>9){
                    requestURL =
                            "QuerySong?rows=" + Util.LIST_SONG_ROW +
                            "&condition=Song_WordCount%3E9" +
                            "&lang="+Util.getLang(listType);
                    ActivityMain.TITLE_MAIN = getResources().getString(listType) + " " + getResources().getString(R.string.menu_word_10);
                }else {
                    requestURL =
                            "QuerySong?rows=" + Util.LIST_SONG_ROW +
                            "&condition=Song_WordCount="+listArgument +
                            "&lang="+Util.getLang(listType);
                    ActivityMain.TITLE_MAIN = getResources().getString(listType) + " " + listArgument;
                }

                break;
            case R.string.menu_chorus:
                requestURL =
                        "QuerySong?rows=" + Util.LIST_SONG_ROW +
                        "&condition=Song_SingerType=3" +
                        "&lang=" + Util.getLang(Integer.parseInt(listArgument));
                ActivityMain.TITLE_MAIN = getResources().getString(listType) + " " + getResources().getString(Integer.parseInt(listArgument));
                break;
            case R.string.menu_billboard:
                //actionMenu.findItem(R.id.list_song_sort).setVisible(false);
                LIST_SORT = "&sort=Song_PlayCount%20DESC";
                requestURL = "QuerySong?rows="+Util.LIST_SONG_ROW +
                        "&condition=Song_PlayCount%3E" + ActivityMain.PREF_BILLBOARD_LEAST_PLAY_COUNT +
                        "&lang="+Util.getLang(Integer.parseInt(listArgument));
                ActivityMain.TITLE_MAIN = getResources().getString(listType) + " " + getResources().getString(Integer.parseInt(listArgument));
                break;
            case R.string.menu_new:
                //actionMenu.findItem(R.id.list_song_sort).setVisible(false);
                LIST_SORT = "&sort=Song_CreatDate%20DESC,Song_WordCount,Song_SongName";
                ActivityMain.TITLE_MAIN = getResources().getString(listType) + " " + getResources().getString(Integer.parseInt(listArgument));
                if(ActivityMain.PREF_NEW_IN_SEARCH_GUARANTEE){
                    requestURL =  "QuerySong?rows=" + Util.LIST_SONG_ROW +
                            "&lang="+Util.getLang(Integer.parseInt(listArgument));
                }else {
                    Calendar calendar = Calendar.getInstance();
                    //set new song date
                    calendar.add(Calendar.DAY_OF_MONTH, (int) (ActivityMain.PREF_NEW_IN_DAYS * -1));
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    String date = df.format(calendar.getTime());
                    date = date.replace("/", "%2F");
                    requestURL = "QuerySong?rows=" + Util.LIST_SONG_ROW +
                            "&condition=Song_CreatDate%20%3E=%20%27" + date + "%27" +
                            "&lang=" + Util.getLang(Integer.parseInt(listArgument));
                }
                break;
            case R.string.menu_favorite:
                listArgument = listArgument.replace("#", "%23");
                loginFavorite(ActivityMain.PREF_URI + "FavoriteLogin?user="+listArgument);
                requestURL = "FavoriteSong?rows=" + Util.LIST_SONG_ROW + "&user=" + listArgument;
                break;
        }
        requestURL = ActivityMain.PREF_URI + requestURL;
        view = inflater.inflate(R.layout.fragment_list, container, false);
        vLoading = (LinearLayout) view.findViewById(R.id.vLoading);
        vStatus = (TextView) view.findViewById(R.id.vStatus);
        mylist = new ArrayList<HashMap<String, String>>();
        adapter = new SimpleAdapter(getActivity(),
                mylist, R.layout.list_song,
                new String[] { "title", "songId", "songSinger", "songName" },
                new int[] { R.id.list_title, R.id.list_text_id, R.id.list_text_singer, R.id.list_text_name});
        vListView = (ListView) view.findViewById(R.id.vListView);
        vListView.setAdapter(adapter);
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
        vListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(HAS_NEXT_PAGE && !IS_LOADING && firstVisibleItem > (totalItemCount - 20)){
                    getSonglist();
                }
            }
        });
        getSonglist();
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ActivityMain.TITLE_MAIN);
        return view;
    }
    private void loginFavorite(String s){
        StringRequest stringRequest = new StringRequest(s,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                vStatus.setText(R.string.string_connection_error);
                vLoading.setVisibility(View.INVISIBLE);
                vStatus.setVisibility(View.VISIBLE);
            }
        });
        stringRequest.setTag(QUEUE_TAG);
        mQueue.add(stringRequest);
    }

    private void getSonglist(){
        IS_LOADING = true;
        vLoading.setVisibility(View.VISIBLE);
        vStatus.setVisibility(View.INVISIBLE);
        Log.d("Request URL", requestURL + "&page=" +  CURRENT_PAGE + LIST_SORT);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(requestURL + "&page=" + CURRENT_PAGE + LIST_SORT,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        IS_LOADING = false;
                        Log.d("RESPONSE", response.toString());
                        vLoading.setVisibility(View.INVISIBLE);
                        if  (response.length() == 0){
                            vStatus.setText(R.string.string_song_empty);
                            vStatus.setVisibility(View.VISIBLE);
                        }else {
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
                        }
                        adapter.notifyDataSetChanged();
                        setHasNextPage();
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
    public void onStop () {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(QUEUE_TAG);
        }
    }

    Menu actionMenu;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_list_song, menu);
        Log.d("submenu", "onCreateOptionsMenu");
        actionMenu = menu;
        switch (bundle.getInt(Util.LIST_TYPE)){
            case R.string.menu_lang_chinese:
            case R.string.menu_lang_taiwanese:
            case R.string.menu_lang_cantonese:
            case R.string.menu_lang_japanese:
            case R.string.menu_lang_english:
            case R.string.menu_lang_hakka:
            case R.string.menu_lang_children:
            case R.string.menu_lang_aborigine:
            case R.string.menu_lang_korea:
            case R.string.menu_lang_other:
                actionMenu.findItem(R.id.list_song_sort).getSubMenu().findItem(R.id.action_sort_word_asc).setVisible(false);
                actionMenu.findItem(R.id.list_song_sort).getSubMenu().findItem(R.id.action_sort_word_desc).setVisible(false);
                actionMenu.findItem(R.id.list_song_sort).getSubMenu().findItem(R.id.action_sort_song_name_asc).setChecked(true);
                break;
            case R.string.menu_billboard:
            case R.string.menu_new:
                actionMenu.findItem(R.id.list_song_sort).setVisible(false);
                break;
            default:
                break;
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sort_word_asc:
                Log.d("SORT","WORD ASC");
                LIST_SORT = "&sort=Song_WordCount,Song_SongName";
                item.setChecked(true);
                resetList();
                return true;
            case R.id.action_sort_word_desc:
                Log.d("SORT","WORD DESC");
                LIST_SORT = "&sort=Song_WordCount%20DESC,Song_SongName";
                item.setChecked(true);
                resetList();
                return true;
            case R.id.action_sort_song_name_asc:
                Log.d("SORT","Song name ASC");
                LIST_SORT = "&sort=Song_SongName,Song_WordCount";
                item.setChecked(true);
                resetList();
                return true;
            case R.id.action_sort_song_name_desc:
                Log.d("SORT","Song name DESC");
                LIST_SORT = "&sort=Song_SongName%20DESC,Song_WordCount";
                item.setChecked(true);
                resetList();
                return true;
            case R.id.action_sort_newest:
                Log.d("SORT","Newest");
                LIST_SORT = "&sort=Song_CreatDate%20DESC,Song_WordCount,Song_SongName";
                item.setChecked(true);
                resetList();
                return true;
            case R.id.action_sort_oldest:
                Log.d("SORT","Oldest");
                LIST_SORT = "&sort=Song_CreatDate,Song_WordCount,Song_SongName";
                item.setChecked(true);
                resetList();
                return true;
            case R.id.action_sort_playcount_desc:
                Log.d("SORT","Play count DESC");
                LIST_SORT = "&sort=Song_PlayCount%20DESC,Song_WordCount,Song_SongName";
                item.setChecked(true);
                resetList();
                return true;
            case R.id.action_sort_playcount_asc:
                Log.d("SORT","Play count ASC");
                LIST_SORT = "&sort=Song_PlayCount,Song_WordCount,Song_SongName";
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
        getSonglist();
    }

}
