import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Level {
    public Level(MainGame game, TextureRegion[] textures, float tileSize) {
        this.game = game;
        this.textures = textures;
        this.tileSize = tileSize;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.x = 0;
        bodyDef.position.y = 0;

        this.body = this.game.physics.createBody(bodyDef);
        this.body.setUserData(this);
    }

    MainGame game;
    TextureRegion[] textures;
    float tileSize;
    Body body;

    public void addRectFixture(Vector2 bottomLeft, Vector2 topRight) {
        PolygonShape boundingBox = new PolygonShape();
        Vector2 center = topRight.sub(bottomLeft).scl(0.5f);
        boundingBox.setAsBox(center.x, center.y, center.add(bottomLeft), 0.0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingBox;
        fixtureDef.friction = 0;
        fixtureDef.density = 500;

        this.body.createFixture(fixtureDef);
    }

    int[][] tiles;

    public void createLevel() {
        this.tiles = new int[12][12];

        // build random level
        for (int x = 0; x < this.tiles.length; ++x) {
            for (int y = 0; y < this.tiles[x].length; ++y) {
                boolean solid = (x == this.tiles.length - 1 || y == 0 || y == this.tiles[x].length - 1);
                if (!solid && Math.random() > 0.8f) {
                    solid = true;
                }

                int tile = (int)(Math.random() * this.textures.length);
                if (tile != this.textures.length) tile += 1;
                if (solid) tile *= -1;

                this.tiles[x][y] = solid? -1 : 1;
            }
        }

        // build fixtures
        for (int x = 0; x < this.tiles.length; ++x) {
            for (int y = 0; y < this.tiles.length; ++y) {
                if (this.tiles[x][y] < 0) {
                    addRectFixture(new Vector2(x * this.tileSize, y * this.tileSize), new Vector2(x * this.tileSize + this.tileSize, y * this.tileSize + this.tileSize));
                }
            }
        }
    }

    public Vector2 getHeroStartPos() {
        return new Vector2(12, 12);
    }

    public void draw(SpriteBatch spriteBatch) {
        float tilePixelSize = this.tileSize * this.game.pixelsInMeter;

        for (int x = 0; x < this.tiles.length; ++x) {
            for (int y = 0; y < this.tiles.length; ++y) {
                int tile = this.tiles[x][y];
                if (tile < 0) tile = 1 - tile;
                else tile -= 1;

                if (tile >= this.textures.length) tile = 0;

                spriteBatch.draw(this.textures[tile],
                        x * tilePixelSize, y * tilePixelSize,
                        tilePixelSize,
                        tilePixelSize);
            }
        }
    }
}
