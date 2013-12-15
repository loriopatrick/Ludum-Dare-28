import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Obstacle {
    public Obstacle(MainGame game, TextureRegion texture, int pWidth, int pHeight, float x, float y) {
        this.game = game;
        this.texture = texture;

        this.width = pWidth;
        this.height = pHeight;

        this.x = x;
        this.y = y;

        this.boxSize = new Vector2(0.5f * this.width / this.game.pixelsInMeter,
                0.5f * this.height / this.game.pixelsInMeter);
        this.center = new Vector2(0, 0);

        this.buildPhysicsBody();
    }

    MainGame game;
    TextureRegion texture;
    Body body;

    int width;
    int height;

    float x;
    float y;
    float px;
    float py;

    Vector2 boxSize;
    Vector2 center;

    public void buildPhysicsBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.x = x;
        bodyDef.position.y = y;

        PolygonShape boundingBox = new PolygonShape();
        boundingBox.setAsBox(boxSize.x, boxSize.y, center, 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingBox;
        fixtureDef.friction = 0;
        fixtureDef.density = 0;

        this.body = this.game.physics.createBody(bodyDef);
        this.body.setUserData(this);
        this.body.createFixture(fixtureDef);

        this.px = x * this.game.pixelsInMeter - this.width / 2;
        this.py = y * this.game.pixelsInMeter - this.height / 2;
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, px, py, this.width, this.height);
    }
}
