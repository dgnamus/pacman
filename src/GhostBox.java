import java.util.ArrayList;
import java.util.List;

public class GhostBox<T extends Ghost> {
    private final List<T> ghosts = new ArrayList<>();

    public void addGhost(T ghost) {
        ghosts.add(ghost);
    }

    public List<T> getGhosts() {
        return ghosts;
    }
}
