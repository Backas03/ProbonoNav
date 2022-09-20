package kr.kro.probononav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Process;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class MainActivity extends AppCompatActivity {

    // map view from T Map API
    private TMapView tMapView;

    // this uses for checking my request code
    public static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_map);
        super.onCreate(savedInstanceState);

        /* init map view start */
        FrameLayout container = findViewById(R.id.TMapViewContainer);
        tMapView = new TMapView(this);
        container.addView(tMapView);
        tMapView.setSKTMapApiKey(APIKeys.SKT_MAP_API_KEY);
        /* init map view end */

        /* check permission start */
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // if location access permission is not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE); // request location access permission
            return;
        }
        /* check permission end */

        /* set center point to current location start */
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double x = location.getLongitude();
        double y = location.getLatitude();
        tMapView.setCenterPoint(x, y);
        /* set center point to current location end */


        /* init location listener start */
        LocationListener locationListener = loc -> {
            tMapView.setCenterPoint(loc.getLongitude(), loc.getLatitude());
            updateMe(loc.getLongitude(), loc.getLatitude());
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, locationListener);
        /* init location listener end */


        /* update radius of circle "me" when zoom level changed */
        tMapView.setOnDisableScrollWithZoomLevelListener((zoom, point) -> {
            updateMe(point.getLongitude(), point.getLatitude());
        });

    }

    private TMapCircle me;

    private void updateMe(double x, double y) {
        // if circle "me" isn't initialized
        if (me == null) {
            // init circle
            me = new TMapCircle();
            me.setLineColor(Color.BLUE);
            me.setAreaColor(Color.BLUE);
            me.setRadius(1);
            me.longitude = x;
            me.latitude = y;
            tMapView.addTMapCircle("me", me);
            return;
        }
        // else update
        me.longitude = x;
        me.latitude = y;
        me.setRadius(tMapView.getZoomLevel());
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