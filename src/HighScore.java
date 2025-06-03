import java.io.Serializable;

public class HighScore implements Serializable {
    private String playerName;
    private int highScore;

    public HighScore(String playerName, int highScore) {
        this.playerName = playerName;
        this.highScore = highScore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getHighScore() {
        return highScore;
    }

    @Override
    public String toString() {
        return playerName + ": " + highScore;
    }
}
