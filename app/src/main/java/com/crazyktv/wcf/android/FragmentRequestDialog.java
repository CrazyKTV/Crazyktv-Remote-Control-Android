package com.crazyktv.wcf.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class FragmentRequestDialog extends DialogFragment
{
    String requestArg;
    RequestQueue mQueue;
    String songId;
    Bundle bundle;
    Boolean isQueue;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mQueue = Volley.newRequestQueue(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_request_dialog, null);
        bundle = getArguments();
        songId = bundle.getString("songId");
        ((TextView) view.findViewById(R.id.text_song_singer)).setText(bundle.getString("songSinger"));
        ((TextView) view.findViewById(R.id.text_song_name)).setText(bundle.getString("songName"));
        if(bundle.getBoolean("isQueue", false)){
            builder.setTitle(R.string.title_queue);
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.string_priority,
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    volley(ActivityMain.PREF_URI + "DoCrazyKTV_Action?state=InsertV&value="+songId);
                                }
                            })
                    .setNegativeButton(R.string.string_delete,
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    volley(ActivityMain.PREF_URI + "DoCrazyKTV_Action?state=Delete&value=" + songId);
                                }
                            });
        }else {
            builder.setTitle(R.string.string_request);
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.string_priority,
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    volley(ActivityMain.PREF_URI + "DoCrazyKTV_Action?state=Insert&value="+songId);
                                }
                            })
                    .setNegativeButton(R.string.string_request,
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    volley(ActivityMain.PREF_URI + "OrderSong?value=" + songId);
                                }
                            });
        }

        return builder.create();
    }

    private void volley(String arg){
        Log.d("Request URL", arg);

        StringRequest jsonArrayRequest = new StringRequest(arg,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);

            }
        });
        mQueue.add(jsonArrayRequest);
    }
}
