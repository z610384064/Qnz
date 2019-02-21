package com.rd.qnz.util;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * 自定义volley request
 */
public class PostJsonObjectRequest extends JsonRequest<JSONObject> {

	public PostJsonObjectRequest(int method, String url, JSONObject param,
								 Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, (param == null)?null:param.toString(), listener, errorListener);
		int socketTimeout = 10000;//10 seconds
		RetryPolicy policy = new DefaultRetryPolicy(
		     socketTimeout,           
		     0 ,      //DefaultRetryPolicy.DEFAULT_MAX_RETRIES
		     DefaultRetryPolicy.DEFAULT_BACKOFF_MULT );
		this.setRetryPolicy(policy);
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
            String jsonString =
                    new String(response.data , HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
	}

}
