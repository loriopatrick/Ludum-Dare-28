import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Hero {
    public Hero(MainGame game, String texture) {
        this.game = game;
        this.frames = TextureRegion.split(new Texture(texture), 8, 16)[0];

        this.width = this.frames[0].getRegionWidth();
        this.height = this.frames[0].getRegionHeight();

        this.moveSpeed = 5f;

        // Create the physics object
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;

        PolygonShape boundingBox = new PolygonShape();
        boundingBox.setAsBox(0.5f * this.width / this.game.pixelsInMeter, 0.5f * (this.height - 6) / this.game.pixelsInMeter);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingBox;
        fixtureDef.friction = 0;
        fixtureDef.density = 1;

        body = this.game.physics.createBody(bodyDef);
        body.createFixture(fixtureDef);
    }

    MainGame game;

    public Body body;

    TextureRegion[] frames;
    int width;
    int height;
    float moveSpeed;


    public void updateInput() {
        float x = 0.0f, y = 0.0f;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += this.moveSpeed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= this.moveSpeed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += this.moveSpeed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= this.moveSpeed;
        }

        body.setLinearVelocity(x, y);
    }

    private float _aniTimeBucket = 0.0f;
    private float _aniFrameTime = 0.1f;
    private int _aniFrame = 0;
    private int _aniFrames = 2;

    private boolean _flipHorizontal;
    private boolean _goingUp;

    public Vector2 getPosition() {
        Vector2 pos = body.getPosition().scl(this.game.pixelsInMeter);
        return new Vector2(pos.x - this.width / 2, pos.y - this.height / 2);
    }

    public void draw(float delta, SpriteBatch spriteBatch) {
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

        Vector2 renderPos = getPosition();

        if (_flipHorizontal) {
            renderPos.x += this.width;
        }

        spriteBatch.draw(frame, renderPos.x, renderPos.y,
                this.width * (_flipHorizontal? -1 : 1), this.height);
    }
}
