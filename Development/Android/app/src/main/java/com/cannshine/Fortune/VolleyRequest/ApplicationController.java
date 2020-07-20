package com.cannshine.Fortune.VolleyRequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class ApplicationController {
    private static ApplicationController mApplicationController;
    private RequestQueue requestQueue;
    private Context mContext;
    private ImageLoader imageLoader;

    private ApplicationController(Context context){
        this.mContext = context;
        this.requestQueue = getRequestQueue();
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized ApplicationController getInstance(Context context){
        if(mApplicationController == null){
            mApplicationController = new ApplicationController(context);
        }
        return mApplicationController;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader(){
        return imageLoader;
    }

}
