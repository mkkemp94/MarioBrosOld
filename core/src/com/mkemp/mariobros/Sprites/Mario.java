package com.mkemp.mariobros.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mkemp.mariobros.MarioBros;
import com.mkemp.mariobros.Screens.PlayScreen;

/**
 * Created by kempm on 5/24/2017.
 */

public class Mario extends Sprite {

    private String yooooo;

    // Holds all the states Mario can be in.
    public enum State { FALLING, JUMPING, STANDING, RUNNING };

    // Current and previous states.
    public State currentState, previousState;

    // The world mario will live in.
    public World world;

    // Box2D body.
    public Body b2body;

    // Mario standing.
    private TextureRegion marioStand;

    // Keep track of the amount of time in a given state.
    private float stateTimer;

    // Which direction Mario is running.
    private boolean runningRight;

    // Run and jump animations.
    private Animation<TextureRegion> marioRun, marioJump;

    // Constructor takes in a world.
    public Mario(PlayScreen screen) {

        // Pass in a texture region to manipulate in the code below.
        super(screen.getAtlas().findRegion("little_mario"));

        // This world is the world.
        this.world = screen.getWorld();

        // Start off standing.
        currentState = State.STANDING;
        previousState = State.STANDING;

        // We've been in this state for 0 seconds.
        stateTimer = 0;

        // Start off facing right.
        runningRight = true;

        // Run animation! An array of texture regions will be passed.
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
        }
        marioRun = new Animation<TextureRegion>(0.1f, frames);

        // We'll be reusing frames for our jump animation.
        frames.clear();

        // Jump animation.
        for (int i = 4; i < 6; i++) {
            frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
        }
        marioJump = new Animation<TextureRegion>(0.1f, frames);

        // Get the texture passed in the super class above.
        marioStand = new TextureRegion(getTexture(), 0, 0, 16, 16);

        // This function defines the mario body.
        defineMario();

        // Set the bounds around this sprite.
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);

        // This texture region is now associated with this sprite.
        // Sprite has a draw() method.
        // So it knows how to draw itself.
        setRegion(marioStand);
    }

    public void update(float dt) {

        // Set the position of the mario sprite to our body.
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        // Display the appropriate frame as the sprite's texture region.
        setRegion(getFrame(dt));

    }

    public TextureRegion getFrame(float dt) {

        // What state is Mario in?
        currentState = getState();

        TextureRegion region;

        // Depending on the current state...
        switch (currentState) {

            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;

            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;

            case FALLING:
            case STANDING:
            default:
                region = marioStand;
                break;
        }

        // If we're moving to the left and mario isn't facing left...
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            // Flip him to face left. x true and y false.
            region.flip(true, false);
            runningRight = false;
        }

        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        // Reset timer if needed.
        stateTimer = (currentState == previousState) ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {

        // What is our b2body doing currently?
        if (b2body.getLinearVelocity().y > 0 ||
                (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {

            // In this case, he's jumping.
            return State.JUMPING;
        }

        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;

    }

    public void defineMario() {

        // Body definition.
        BodyDef bdef = new BodyDef();

        // Position the body.
        bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);

        // Type of body.
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
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;

        // What can he collide with?
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT
        ;

        // Assign this shape to fdef.
        fdef.shape = shape;

        // Create the fixture for the body.
        b2body.createFixture(fdef);

        // Create a sensor for Mario's head.
        EdgeShape head = new EdgeShape();

        // An edgeshape is basically a line between two points.
        // Define that line to be above the head, a little to both sides.
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));

        // Set the head.
        fdef.shape = head;

        // This fdef shouldn't collide with anything in the world.
        fdef.isSensor = true;

        // Create fixture and uniquely identify this fixture as head.
        b2body.createFixture(fdef).setUserData("head");
    }
}
