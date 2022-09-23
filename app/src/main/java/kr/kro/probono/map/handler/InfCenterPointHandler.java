package kr.kro.probono.map.handler;


import android.location.Location;

import com.skt.Tmap.TMapView;

public interface InfCenterPointHandler {

    float getMinTimeMS();
    float getMinDistanceM();
    String getLocationProvider();

    void onLocationChanged(TMapView view, Location location);

}
