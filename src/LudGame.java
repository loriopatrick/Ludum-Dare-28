import com.badlogic.gdx.Game;

public class LudGame extends Game {
    public LevelDef[] levelDefs = {
            new LevelDef("One Goblin", "level4", 1, 0),
            new LevelDef("Two Goblins", "level2", 2, 0),
            new LevelDef("Hold Down Left Mouse", "level6", 6, 0),
            new LevelDef("Kill Combo", "level3", 30, 0),
            new LevelDef("Bounce it off their heads", "level1", 20, 0),
            new LevelDef("New Tactics", "level2", 0, 1),
            new LevelDef("Getting Hard?", "level2", 0, 3),
            new LevelDef("Here we go", "level4", 5, 2),
            new LevelDef("Ramp it up a bit", "level1", 15, 3),
            new LevelDef("Is One Orb Enough?", "level5", 10, 6)
    };

    String[] levelFiles = {"level1", "level2", "level3", "level4", "level5"};

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
        if (currentLevel >= levelDefs.length) {
            setLevel(new LevelDef("Random Level",
                    levelFiles[(int)Math.round(Math.random() * (levelFiles.length - 1))],
                    (int)(Math.random() * 30), (int)(Math.random() * 10)));
            return;
        }
        setLevel(levelDefs[currentLevel++]);
    }

    public void pauseGame() {
    }

    public void resumeGame() {
    }
}
