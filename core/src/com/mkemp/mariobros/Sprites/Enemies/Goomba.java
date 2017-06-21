package com.mkemp.mariobros.Sprites.Enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mkemp.mariobros.MarioBros;
import com.mkemp.mariobros.Screens.PlayScreen;

/**
 * Created by kempm on 6/4/2017.
 */

public class Goomba extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    private AssetManager manager;

    public Goomba(PlayScreen screen, float x, float y, AssetManager manager) {
        super(screen, x, y);
        this.manager = manager;
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        }
        walkAnimation = new Animation<TextureRegion>(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        stateTime += dt;

        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        }
        else if (!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }

    }

    @Override
    protected void defineEnemy() {

        // Body definition.
        BodyDef bdef = new BodyDef();

        // Make sure the goomba is at the position defined in the constructor.
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;

        // Create the newly defined box2d body.
        b2body = world.createBody(bdef);

        // The fixture definition.
        FixtureDef fdef = new FixtureDef();

        // We need a shape.
        CircleShape shape = new CircleShape();

        // Set the radius.
        shape.setRadius(6 / MarioBros.PPM);

        // Set a filter.
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;

        // What can he collide with?
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.MARIO_BIT
        ;

        // Assign this shape to fdef.
        fdef.shape = shape;

        // Create the fixture for the body and set the user data.
        b2body.createFixture(fdef).setUserData(this);

        // Create the head here:
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-5, 8).scl(1 / MarioBros.PPM);
        vertices[1] = new Vector2(5, 8).scl(1 / MarioBros.PPM);
        vertices[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
        vertices[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
        head.set(vertices);

        fdef.shape = head;

        // Bounciness
        fdef.restitution = 0.5f;

        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1) {
            super.draw(batch);
        }
    }

    @Override
    public void hitOnHead() {

        // Remove the body so there's no longer collision with mario.
        // This method is getting called inside the contact listener...
        // Which is getting called un Play Screen's update()
        setToDestroy = true;

        manager.get("audio/sounds/stomp.wav", Sound.class).play();


    }
}
