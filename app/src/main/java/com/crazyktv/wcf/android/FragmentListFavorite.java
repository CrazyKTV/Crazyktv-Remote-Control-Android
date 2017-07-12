package com.crazyktv.wcf.android;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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

public class FragmentListFavorite extends Fragment {

    private View view;
    private ListView vListView;
    private LinearLayout vLoading;
    private TextView vStatus;
    private Fragment fragment, fragmentSelf;
    private String url;
    private ArrayList<HashMap<String, String>> mylist;
    private ListAdapter adapter;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();

        fragmentSelf = this;
        fragment = new FragmentListSong();
        url = ActivityMain.PREF_URI +"FavoriteUser?rows=10000";
        view = inflater.inflate(R.layout.fragment_list, container, false);
        vListView = (ListView) view.findViewById(R.id.vListView);
        vLoading = (LinearLayout) view.findViewById(R.id.vLoading);
        vStatus = (TextView) view.findViewById(R.id.vStatus);
        mylist = new ArrayList<HashMap<String, String>>();
        adapter = new SimpleAdapter(getActivity(), mylist, R.layout.list_song, new String[] { "title", "User_Id", "User_Name" },new int[] { R.id.list_title, R.id.list_text_singer, R.id.list_text_name});
        getSingerList();
        return view;
    }

    private void getSingerList(){
        Log.d("Request URL", url);
        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response", response.toString());
                        vLoading.setVisibility(View.INVISIBLE);
                        if(response.length() == 0){
                            vStatus.setVisibility(View.VISIBLE);
                        }else{
                            try{
                                for(int i=0;i<response.length();i++){
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    map.put("User_Id", jsonObject.getString("User_Id"));
                                    map.put("User_Name", jsonObject.getString("User_Name"));
                                    map.put("title", jsonObject.getString("User_Name").substring(0,1));
                                    mylist.add(map);
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            vListView.setAdapter(adapter);
                            vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String fid = ((TextView) view.findViewById(R.id.list_text_singer)).getText().toString();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(Util.LIST_TYPE, R.string.menu_favorite);
                                    bundle.putString(Util.LIST_ARGUMENT, fid);
                                    fragment.setArguments(bundle);
                                    getFragmentManager().beginTransaction()
                                            .add(R.id.container, fragment)
                                            .hide(fragmentSelf)
                                            .addToBackStack(Util.BACK_STACK_FAVORITE)
                                            .commit();
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                vStatus.setText(R.string.string_connection_error);
                vLoading.setVisibility(View.INVISIBLE);
                vStatus.setVisibility(View.VISIBLE);

            }
        });
        mQueue.add(jsonArrayRequest);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.menu_favorite));

    }
}
