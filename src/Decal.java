import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Decal {
    public Decal(TextureRegion texture, float x, float y, float w, float h) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    TextureRegion texture;
    float x;
    float y;
    float w;
    float h;

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, x, y, w, h);
    }
}
