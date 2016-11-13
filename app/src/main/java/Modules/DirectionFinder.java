package Modules;

import android.os.AsyncTask;
import android.util.Log;

import com.EPDev.PromotionAppIcc.NearActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.messages.Distance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyDg1_pewVhYRuMzkS4F7BMsR_i7pZL86sc";
    private DirectionFinderListener listener;
    private LatLng origin;
    private LatLng destination;

    public DirectionFinder(DirectionFinderListener listener, LatLng origin, LatLng destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {

        String urlLatOrigin = URLEncoder.encode(String.valueOf(origin.latitude), "utf-8");
        String urlLngOrigin = URLEncoder.encode(String.valueOf(origin.longitude), "utf-8");

        String urlLatDestination = URLEncoder.encode(String.valueOf(destination.latitude), "utf-8");
        String urlLngDestination = URLEncoder.encode(String.valueOf(destination.longitude), "utf-8");

        String URL = DIRECTION_URL_API
                .concat("origin=").concat(urlLatOrigin).concat(",").concat(urlLngOrigin)
                .concat("&destination=").concat(urlLatDestination).concat(",").concat(urlLngDestination)
                .concat("&key=").concat(GOOGLE_API_KEY);
        Log.e("CREATE_URL_FINDER","This is my complete url: "+URL);
        return URL;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.e("DIRECTION_FINDER","Before onPostExecute");
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {

        Log.e("DIRECTION_FINDER","Before parseJson");

        if (data == null)
            return;

        Log.e("DIRECTION_FINDER","Before parseJson, data =! null");

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.distances = new Distances(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            Log.e("DIRECTION_FINDER","Before add routes");

            routes.add(route);
        }

        Log.e("DIRECTION_FINDER","After add routes");
        listener.onDirectionFinderSuccess(routes);

    }

    private List<LatLng> decodePolyLine(final String poly) {
        Log.e("DIRECTION_FINDER","Before decodePolyLine");
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }
        Log.e("DIRECTION_FINDER","After decodePolyLine");
        return decoded;
    }
}