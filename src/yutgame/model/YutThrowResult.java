package yutgame.model;

/**
 * 윷 던지기 결과를 나타내는 Enum.
 */
public enum YutThrowResult {
    BACKDO(-1, "빽도"),
    DO(1, "도"),
    GAE(2, "개"),
    GEO(3, "걸"),
    YUT(4, "윷"),
    MO(5, "모");

    private final int value;
    private final String display;

    YutThrowResult(int value, String display) {
        this.value = value;
        this.display = display;
    }

    public int getValue() {
        return value;
    }

    public String getDisplay() {
        return display;
    }

    @Override
    public String toString() {
        return display;
    }
}


