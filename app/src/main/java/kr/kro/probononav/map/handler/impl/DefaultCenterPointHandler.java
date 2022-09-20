package kr.kro.probononav.map.handler.impl;

import android.location.Location;
import android.location.LocationManager;

import com.skt.Tmap.TMapView;

import kr.kro.probononav.map.handler.InfCenterPointHandler;

public class DefaultCenterPointHandler implements InfCenterPointHandler {

    @Override
    public float getMinTimeMS() {
        return 1000;
    }

    @Override
    public float getMinDistanceM() {
        return 5;
    }

    @Override
    public String getLocationProvider() {
        return LocationManager.GPS_PROVIDER;
    }

    @Override
    public void onLocationChanged(TMapView view, Location location) {
        view.setCenterPoint(location.getLongitude(), location.getLatitude());
    }

}
