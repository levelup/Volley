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

package com.android.volleyextended;

import org.apache.http.HttpStatus;

import java.util.Map;


/**
 * <p>
 * Data and headers returned from {@link Network#performRequest(Request)} or from {@link CacheDispatcher}.
 * </p>
 * <p>
 * The response can have 3 states:
 * <dl>
 * <dt><code>notModified = false</code> and <code>cached = false</code></dt>
 * <dd>completely new response</dd>
 * <dt><code>notModified = false</code> and <code>cached = true</code></dt>
 * <dd>previously cached response, not expired (response comes from cache, no internet access)</dd>
 * <dt><code>notModified = true</code> and <code>cached = true</code></dt>
 * <dd>previously cached response, expired but not modified (HTTP 304) (response comes from cache but was verified through internet access)</dd>
 * </dl>
 * </p>
 */
public class NetworkResponse {


  /**
   * Creates a new network response.
   * 
   * @param statusCode
   *          the HTTP status code
   * @param data
   *          Response body
   * @param headers
   *          Headers returned with this response, or null for none
   * @param notModified
   *          True if the server returned a 304 and the data was
   *          already in cache
   * @param cached
   *          True if the response comes from cache
   */
  public NetworkResponse(final int statusCode, final byte[] data, final Map<String, String> headers, final boolean notModified, final boolean cached) {

    this.statusCode = statusCode;
    this.data = data;
    this.headers = headers;
    this.notModified = notModified;
    this.cached = cached;
  }

  public NetworkResponse(final byte[] data, final Map<String, String> headers, final boolean cached) {

    this(HttpStatus.SC_OK, data, headers, false, cached);
  }


  /** The HTTP status code. */
  public final int                 statusCode;

  /** Raw data from this response. */
  public final byte[]              data;

  /** Response headers. */
  public final Map<String, String> headers;

  /** True if the server returned a 304 (Not Modified). */
  public final boolean             notModified;

  /** True if the response comes from cache. */
  public final boolean             cached;
}
