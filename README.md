VolleyExtended
==============

This is a fork of the [Android Volley](https://android.googlesource.com/platform/frameworks/volley/) project with some changes:

 - If the response comes back it has a flag to indicate if the content has changed or not. This is usefull if you wont to refresh the content of a view. If the content has not changed you don't need to refresh the view.
 - If a network error occurs you get a VolleyError. Now you get also a cached response object if a previous request was successfully.
 