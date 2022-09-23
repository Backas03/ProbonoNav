package kr.kro.probono.tts;

public class TextToSpeech {

    private Voice voice;
    private String tts;
    private float volume;

    public TextToSpeech(Voice voice, String tts) {
        this(voice, tts, 1);
    }

    public TextToSpeech(Voice voice, String tts, float volume) {
        this.voice = voice;
        this.tts = tts;
        this.volume = volume;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public String getText() {
        return tts;
    }

    public void setText(String tts) {
        this.tts = tts;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public String toVoiceFormat() {
        return "<voice name=\"" + voice.getName() + "\">" + tts + "</voice>";
    }

}
