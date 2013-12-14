import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Character {
    public Enemy(MainGame game, TextureRegion[] frames, Vector2 position, float health) {
        super(game, frames, position, (short)0, 10);
    }

    public void touchHero(Hero hero) {
    }

    public void hitByTheBullet(TheBullet bullet) {
    }
}
