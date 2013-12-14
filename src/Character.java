import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.sql.PseudoColumnUsage;

public class Character {
    public Character(MainGame game, TextureRegion[] frames, Vector2 position, short collisionLayer, float health) {
        this.game = game;
        this.frames = frames;

        this.width = this.frames[0].getRegionWidth();
        this.height = this.frames[0].getRegionHeight();

        this.health = health;

        // Create the physics object
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.x = position.x;
        bodyDef.position.y = position.y;

        PolygonShape boundingBox = new PolygonShape();
        boundingBox.setAsBox(0.5f * this.width / this.game.pixelsInMeter,
                0.5f * (this.height - 6) / this.game.pixelsInMeter);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingBox;
        fixtureDef.friction = 0;
        fixtureDef.density = 100;
        fixtureDef.filter.groupIndex = collisionLayer;

        this.body = this.game.physics.createBody(bodyDef);
        this.body.setUserData(this);
        this.body.createFixture(fixtureDef);
    }

    MainGame game;
    TextureRegion[] frames;
    Body body;

    int width;
    int height;

    public float health;

    public void update(float delta) {
    }

    public void applyDamage(float damage) {
        this.health -= damage;

        if (this.health <= 0) {
            this.die();
        }
    }

    public void destroyPhysics() {
        this.game.toDestroy.push(this.body);
    }

    private Vector2 _deathPosition;
    private boolean _dead;

    public void die() {
        this._deathPosition = getRenderPosition().sub(this.width / 2, this.height / 2);
        this._dead = true;
        destroyPhysics();
    }

    public boolean isDead() {
        return this._dead;
    }

    private float _aniTimeBucket = 0.0f;
    private float _aniFrameTime = 0.1f;
    private int _aniFrame = 0;
    private int _aniFrames = 2;

    private boolean _flipHorizontal;
    private boolean _goingUp;

    public Vector2 getRenderPosition() {
        Vector2 pos = getPosition().scl(this.game.pixelsInMeter);
        return new Vector2(pos.x - this.width / 2, pos.y - this.height / 2);
    }

    public Vector2 getPosition() {
        return this.body.getPosition();
    }

    public void draw(float delta, SpriteBatch spriteBatch) {
        if (isDead()) {
            spriteBatch.draw(frames[6], _deathPosition.x, _deathPosition.y, this.width, this.height);
            return;
        }

        TextureRegion frame;
        Vector2 velocity = body.getLinearVelocity();

        // update animation
        double speed = Math.sqrt(velocity.dot(velocity));
        if (speed > 0.01) {
            _aniTimeBucket += delta;
            if (_aniTimeBucket > _aniFrameTime) {
                _aniTimeBucket = 0.0f;
                ++_aniFrame;
                if (_aniFrame >= _aniFrames) {
                    _aniFrame = 0;
                }
            }

            if (Math.abs(velocity.x) > 0.01f)
                _flipHorizontal = velocity.x > 0;
            _goingUp = velocity.y > 0;
        } else {
            _aniFrame = -1;
        }


        // draw frame
        if (_aniFrame == -1) {
            frame = this.frames[0];
        } else {
            frame = this.frames[(_aniFrame + 1) * 2 + (_goingUp? 1 : 0)];
        }

        Vector2 renderPos = getRenderPosition();

        frame.flip(_flipHorizontal, false);
        spriteBatch.draw(frame, renderPos.x, renderPos.y,
                this.width, this.height);
        frame.flip(_flipHorizontal, false);
    }

    public static TextureRegion[] loadFrames(String assetSource) {
        return TextureRegion.split(new Texture(assetSource), 8, 16)[0];
    }
}
