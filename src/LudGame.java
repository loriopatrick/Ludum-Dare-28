import com.badlogic.gdx.Game;

public class LudGame extends Game {
    public LevelDef[] levelDefs = {
            new LevelDef("One Goblin", "level2", 1, 0),
            new LevelDef("Two Goblins", "level2", 2, 0),
            new LevelDef("Twenty Goblins", "level1", 20, 0),
            new LevelDef("One Mage", "level2", 0, 1),
            new LevelDef("Three Mages", "level2", 0, 3),
            new LevelDef("Let the games begin", "level2", 5, 2),
            new LevelDef("Ramp it up", "level1", 15, 3)
    };

    public int currentLevel = 0;

    @Override
    public void create() {
        IntroScreen intro = new IntroScreen(this);
        setScreen(intro);
    }

    MainGame currentGame = null;

    public void setLevel(LevelDef def) {
        if (currentGame != null) currentGame.dispose();
        currentGame = new MainGame(this, def);
        setScreen(currentGame);
    }

    public void nextLevel() {
        setLevel(levelDefs[currentLevel++]);
    }

    public void pauseGame() {
    }

    public void resumeGame() {
    }
}
