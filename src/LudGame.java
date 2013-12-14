import com.badlogic.gdx.Game;

public class LudGame extends Game {
    @Override
    public void create() {
        MainGame mainGame = new MainGame(this);
        setScreen(mainGame);
    }
}
