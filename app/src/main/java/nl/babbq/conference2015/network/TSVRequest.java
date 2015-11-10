package nl.babbq.conference2015.network;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import nl.babbq.conference2015.objects.Conference;

/**
 * Created by nono on 11/1/15.
 */
public class TSVRequest  extends Request<List<Conference>> {
    private final Response.Listener<List<Conference>> mListener;
    private Context mContext;

    public TSVRequest(Context context,
                      int method,
                      String url,
                      Response.Listener<List<Conference>> listener,
                      Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mContext = context;
        setShouldCache(false);
    }

    @Override
    protected Response<List<Conference>> parseNetworkResponse(NetworkResponse response) {
        InputStream inputStream = new ByteArrayInputStream(response.data);
        List<Conference> conferences;
        try {
            conferences = Conference.parseInputStream(mContext, new InputStreamReader(inputStream));
        } catch (Exception e) {
            mContext = null;
            return Response.error(new ParseError(e));
        }
        return Response.success(conferences, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(List<Conference> response) {
        mContext = null;
        mListener.onResponse(response);
    }
}
