/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volleyextended.toolbox;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.volleyextended.VolleyError;
import com.android.volleyextended.toolbox.ImageLoader.ImageContainer;
import com.android.volleyextended.toolbox.ImageLoader.ImageListener;


/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request.
 */
public class NetworkImageView extends ImageView {


  /** The URL of the network image to load */
  private String         mUrl;

  /**
   * Resource ID of the image to be used as a placeholder until the network image is loaded.
   */
  private int            mDefaultImageId;

  /**
   * Resource ID of the image to be used if the network response fails.
   */
  private int            mErrorImageId;

  /** Local copy of the ImageLoader. */
  private ImageLoader    mImageLoader;

  /** Current ImageContainer. (either in-flight or finished) */
  private ImageContainer mImageContainer;


  public NetworkImageView(final Context context) {

    this(context, null);
  }

  public NetworkImageView(final Context context, final AttributeSet attrs) {

    this(context, attrs, 0);
  }

  public NetworkImageView(final Context context, final AttributeSet attrs, final int defStyle) {

    super(context, attrs, defStyle);
  }

  /**
   * Sets URL of the image that should be loaded into this view. Note that calling this will
   * immediately either set the cached image (if available) or the default image specified by {@link NetworkImageView#setDefaultImageResId(int)} on
   * the view.
   * NOTE: If applicable, {@link NetworkImageView#setDefaultImageResId(int)} and {@link NetworkImageView#setErrorImageResId(int)} should be called
   * prior to calling
   * this function.
   * 
   * @param url
   *          The URL that should be loaded into this ImageView.
   * @param imageLoader
   *          ImageLoader that will be used to make the request.
   */
  public void setImageUrl(final String url, final ImageLoader imageLoader) {

    this.mUrl = url;
    this.mImageLoader = imageLoader;
    // The URL has potentially changed. See if we need to load it.
    loadImageIfNecessary(false);
  }

  /**
   * Sets the default image resource ID to be used for this view until the attempt to load it
   * completes.
   */
  public void setDefaultImageResId(final int defaultImage) {

    this.mDefaultImageId = defaultImage;
  }

  /**
   * Sets the error image resource ID to be used for this view in the event that the image
   * requested fails to load.
   */
  public void setErrorImageResId(final int errorImage) {

    this.mErrorImageId = errorImage;
  }

  /**
   * Loads the image for the view if it isn't already loaded.
   * 
   * @param isInLayoutPass
   *          True if this was invoked from a layout pass, false otherwise.
   */
  private void loadImageIfNecessary(final boolean isInLayoutPass) {

    final int width = getWidth();
    final int height = getHeight();

    // if the view's bounds aren't known yet, hold off on loading the image.
    if ((width == 0) && (height == 0)) {
      return;
    }

    // if the URL to be loaded in this view is empty, cancel any old requests and clear the
    // currently loaded image.
    if (TextUtils.isEmpty(this.mUrl)) {
      if (this.mImageContainer != null) {
        this.mImageContainer.cancelRequest();
        this.mImageContainer = null;
      }
      setImageBitmap(null);
      return;
    }

    // if there was an old request in this view, check if it needs to be canceled.
    if ((this.mImageContainer != null) && (this.mImageContainer.getRequestUrl() != null)) {
      if (this.mImageContainer.getRequestUrl().equals(this.mUrl)) {
        // if the request is from the same URL, return.
        return;
      } else {
        // if there is a pre-existing request, cancel it if it's fetching a different URL.
        this.mImageContainer.cancelRequest();
        setImageBitmap(null);
      }
    }

    // The pre-existing content of this view didn't match the current URL. Load the new image
    // from the network.
    final ImageContainer newContainer = this.mImageLoader.get(this.mUrl, new ImageListener() {


      @Override
      public void onErrorResponse(final VolleyError error, final ImageContainer cachedResponse) {

        if (NetworkImageView.this.mErrorImageId != 0) {
          setImageResource(NetworkImageView.this.mErrorImageId);
        }
      }

      @Override
      public void onResponse(final ImageContainer response, final boolean isImmediate) {

        // If this was an immediate response that was delivered inside of a layout
        // pass do not set the image immediately as it will trigger a requestLayout
        // inside of a layout. Instead, defer setting the image by posting back to
        // the main thread.
        if (isImmediate && isInLayoutPass) {
          post(new Runnable() {


            @Override
            public void run() {

              onResponse(response, false);
            }
          });
          return;
        }

        if (response.getBitmap() != null) {
          setImageBitmap(response.getBitmap());
        } else if (NetworkImageView.this.mDefaultImageId != 0) {
          setImageResource(NetworkImageView.this.mDefaultImageId);
        }
      }
    });

    // update the ImageContainer to be the new bitmap container.
    this.mImageContainer = newContainer;
  }

  @Override
  protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {

    super.onLayout(changed, left, top, right, bottom);
    loadImageIfNecessary(true);
  }

  @Override
  protected void onDetachedFromWindow() {

    if (this.mImageContainer != null) {
      // If the view was bound to an image request, cancel it and clear
      // out the image from the view.
      this.mImageContainer.cancelRequest();
      setImageBitmap(null);
      // also clear out the container so we can reload the image if necessary.
      this.mImageContainer = null;
    }
    super.onDetachedFromWindow();
  }

  @Override
  protected void drawableStateChanged() {

    super.drawableStateChanged();
    invalidate();
  }
}
