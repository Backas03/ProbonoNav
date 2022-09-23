package kr.kro.probono.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import kr.kro.probono.map.handler.InfCenterPointHandler;
import kr.kro.probono.map.handler.impl.DefaultCenterPointHandler;

public class MapLocationManager extends Activity {

    // this uses for checking my request code
    public static final int REQUEST_CODE = 0;

    private LocationManager locationManager;
    private InfCenterPointHandler centerPointHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE); // request location access permission
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        centerPointHandler = new DefaultCenterPointHandler();
    }

    // this method checks permission is granted by user. if permission is denied, application finish itself.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if my request
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            for (int i=0; i<grantResults.length; i++) {
                // if permission isn't granted
                if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                    // finish the app
                    moveTaskToBack(true);
                    finishAndRemoveTask();
                    Process.killProcess(Process.myPid());
                    break;
                }
            }
        }
    }




}
