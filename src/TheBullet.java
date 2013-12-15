import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class TheBullet {
    public TheBullet(MainGame game, Animation animation, int width, int height) {
        this.game = game;
        this.animation = animation;
        this.width = width;
        this.height = height;
    }

    MainGame game;
    Animation animation;
    Body body;

    int width;
    int height;

    private int killCount = 0;

    public void addKill() {
        ++killCount;
    }

    public int getKillCount() {
        return killCount;
    }

    public void resetKillCount() {
        killCount = 0;
    }

    public void create(Vector2 pos) {
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
        fixtureDef.filter.groupIndex = -1;

        this.body = this.game.physics.createBody(bodyDef);
        this.body.setUserData(this);
        this.body.createFixture(fixtureDef);
    }

    public void destroy() {
        this.game.toDestroy.push(this.body);
    }

    public void fire(Vector2 pos, Vector2 direction) {
        create(pos);
        Vector2 unitDirection = direction.scl(1 / direction.len());
        this.body.applyForce(unitDirection.scl(50), new Vector2(0, 0), true);
    }

    public float getDamage() {
        return 10.0f;
    }

    float _elapsedTime = 0.0f;
    public void draw(float delta, SpriteBatch spriteBatch, Vector2 pos) {
        _elapsedTime += delta;
        TextureRegion frame = this.animation.getKeyFrame(_elapsedTime, true);
        pos = pos.scl(this.game.pixelsInMeter).sub(this.width / 2, this.height / 2);
        spriteBatch.draw(frame, pos.x, pos.y, this.width, this.height);
    }

    public void draw(float delta, SpriteBatch spriteBatch) {
        draw(delta, spriteBatch, this.body.getPosition());
    }

    private boolean _heroCollide = false;
    public void allowHeroCollide() {
        Filter filter = new Filter();
        filter.groupIndex = 0;
        this.body.getFixtureList().get(0).setFilterData(filter);
        _heroCollide = true;
    }

    public boolean isHeroCollideAllowed() {
        return _heroCollide;
    }
}
