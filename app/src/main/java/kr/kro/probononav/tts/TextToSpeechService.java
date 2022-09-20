package kr.kro.probononav.tts;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;

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

public class TextToSpeechService extends Service implements MediaPlayer.OnPreparedListener {

    private Queue<TextToSpeech> queue;
    private MediaPlayer player;


    @Override
    public void onCreate() {
        initPlayer();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    public void enqueue(TextToSpeech tts) {
        if (player == null) {
            try {
                play(tts);
            } catch (IOException ignore) {

            }
            return;
        }
        if (queue == null) queue = new LinkedList<>();
        queue.offer(tts);
    }

    private void play(TextToSpeech tts) throws IOException {
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
            File tempFile = File.createTempFile("tempFileTTS", "mp3", getCacheDir());
            tempFile.deleteOnExit();
            try (InputStream in = connection.getInputStream(); FileOutputStream os = new FileOutputStream(tempFile)){
                while ((data = in.read()) != -1) {
                    os.write(data);
                }
                player.setDataSource(os.getFD());
                player.prepare();
                player.start();
                player.setOnCompletionListener(mediaPlayer -> {
                    TextToSpeech textToSpeech = queue.poll();
                    if (textToSpeech != null) {
                        try {
                            play(textToSpeech);
                        } catch (IOException ignore) {
                            getApplicationContext().getSystemService(ALARM_SERVICE)
                        }
                    }
                });
            }
        } finally {
            connection.disconnect();
        }
    }

    private void initPlayer() {
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setOnPreparedListener(this);
        player.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        TextToSpeech tts = queue.poll();
        if (tts != null) {
            try {
                play(tts);
            } catch (IOException ignore) {

            }
        }
    }

}
