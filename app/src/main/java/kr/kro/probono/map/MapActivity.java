package kr.kro.probono.map;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapView;

import kr.kro.probono.APIKeys;
import kr.kro.probono.R;

public class MapActivity extends AppCompatActivity {

    private TMapView view;

    public TMapView getView() {
        return view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_map);
        super.onCreate(savedInstanceState);

        /* init map view start */
        FrameLayout container = findViewById(R.id.TMapViewContainer);
        view = new TMapView(this);
        container.addView(view);
        view.setSKTMapApiKey(APIKeys.SKT_MAP_API_KEY);
    }


}
