package mobile.computing.expressstore;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingleton {

    private static RequestQueueSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private RequestQueueSingleton(Context context)
    {
        mCtx=context.getApplicationContext();
        mRequestQueue=getRequestQueue();
    }

    public static synchronized RequestQueueSingleton getInstance(Context context){
        if(mInstance==null)
        {
            mInstance=new RequestQueueSingleton(context.getApplicationContext());
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null)
        {
            mRequestQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}
