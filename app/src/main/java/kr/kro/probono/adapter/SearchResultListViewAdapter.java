package kr.kro.probono.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.List;

import kr.kro.probono.R;

public class SearchResultListViewAdapter extends ArrayAdapter<TMapPOIItem> {

    private final List<TMapPOIItem> searched;
    private final TMapPoint currentLocation;

    public SearchResultListViewAdapter(@NonNull Context context, int resource, List<TMapPOIItem> searched, TMapPoint currentLocation) {
        super(context, resource);
        this.searched = searched;
        this.currentLocation = currentLocation;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.searched_result_row, parent, false);
        }

        TextView distanceView = v.findViewById(R.id.searched_result_distance_text_view);
        TextView infoView = v.findViewById(R.id.searched_result_info_text_view);

        TMapPOIItem item = searched.get(position);

        if (item != null) {
            int distance = (int) item.getDistance(currentLocation);
            distanceView.setText(distance + "m");

            String info = item.getPOIName();
            infoView.setText(info);
        }

        return v;
    }
}
