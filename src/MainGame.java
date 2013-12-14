import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import org.lwjgl.Sys;

public class MainGame implements Screen {
    public MainGame(LudGame game) {
        this.game = game;
        this.physics = new World(new Vector2(0, 0), true);
        this.hero = new Hero(this, "assets/hero.png");

        this.camera = new OrthographicCamera(1, 1);
        this.camera.zoom = 0.1f;
        debug = new Box2DDebugRenderer();

        this.floors = TextureRegion.split(new Texture("assets/floor.png"), 16, 16)[0];

        this.test = new Obstacle(this, TextureRegion.split(new Texture("assets/grounds.png"), 16, 16)[0][0], 16, 16, 0, 0);

        this.spriteBatch = new SpriteBatch();
    }

    Box2DDebugRenderer debug;

    public LudGame game;
    public World physics;
    public Hero hero;
    public OrthographicCamera camera;

    public TextureRegion[] floors;

    //test
    public Obstacle test;

    public float centerX;
    public float centerY;

    private float width;
    private float height;

    public float pixelsInMeter = 10;

    private SpriteBatch spriteBatch;

    int x = 0, y = 0;

    @Override
    public void render(float delta) {
        GL11 gl = Gdx.gl11;
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);

        this.camera.update();

        this.hero.updateInput();
        this.physics.step(delta, 10, 10);

        spriteBatch.setProjectionMatrix(this.camera.combined);
        spriteBatch.begin();

        int radius = 12;
        int tileSize = 32;
        int radiusSize = radius * tileSize;

        Vector2 heroPos = hero.getPosition();
        int rx = (int)Math.floor(heroPos.x / tileSize) * tileSize;
        int ry = (int)Math.floor(heroPos.y / tileSize) * tileSize;

        for (int x = rx - radiusSize; x < rx + radiusSize; x += tileSize) {
            for (int y = ry - radiusSize; y < ry + radiusSize; y += tileSize) {
                float dist = heroPos.dst(x, y);
                if (dist > radiusSize) continue;
//                float color = 1 - dist / (float)radiusSize;
                spriteBatch.setColor(0.01f, 0.9f, 0, 1);
                spriteBatch.draw(floors[0], x, y, tileSize, tileSize);
            }
        }

        spriteBatch.setColor(1, 1, 1, 1);
        this.test.draw(spriteBatch);
        this.hero.draw(delta, spriteBatch);
        spriteBatch.end();

        debug.render(this.physics, camera.combined.scl(this.pixelsInMeter));
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        this.camera.viewportWidth = this.width;
        this.camera.viewportHeight = this.height;
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
