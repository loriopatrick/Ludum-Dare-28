import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Goblin extends Enemy {
    public Goblin(MainGame game, TextureRegion[] frames, Vector2 position, float health, float damage) {
        super(game, frames, 10, position, health);

        this.damage = damage;
    }

    float damage;
    float speed = 5f;

    Hero attacking = null;

    float attackTimeout = 0.2f;
    float attackTimeoutElapsed = 0.0f;

    public void update(float delta) {
        if (this.isDead()) return;
        Vector2 heroPos = this.game.hero.getPosition();
        Vector2 dir = heroPos.sub(this.getPosition());
        dir.setAngle(dir.angle() + 45.0f - 90.0f * (float)Math.random());
        dir.scl(speed / dir.len());
        this.body.setLinearVelocity(dir);

        attackTimeoutElapsed += delta;
        if (attacking != null && attackTimeout <= attackTimeoutElapsed) {
            attacking.applyDamage(this.damage);
            attackTimeoutElapsed = 0.0f;
        }
    }

    public void onHero(Hero hero) {
        attacking = hero;
    }

    public void offHero(Hero hero) {
        attacking = null;
    }

    public void hitByTheBullet(TheBullet bullet) {
        this.applyDamage(bullet.getDamage());
        if (this.isDead()) {
            bullet.addKill();
        }
    }
}
