package kr.kro.probono.tts;

public class Voice {

    public static Voice MAN_DIALOG_BRIGHT = new Voice("MAN_DIALOG_BRIGHT");
    public static Voice TEST_1 = new Voice("2134123123");

    private final String name;

    private Voice(String value) {
        this.name = value;
    }

    public String getName() {
        return name;
    }

}
