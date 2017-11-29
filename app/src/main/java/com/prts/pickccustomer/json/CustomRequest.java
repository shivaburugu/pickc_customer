package com.prts.pickccustomer.json;

/**
 * Created by Uday on 30-07-2016.
 */

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class CustomRequest extends Request<Boolean> {

    private static final String TAG = "CustomRequestJSONObj";
    private Listener<Boolean> listener;
    private Map<String, String> params;

    public CustomRequest(String url, Map<String, String> params,
                         Listener<Boolean> reponseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    public CustomRequest(int method, String url, Map<String, String> params,
                         Listener<Boolean> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    };

    @Override
    protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.d(TAG, "parseNetworkResponse: "+jsonString);
            return null;
           /* return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));*/
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(Boolean response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}