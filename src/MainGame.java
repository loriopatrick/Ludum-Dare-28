import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import org.lwjgl.Sys;

import java.util.Date;
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
        this.camera.zoom = 0.2f;

        // load assets
        TextureRegion[] heroFrames = Character.loadFrames("assets/hero.png");
        TextureRegion[] goblinFrames = Character.loadFrames("assets/goblin.png");
        TextureRegion[] textures = TextureRegion.split(new Texture("assets/grounds.png"), 16, 16)[0];

        // load gameplay items
        this.level = new Level(this, textures, 1);
        this.level.createLevel();

        this.hero = new Hero(this, heroFrames);
        this.hero.setPosition(this.level.getHeroStartPos());
        this.goblin = new Goblin(this, goblinFrames, new Vector2(1, 1), 10, 10);
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

    public Level level;
    public Hero hero;
    public Goblin goblin;

    private boolean hitEdge = true;

    @Override
    public void render(float delta) {
        GL11 gl = Gdx.gl11;
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);

        this.hero.update(delta);
        this.goblin.update(delta);

        // update the camera position;
        float padding = 15f;
        Vector2 heroRenderPos = this.hero.getRenderPosition();
        Vector3 bottomLeft = new Vector3(heroRenderPos.x - padding, heroRenderPos.y - padding, 0);
        Vector3 topLeft = new Vector3(heroRenderPos.x - padding, heroRenderPos.y + this.hero.height + padding, 0);
        Vector3 bottomRight = new Vector3(heroRenderPos.x + this.hero.width + padding, heroRenderPos.y - padding, 0);
        Vector3 topRight = new Vector3(heroRenderPos.x + this.hero.width + padding, heroRenderPos.y + this.hero.height + padding, 0);

        Vector3 center = new Vector3(bottomLeft.x + this.hero.width, bottomRight.y + this.hero.height, 0);

        if (new Vector3(this.camera.position).sub(center).len() < padding) {
            hitEdge = false;
        } else if (!this.camera.frustum.pointInFrustum(bottomLeft) || !this.camera.frustum.pointInFrustum(topLeft) ||
                !this.camera.frustum.pointInFrustum(bottomRight) || !this.camera.frustum.pointInFrustum(topRight)) {
            hitEdge = true;
        }

        if (hitEdge) {
            Vector3 dir = center.sub(this.camera.position).scl(delta * 5);
            camera.translate(dir);
        }

//        Vector2 heroPos = this.hero.getPosition().scl(this.pixelsInMeter);
//        this.camera.position.x = heroPos.x;
//        this.camera.position.y = heroPos.y;

//        this.camera.position.x = thi
        this.camera.update();

        while (toDestroy.size() > 0) {
            Body body = toDestroy.pop();
            if (!body.isActive()) continue;
            this.physics.destroyBody(body);
        }

        this.physics.step(delta, 10, 10);

        spriteBatch.setProjectionMatrix(this.camera.combined);
        spriteBatch.begin();
        spriteBatch.setColor(1, 1, 1, 1);
        this.level.draw(spriteBatch);
        this.goblin.draw(delta, spriteBatch);
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
