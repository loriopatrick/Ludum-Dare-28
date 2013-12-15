import com.badlogic.gdx.physics.box2d.*;

public class GameCollision implements ContactListener {
    public GameCollision(MainGame game) {
        this.game = game;
    }

    private final MainGame game;

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

                this.game.bounce.play();

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
                enemy.onHero((Hero) other);
            }

            return;
        }


        Arrow arrow = null;
        if (a.getUserData() instanceof Arrow) {
            arrow = (Arrow)a.getUserData();
            other = b.getUserData();
        } else if (b.getUserData() instanceof Arrow) {
            arrow = (Arrow)b.getUserData();
            other = a.getUserData();
        }

        if (arrow != null) {
            if (other != null && other instanceof Hero) {
                Hero hero = (Hero)other;
                hero.applyDamage(arrow.damage);
            }

            arrow.destroy();
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA() == null) return;
        Body a = contact.getFixtureA().getBody();
        if (contact.getFixtureB() == null) return;
        Body b = contact.getFixtureB().getBody();

        Enemy enemy = null;
        Object other = null;

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
                enemy.offHero((Hero) other);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
