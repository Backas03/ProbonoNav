package kr.kro.probono;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import kr.kro.probono.R;

public class POISearchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_poi);
        super.onCreate(savedInstanceState);

        initListener(findViewById(R.id.button_poi_search), findViewById(R.id.edit_text_poi_search));
    }

    private void initListener(Button button, EditText editText) {
        button.setOnClickListener(view -> {
            String input = editText.getText().toString();
            if (input.isEmpty()) {
                // TODO TTS
                return;
            }
            TMapData data = new TMapData();
            try {
                List<TMapPOIItem> searched = data.findAllPOI(input);



            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        });
    }

}
