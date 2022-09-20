package kr.kro.probononav;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;
import java.util.List;

import kr.kro.probononav.adapter.SearchResultListViewAdapter;

public class SearchResultActivity extends AppCompatActivity {


    public void init(List<TMapPOIItem> searched) {
        setContentView(R.layout.activity_search_result);

        SearchResultListViewAdapter adapter = new SearchResultListViewAdapter(this, R.layout.searched_result_row, searched, )
    }



}
