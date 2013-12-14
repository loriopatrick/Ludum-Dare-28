import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Octopus on 12/14/13.
 */
public class Goblin extends Enemy {
    public Goblin(MainGame game, TextureRegion[] frames, Vector2 position, float health, float damage) {
        super(game, frames, position, health);
    }
}
