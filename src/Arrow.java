import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Arrow {
    public Arrow(MainGame game, TextureRegion texture, Vector2 pos, Vector2 target, float damage, float speed) {
        this.game = game;
        this.texture = texture;
        this.damage = damage;
        this.speed = speed;

        this.width = texture.getRegionWidth() / 4;
        this.height = texture.getRegionHeight() / 4;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.bullet = true;
        bodyDef.position.x = pos.x;
        bodyDef.position.y = pos.y;

        CircleShape boundingCircle = new CircleShape();
        boundingCircle.setRadius(0.5f * width / this.game.pixelsInMeter);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingCircle;
        fixtureDef.friction = 0;
        fixtureDef.density = 1;
        fixtureDef.restitution = 1;
        fixtureDef.filter.groupIndex = -2;

        this.body = this.game.physics.createBody(bodyDef);
        this.body.setUserData(this);
        this.body.createFixture(fixtureDef);

        Vector2 direction = new Vector2(target).sub(pos);
        direction.scl(speed / direction.len());
        this.body.setLinearVelocity(direction);
    }

    MainGame game;

    TextureRegion texture;
    float damage;
    float speed;

    int width;
    int height;

    Body body;

    public void draw(float delta, SpriteBatch spriteBatch) {
        Vector2 pos = this.body.getPosition().scl(this.game.pixelsInMeter).sub(this.width / 2, this.height / 2);

        float offAngle = this.body.getLinearVelocity().angle() - 90;
        spriteBatch.draw(texture, pos.x, pos.y, this.width / 2, this.height / 2, this.width, this.height, 1, 1, offAngle);
    }

    protected boolean active = true;

    public void destroy() {
        this.game.toDestroy.add(this.body);
        active = false;
    }
}
