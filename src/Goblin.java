import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Goblin extends Enemy {
    public Goblin(MainGame game, TextureRegion[] frames, Vector2 position, float health, float damage) {
        super(game, frames, 10, position, health);

        this.damage = damage;
    }

    float damage;

    public void touchHero(Hero hero) {
        hero.applyDamage(this.damage);
    }

    public void hitByTheBullet(TheBullet bullet) {
        this.applyDamage(bullet.getDamage());
    }
}
