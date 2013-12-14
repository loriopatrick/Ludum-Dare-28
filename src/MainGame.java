import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Stack;

public class MainGame implements Screen {
    public MainGame(LudGame game) {

        // Setup the CORE stuff
        this.game = game;
        this.physics = new World(new Vector2(0, 0), true);
        this.collision = new GameCollision();
        this.debug = new Box2DDebugRenderer(); // temp
        this.physics.setContactListener(this.collision);
        this.toDestroy = new Stack<Body>();
        this.spriteBatch = new SpriteBatch();

        this.camera = new OrthographicCamera(1, 1);
        this.camera.zoom = 0.1f;

        // load assets
        TextureRegion[] heroFrames = Character.loadFrames("assets/hero.png");
        TextureRegion[] goblinFrames = Character.loadFrames("assets/goblin.png");

        // load gameplay items
        this.hero = new Hero(this, heroFrames);
    }

    private float width;
    private float height;
    public float pixelsInMeter = 10;

    public final LudGame game;
    public final World physics;
    private GameCollision collision;
    public Stack<Body> toDestroy;
    private Box2DDebugRenderer debug;
    private SpriteBatch spriteBatch;
    public final OrthographicCamera camera;

    public Hero hero;

    @Override
    public void render(float delta) {
        GL11 gl = Gdx.gl11;
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);

        this.camera.update();

        this.hero.update(delta);

        while (toDestroy.size() > 0) {
            Body body = toDestroy.pop();
            if (!body.isActive()) continue;
            this.physics.destroyBody(body);
        }

        this.physics.step(delta, 10, 10);

        spriteBatch.setProjectionMatrix(this.camera.combined);
        spriteBatch.begin();
        spriteBatch.setColor(1, 1, 1, 1);
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
        this.physics.dispose();
    }
}
