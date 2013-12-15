import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Character {
    public Enemy(MainGame game, TextureRegion[] frames, int height, Vector2 position, float health) {
        super(game, frames, height, position, (short)-2, 10);
    }

    public void onHero(Hero hero) {
    }

    public void offHero(Hero hero) {
    }

    public void hitByTheBullet(TheBullet bullet) {
    }
}
