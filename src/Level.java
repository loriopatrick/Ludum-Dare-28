import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class Level {
    public Level(MainGame game, Hero hero, int levelRadius) {
        this.game = game;
        this.hero = hero;
        this.levelRadius = levelRadius;

        this.groundTiles = TextureRegion.split(new Texture("assets/grounds.png"), 16, 16)[0];
        this.tileSize = 16;
    }

    MainGame game;
    Hero hero;
    int levelRadius;
    int tileSize;

    TextureRegion[] groundTiles;

    public void draw(SpriteBatch spriteBatch) {
    }
}
