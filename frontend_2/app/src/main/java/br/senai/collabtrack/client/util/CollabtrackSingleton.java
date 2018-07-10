package br.senai.collabtrack.client.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import br.senai.collabtrack.client.Application;

/**
 * Created by ezs on 26/10/2017.
 */

public class CollabtrackSingleton {
    private static CollabtrackSingleton singleton = null;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private CollabtrackSingleton() {

        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

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

    public static synchronized CollabtrackSingleton getInstance(){
        return singleton != null ? singleton : (singleton = new CollabtrackSingleton());
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            try {
                requestQueue = Volley.newRequestQueue(Application.getContext());
            } catch (Exception e){
                Application.log(e);
            }
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }


}
