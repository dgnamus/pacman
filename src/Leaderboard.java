import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Leaderboard {
    private static final String FILE_NAME = "leaderboard.txt";
    private final List<HighScore> highScores;

    public Leaderboard() {
        highScores = loadScores();
    }

    public synchronized void addScore(String name, int score) {
        highScores.add(new HighScore(name, score));
        highScores.sort(Comparator.comparingInt(HighScore::getHighScore).reversed());
        saveScores();
    }

    public synchronized List<HighScore> getScores() {
        return new ArrayList<>(highScores);
    }

    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<HighScore> loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<HighScore>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
