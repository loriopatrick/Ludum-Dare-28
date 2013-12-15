import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Stack;

public class MainGame implements Screen {
    public MainGame(LudGame game, LevelDef levelDef) {

        // Setup the CORE stuff
        this.game = game;
        this.physics = new World(new Vector2(0, 0), true);
        this.collision = new GameCollision(this);
        this.debug = new Box2DDebugRenderer(); // temp
        this.physics.setContactListener(this.collision);
        this.toDestroy = new Stack<Body>();
        this.spriteBatch = new SpriteBatch();
        this.uiSpriteBatch = new SpriteBatch();

        this.camera = new OrthographicCamera(1, 1);
        this.camera.zoom = 0.15f;

        // load assets
        heroFrames = Character.loadFrames("assets/hero.png");
        goblinFrames = Character.loadFrames("assets/goblin.png");
        goblinMageFrames = Character.loadFrames("assets/goblinMage.png");
        TextureRegion[][] tiles = TextureRegion.split(new Texture("assets/" + levelDef.levelFile + ".png"), 1, 1);
        bounce = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/bounce.wav"));
        catchBullet = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/catch.wav"));
        damage = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/damage.wav"));
        shootArrow = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/shootArrow.wav"));
        shootArrow.setVolume(0, 0.1f);
        die = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/die.wav"));

        this.healthFrames = TextureRegion.split(new Texture("assets/health.png"), 16, 2)[0];
        this.font = new BitmapFont();

        // load gameplay items
        this.level = new Level(this, tiles, 1, new Color(0x2d2e2eff), new Color(0x161717ff), new Color(0xff87e9ff));
        this.level.setupLevel();

        this.hero = new Hero(this, heroFrames, this.level.getHeroSpawn());

        this.goblins = new ArrayList<Enemy>();
        for (int i = 0; i < levelDef.goblins; ++i) {
            this.goblins.add(new Goblin(this, goblinFrames, this.level.getEnemySpawn(), 5, 2));
        }
        this.decals = new ArrayList<Decal>();

        TextureRegion arrow = TextureRegion.split(new Texture("arrow.png"), 8, 16)[0][0];

        for (int i = 0; i < levelDef.mages; ++i) {
            this.goblins.add(new GoblinMage(this, goblinMageFrames, arrow, this.level.getEnemySpawn(), 10, 5));
        }

        // save level info for restart
        this.levelDef = levelDef;

        announce("LEVEL: " + levelDef.levelName, 5.0f);
    }

    private final LevelDef levelDef;

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

    public TextureRegion[] goblinFrames;
    public TextureRegion[] goblinMageFrames;
    public TextureRegion[] heroFrames;

    public Sound bounce;
    public Sound catchBullet;
    public Sound damage;
    public Sound shootArrow;
    public Sound die;

    public Level level;
    public Hero hero;
    public ArrayList<Enemy> goblins;
    public ArrayList<Decal> decals;

    private boolean hitEdge = true;

    private SpriteBatch uiSpriteBatch;
    private TextureRegion[] healthFrames;
    private BitmapFont font;

    private String _announceMessage = null;
    private float _messageUpTime;
    private float _messageUpTimeElapsed;

    public void announce(String message, float time) {
        _announceMessage = message;
        _messageUpTime = time;
    }

    @Override
    public void render(float delta) {
        GL11 gl = Gdx.gl11;
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);


        // update Hero & AI
        this.hero.update(delta);
        for (int i = 0; i < goblins.size(); ) {
            Enemy enemy = goblins.get(i);
            if (enemy.isDead()) {
                goblins.remove(i);
                continue;
            }
            enemy.update(delta);
            ++i;
        }

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
        this.camera.update();

        // garbage collect physics bodies
        while (toDestroy.size() > 0) {
            Body body = toDestroy.pop();
            if (!body.isActive()) continue;
            this.physics.destroyBody(body);
        }

        // update physics
        this.physics.step(delta, 10, 10);

        spriteBatch.setProjectionMatrix(this.camera.combined);
        spriteBatch.begin();
        spriteBatch.setColor(1, 1, 1, 1);

        // draw level
        this.level.draw(spriteBatch);

        // draw decals
        for (Decal decal : decals) {
            decal.draw(spriteBatch);
        }

        // draw hero
        this.hero.draw(delta, spriteBatch);

        // draw enemies
        for (Enemy goblin : goblins) {
            goblin.draw(delta, spriteBatch);
        }

        spriteBatch.end();

        uiSpriteBatch.begin();
        // draw health indicator
        int healthFrame = (int) Math.max(Math.ceil(this.hero.health * 15.0 / 100.0), 0);
        uiSpriteBatch.draw(this.healthFrames[healthFrame], 10, 10, 220, 40);
        font.draw(uiSpriteBatch, "Health: " + this.hero.health + "%", 15, 35);

        // draw enemy counter
        uiSpriteBatch.draw(goblinFrames[0], 250, 0, 64, 128);
        font.draw(uiSpriteBatch, "" + this.goblins.size(), 270, 15);

        // draw announce message
        if (_announceMessage != null && _messageUpTimeElapsed < _messageUpTime) {
            _messageUpTimeElapsed += delta;

            font.draw(uiSpriteBatch, _announceMessage, _announceMessage.length() * 5, 300);

            if (_messageUpTimeElapsed >= _messageUpTime) {
                _announceMessage = null;
                _messageUpTimeElapsed = 0;
            }
        }

        if (this.goblins.size() == 0 && !this.hero.hasBullet) {
            font.draw(uiSpriteBatch, "Catch your orb to continue to the next level.", 400, 400);
        }
        uiSpriteBatch.end();

//        this.debug.render(this.physics, this.camera.combined.scl(this.pixelsInMeter));

        if (this.hero.isDead()) {
            restart();
        }

        if (this.goblins.size() == 0 && this.hero.hasBullet) {
            this.game.nextLevel();
        }
    }

    public void restart() {
        this.game.setLevel(this.levelDef);
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
        this.goblinFrames[0].getTexture().dispose();
        this.healthFrames[0].getTexture().dispose();
        this.heroFrames[0].getTexture().dispose();
        this.bounce.dispose();
        this.catchBullet.dispose();
        this.damage.dispose();
        this.shootArrow.dispose();
        this.die.dispose();
        this.level.dispose();
    }
}
