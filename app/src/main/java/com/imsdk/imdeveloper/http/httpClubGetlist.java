package com.imsdk.imdeveloper.http;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONArray;

/**
 * Created by Administrator on 2015/12/19.
 */
public class httpClubGetlist extends AsyncTask<Void,Void,JSONArray>{
    private Activity activity=null;
    public httpClubGetlist(Activity a){
        activity=a;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        return null;
    }
}
