package com.android.volley.toolbox;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader.ImageListener;

/**
 * Container object for all of the data surrounding an image request.
 */
public class ImageContainer {
    /**
     * The most relevant bitmap for the container. If the image was in cache, the
     * Holder to use for the final bitmap (the one that pairs to the requested URL).
     */
    protected Bitmap mBitmap;

    private final ImageLoader mImageLoader;

    final ImageListener mListener;

    /** The cache key that was associated with the request */
    private final String mCacheKey;

    /** The request URL that was specified */
    private final String mRequestUrl;

    /**
     * Constructs a BitmapContainer object.
     * @param imageLoader The {@link ImageLoader} used to cancel the running job
     * @param bitmap The final bitmap (if it exists).
     * @param requestUrl The requested URL for this container.
     * @param cacheKey The cache key that identifies the requested URL for this container.
     */
    public ImageContainer(ImageLoader imageLoader, Bitmap bitmap,
            String requestUrl, String cacheKey, ImageListener listener) {
        mImageLoader = imageLoader;
        mBitmap = bitmap;
        mRequestUrl = requestUrl;
        mCacheKey = cacheKey;
        mListener = listener;
    }

    /**
     * Releases interest in the in-flight request (and cancels it if no one else is listening).
     */
    public void cancelRequest() {
        if (mListener == null) {
            return;
        }

        mImageLoader.cancelRequest(this);
    }

    /**
     * Returns the bitmap associated with the request URL if it has been loaded, null otherwise.
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * Returns the requested URL for this container.
     */
    public String getRequestUrl() {
        return mRequestUrl;
    }
    
    /**
     * The cache key that was associated with the request
     */
    String getCacheKey() {
        return mCacheKey;
    }
}