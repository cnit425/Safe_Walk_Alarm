package cnit355.purdue.safe_walk_alarm;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Location extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    static LatLng curPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        startLocationService();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
    }

    private class GPSListener implements LocationListener
    {
        public void onLocationChanged(android.location.Location location)
        {
            map.clear();
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Log.i("GPSLocationService", msg);
            showCurrentLocation(latitude, longitude);
        }
        public void onProviderDisabled(String provider)
        {

        }
        public void onProviderEnabled(String provider)
        {

        }
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }
    }

    private void showCurrentLocation(Double latitude, Double longitude)
    {
        curPoint = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        showAllMarkers(latitude, longitude);
    }

    private void showAllMarkers(Double latitude, Double longitude)
    {
        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(latitude + 0.001, longitude + 0.001));
        marker.title("Your current location");
        //marker.snippet("Address");
        marker.draggable(false);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.common_full_open_on_phone));
        map.addMarker(marker);
    }

    private void startLocationService()
    {

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPSListener gpsListener = new GPSListener();
        long minTime = 1000;
        float minDistance = 0;
        try
        {
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);
        }
        catch(SecurityException ex)
        {
            ex.printStackTrace();
        }
    }

    //Enabling location service only when resumed, will result in nullPointerException in this version
    /*@Override
    public void onResume()
    {
        super.onResume();
        try
        {
            map.setMyLocationEnabled(true);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
            return;
        }
    }*/

    /*@Override
    public void onStop()
    {
        super.onStop();
        try
        {
            map.setMyLocationEnabled(false);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
            return;
        }
    }*/
}
