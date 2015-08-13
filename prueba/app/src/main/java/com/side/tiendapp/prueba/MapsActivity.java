package com.side.tiendapp.prueba;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;

    private Geocoder geoCoder;
    private Drawable drawable;
    private LatLng ubicacion;
    private GPSTracker gps;
    private ProgressDialog progressDialog = null;
    private boolean pedidoRegistrado = false;


    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();
    private AsyncHttpResponseHandler responseHandler = null;
    private AsyncHttpClient asyncHttpClient;
    private ArrayList<Objeto> objetos;

    String path = "https://raw.githubusercontent.com/tappsi/test_recruiting/master/sample_files/driver_info.json";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        creaFuncion();

        gps = new GPSTracker(this);

        try{
            asyncHttpClient= new AsyncHttpClient();
            //AsyncHttpClient asyncHttpClientSolicitud = new AsyncHttpClient(true, 8181, 443);
            asyncHttpClient.setTimeout(30000);
            RequestParams params = new RequestParams();

            progressDialog = ProgressDialog.show(MapsActivity.this, "Procesando...", "Espere unos segundos por favor.", true, true);
            asyncHttpClient.get(path, params, responseHandler);
        }catch(Exception e){}


        setContentView(R.layout.activity_maps);
        objetos = new ArrayList<>();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
        geoCoder = new Geocoder(this, Locale.getDefault());

        if (gps.canGetLocation()) {
            if (gps.getLatitude() == 0D) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(4.630363, -74.091217)).title(""));
            } else {
                mMap.addMarker(new MarkerOptions().position(new LatLng(gps.getLatitude(), gps.getLongitude())).title(""));
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(4.630363, -74.091217)).title(""));
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (gps.canGetLocation()) {
            Objeto miUbicacion = new Objeto();
            try {
                List<Address> addresses = geoCoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);
                miUbicacion.setLat(gps.getLatitude());
                miUbicacion.setLon(gps.getLongitude());

                if (addresses.size() > 0) {
                    miUbicacion.setAddress(addresses.get(0).getAddressLine(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            miUbicacion.setBooking_id("Mi Ubicación");
            objetos.add(miUbicacion);
            Objeto centroide = new Objeto();
            for(Objeto obj: objetos){
                mMap.addMarker(new MarkerOptions().position(new LatLng(obj.getLat(), obj.getLon())).title(obj.getBooking_id()+"--"+obj.getAddress()));
                centroide.setLon(centroide.getLon() + obj.getLon());
                centroide.setLat(centroide.getLat() + obj.getLat());
            }

            centroide.setBooking_id("Centroide");
            centroide.setLon(centroide.getLon() / objetos.size());
            centroide.setLat(centroide.getLat() / objetos.size());
            objetos.add(centroide);
            mMap.addMarker(new MarkerOptions().position(new LatLng(centroide.getLat(), centroide.getLon())).title("CENTROIDE"));
            //mMap.addMarker(markerOptions);
            // GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(centroide.getLat(), centroide.getLon()), 16));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(4.630363, -74.091217), 16));
        }


    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }



    private void creaFuncion(){
        try {
            objetos = new ArrayList<>();
            responseHandler = new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(String content) {

                    JSONObject jsonResponse;
                    try {


                        jsonResponse = new JSONObject(content);
                        JSONArray jsonMainNode = jsonResponse.optJSONArray("Android");

                        int lengthJsonArr = jsonMainNode.length();
                        for(int i=0; i < lengthJsonArr; i++)
                        {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            Objeto objeto = new Objeto();
                            objeto.setBooking_id(jsonChildNode.optString("booking_id").toString());
                            objeto.setApprox_address(jsonChildNode.optString("approx_address").toString());
                            objeto.setAddress(jsonChildNode.optString("address").toString());
                            objeto.setNeighborhood(jsonChildNode.optString("neighborhood").toString());
                            objeto.setCode(Integer.parseInt(jsonChildNode.optString("code").toString()));

                            objeto.setLat(Double.parseDouble(jsonChildNode.optString("lat").toString()));
                            objeto.setLon(Double.parseDouble(jsonChildNode.optString("lon").toString()));

                            objetos.add(objeto);
                            //Log.i("JSON parse", song_name);
                        }



                    }  catch (JSONException e) {

                        e.printStackTrace();
                    }

                    setUpMapIfNeeded();
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    try {


                    } catch (Exception e) {

                    }

                }

            };

        } catch (Exception ect) {
            ect.printStackTrace();
        }

    }
}
