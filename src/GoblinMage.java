import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class GoblinMage extends Enemy {
    public GoblinMage(MainGame game, TextureRegion[] frames, TextureRegion arrow, Vector2 position, float health, float damage) {
        super(game, frames, 10, position, health);

        this.arrow = arrow;
        this.damage = damage;
        this.arrows = new ArrayList<Arrow>();
    }

    float damage;
    float speed = 2f;

    TextureRegion arrow;
    ArrayList<Arrow> arrows;

    private float shootTime = 0.5f;
    private float shootTimeElapsed = 0.0f;
    public void update(float delta) {
        Vector2 target = this.game.hero.getPosition();
        Vector2 position = this.body.getPosition();

        shootTimeElapsed += delta;
        if (shootTimeElapsed >= shootTime) {
            shootTimeElapsed = 0;
            arrows.add(new Arrow(this.game, arrow, position, target, damage, 10));
            this.game.shootArrow.play();
        }


        Vector2 velocity = new Vector2(target).sub(position);
        if (velocity.len() > 5f) {
            velocity.scl(speed / velocity.len());
            this.body.setLinearVelocity(velocity);
        }
    }

    public void draw(float delta, SpriteBatch spriteBatch) {
        super.draw(delta, spriteBatch);

        for (int i = 0; i < arrows.size();) {
            Arrow arrow = arrows.get(i);
            if (!arrow.active) {
                arrows.remove(i);
                continue;
            }

            arrows.get(i).draw(delta, spriteBatch);
            ++i;
        }
    }

    public void hitByTheBullet(TheBullet bullet) {
        this.applyDamage(bullet.getDamage());
    }
}
