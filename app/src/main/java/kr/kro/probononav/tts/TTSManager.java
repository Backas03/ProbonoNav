package kr.kro.probononav.tts;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import kr.kro.probononav.tts.option.TTSOption;

public class TTSManager {

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
