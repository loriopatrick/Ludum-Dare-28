import com.badlogic.gdx.physics.box2d.*;

public class GameCollision implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();

        Object other = null;

        TheBullet bullet = null;
        if (a.getUserData() instanceof TheBullet) {
            bullet = (TheBullet) a.getUserData();
            other = b.getUserData();
        } else if (b.getUserData() instanceof TheBullet) {
            bullet = (TheBullet) b.getUserData();
            other = a.getUserData();
        }

        if (bullet != null) {
            if (other == null) return;

            if (other instanceof Hero && bullet.isHeroCollideAllowed()) {
                Hero hero = (Hero) other;
                hero.catchBullet(bullet);
            } else {
                bullet.allowHeroCollide(); // allow hero to catch the bullet

                if (other instanceof Enemy) {
                    Enemy enemy = (Enemy) other;
                    enemy.hitByTheBullet(bullet);
                }
            }

            return;
        }

        Enemy enemy = null;
        if (a.getUserData() instanceof Enemy) {
            enemy = (Enemy)a.getUserData();
            other = b.getUserData();
        } else if (b.getUserData() instanceof Enemy) {
            enemy = (Enemy)b.getUserData();
            other = a.getUserData();
        }

        if (enemy != null) {
            if (other == null) return;

            if (other instanceof Hero) {
                enemy.touchHero((Hero)other);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
