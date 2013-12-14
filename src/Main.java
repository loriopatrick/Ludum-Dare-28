import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Main {
    public static void main(String[] args) {
        LudGame game = new LudGame();
        new LwjglApplication(game, "Lud 28", 800, 600, false);
    }
}
