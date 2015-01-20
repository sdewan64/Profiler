package com.shaheed.codewarior.checkboxdevelopers;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Shaheed on 1/20/2015.
 * Shaheed Ahmed Dewan Sagar
 * Ahsanullah University of Science & Technology
 * Email : sdewan64@gmail.com
 */
public class VolleyController extends Application {

    private String mTag = "Default Tag";

    private RequestQueue mRequestQueue;

    private static VolleyController mInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public static synchronized VolleyController getInstance(){
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addNewToRequestQueue(Request<T> request, String tag){
        this.mTag = tag;
        request.setTag(mTag);
        getRequestQueue().add(request);
    }

    public void cancelAllRequest(Object tag){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(tag);
        }
    }

}
