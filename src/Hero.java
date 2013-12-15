import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class Hero extends Character {
    public Hero(MainGame game, TextureRegion[] frames, Vector2 position) {
        super(game, frames, 12, position, (short) -1, 100);

        this.moveSpeed = 10f;
        this.bullet = new TheBullet(this.game, new Animation(0.1f,
                TextureRegion.split(new Texture("assets/bullet.png"), 16, 16)[0]), 4, 4);
    }

    float moveSpeed;
    boolean hasBullet = true;
    TheBullet bullet;

    public void update(float delta) {
        Vector2 velocity = new Vector2(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y += 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x -= 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x += 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y -= 1;
        }

        float scale = 0.0f;
        float speed = velocity.len();
        if (speed > 0) {
            scale = this.moveSpeed / speed;
        }


        body.setLinearVelocity(velocity.scl(scale));

        if (hasBullet && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Ray ray = this.game.camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Vector3 mouse = ray.origin.scl(1 / this.game.pixelsInMeter);
            Vector2 pos = getPosition();

            hasBullet = false;
            this.bullet.fire(pos, new Vector2(mouse.x, mouse.y).sub(pos));
            this.game.shoot.play();
        }
    }

    public void catchBullet(TheBullet bullet) {
        this.game.catchBullet.play();
        bullet.destroy();
        this.hasBullet = true;
        int kills = bullet.getKillCount();
        bullet.resetKillCount();
        if (kills > 1) {
            this.game.announce(kills + " KILL COMBO!!", 1);
        }
    }

    public void die() {
        super.die();
        this.game.announce("YOU DIED! Obviously.", 3.0f);
    }

    public void draw(float delta, SpriteBatch spriteBatch) {
        super.draw(delta, spriteBatch);

        if (!this.hasBullet) {
            this.bullet.draw(delta, spriteBatch);
        } else if (!this._goingUp) {
            this.bullet.draw(delta, spriteBatch, getPosition().add(0, -0.23f));
        }
    }

    public void applyDamage(float damage) {
        super.applyDamage(damage);
        this.game.damage.play();
    }
}
