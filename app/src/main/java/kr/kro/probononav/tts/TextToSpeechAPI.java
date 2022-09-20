package kr.kro.probononav.tts;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

import javax.net.ssl.HttpsURLConnection;

import kr.kro.probononav.tts.option.TTSOption;

public class TextToSpeechAPI {


    public static void speechAsync(boolean stopNowSpeeching) {

    }

    public static void speechSync() {

    }

    public static boolean isSpeeching() {

    }

    public static void stopNowSpeeching() {

    }


    private TextToSpeechAPI() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }



    public static void writeToFile(TextToSpeech tts, File file) throws IOException {
        URL url = new URL(TTSOption.API_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "KakaoAK " + TTSOption.API_AUTH_KEY);
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);


        try (OutputStream out = connection.getOutputStream()) {
            out.write(("<speak>" + tts.toVoiceFormat() + "</speak>").getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
            int data;
            try (InputStream in = connection.getInputStream(); OutputStream os = new FileOutputStream(file)){
                while ((data = in.read()) != -1) {
                    os.write(data);
                }
            }
        } finally {
            connection.disconnect();
        }

    }

}
