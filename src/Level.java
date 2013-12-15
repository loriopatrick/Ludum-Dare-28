import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class Level {
    public Level(MainGame game, TextureRegion[][] tiles, float tileSize, Color wall, Color enemySpawn, Color heroSpan) {
        this.game = game;
        this.tiles = tiles;
        this.tileSize = tileSize;

        this.wall = wall;
        this.enemySpawn = enemySpawn;
        this.heroSpawn = heroSpan;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.x = 0;
        bodyDef.position.y = 0;

        this.body = this.game.physics.createBody(bodyDef);
        this.body.setUserData(this);
    }

    MainGame game;
    TextureRegion[][] tiles;
    Vector2[] heroSpawns;
    Vector2[] enemySpawns;
    Color wall;
    Color enemySpawn;
    Color heroSpawn;

    float tileSize;
    Body body;

    public void addRectFixture(Vector2 bottomLeft, Vector2 topRight, short groupIndex) {
        PolygonShape boundingBox = new PolygonShape();
        Vector2 center = topRight.sub(bottomLeft).scl(0.5f);
        boundingBox.setAsBox(center.x, center.y, center.add(bottomLeft), 0.0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingBox;
        fixtureDef.friction = 0;
        fixtureDef.density = 500;
        fixtureDef.filter.groupIndex = groupIndex;

        this.body.createFixture(fixtureDef);
    }



    public void setupLevel() {
        TextureData td = this.tiles[0][0].getTexture().getTextureData();
        td.prepare();
        Pixmap pixmap = td.consumePixmap();

        ArrayList<Vector2> eSpawns = new ArrayList<Vector2>();
        ArrayList<Vector2> hSpawns = new ArrayList<Vector2>();

        // build fixtures
        for (int y = 0; y < this.tiles.length; ++y) {
            for (int x = 0; x < this.tiles[y].length; ++x) {
                int py = this.tiles.length - y - 1;
                TextureRegion region = this.tiles[y][x];
                Color pixel = new Color(pixmap.getPixel(region.getRegionX(), region.getRegionY()));

                if (pixel.equals(wall)) {
                    Vector2 bl = new Vector2(x, py).scl(this.tileSize);
                    addRectFixture(bl, new Vector2(bl).add(this.tileSize, this.tileSize), (short)0);
                } else if (pixel.equals(enemySpawn)) {
                    Vector2 bl = new Vector2(x, py).scl(this.tileSize);
                    eSpawns.add(new Vector2(bl).add(tileSize / 2, tileSize / 2));
                    addRectFixture(bl, new Vector2(bl).add(this.tileSize, this.tileSize), (short) -2);
                } else if (pixel.equals(heroSpawn)) {
                    hSpawns.add(new Vector2((x + 0.5f) * tileSize, (py + 0.5f) * tileSize));
                }
            }
        }

        enemySpawns = new Vector2[eSpawns.size()];
        eSpawns.toArray(enemySpawns);

        heroSpawns = new Vector2[hSpawns.size()];
        heroSpawns = hSpawns.toArray(heroSpawns);
    }

    public Vector2 getHeroSpawn() {
        return heroSpawns[(int)(Math.random() * heroSpawns.length)];
    }

    public Vector2 getEnemySpawn() {
        return enemySpawns[(int)(Math.random() * enemySpawns.length)];
    }

    public void draw(SpriteBatch spriteBatch) {
        float tilePixelSize = this.tileSize * this.game.pixelsInMeter;

        for (int y = 0; y < this.tiles.length; ++y) {
            for (int x = 0; x < this.tiles[y].length; ++x) {
                spriteBatch.draw(this.tiles[y][x],
                        x * tilePixelSize, (this.tiles.length - 1 - y) * tilePixelSize,
                        tilePixelSize,
                        tilePixelSize);
            }
        }
    }

    public void dispose() {
        this.tiles[0][0].getTexture().dispose();
    }
}
