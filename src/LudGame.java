import com.badlogic.gdx.Game;

public class LudGame extends Game {
    @Override
    public void create() {
        MainGame mainGame = new MainGame(this, "Stay Away From The Goblins", "level1", 20);
        setScreen(mainGame);
    }
}
