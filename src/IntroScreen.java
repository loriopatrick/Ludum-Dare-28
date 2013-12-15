import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IntroScreen implements Screen {
    public IntroScreen (LudGame game) {
        this.game = game;
        splash = new Texture("assets/splash.png");
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.zoom = 0.1f;
    }

    final LudGame game;
    Texture splash;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;

    public float elapsedTime = 0.0f;

    @Override
    public void render(float v) {
        elapsedTime += v;

        if (elapsedTime > 2.0f || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            this.game.nextLevel();
        }

        camera.update();
        spriteBatch.setProjectionMatrix(this.camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(splash, -splash.getWidth() / 2, -splash.getHeight() / 2);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
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
