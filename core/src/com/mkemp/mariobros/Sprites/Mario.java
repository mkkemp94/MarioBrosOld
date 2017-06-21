package com.mkemp.mariobros.Sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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

    // Holds all the states Mario can be in.
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING };

    // Current and previous states.
    public State currentState;
    public State previousState;

    // Keep track of the amount of time in a given state.
    private float stateTimer;

    // The world mario will live in.
    public World world;

    // Box2D body.
    public Body b2body;

    private AssetManager manager;

    // Mario standing, jumping, and running.
    private TextureRegion littleMarioStand;
    private TextureRegion littleMarioJump;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;

    private Animation<TextureRegion> growMario;
    private Animation<TextureRegion> littleMarioRun;
    private Animation<TextureRegion> bigMarioRun;

    // Which direction Mario is running, is he big, and is he growing.
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;

    // Constructor takes in a world.
    public Mario(PlayScreen screen, AssetManager manager) {

        // This world is the world.
        this.world = screen.getWorld();
        this.manager = manager;

        // Start off standing.
        currentState = State.STANDING;
        previousState = State.STANDING;

        // We've been in this state for 0 seconds.
        stateTimer = 0;

        // Start off facing right.
        runningRight = true;

        // Create texture regions out of snaps taken from the png file.
        littleMarioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        littleMarioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        // Create the running animation out of an array of texture regions.
        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Go though texture regions in the png file and add them to the frames array.
        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));

        // Create the animation with how long between frames.
        littleMarioRun = new Animation<TextureRegion>(0.1f, frames);

        // Clear the array so we can use it again.
        frames.clear();

        // Do the same for big mario.
        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        // Add a grow animation.
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<TextureRegion>(0.2f, frames);

        // This function defines the mario body.
        defineMario();

        // Set the bounds around this sprite.
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);

        // This texture region is now associated with this sprite.
        // Sprite has a draw() method.
        // So it knows how to draw itself.
        setRegion(littleMarioStand);
    }

    public void update(float dt) {

        if (marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        else
            // Set the position of the mario sprite to our body.
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        // Display the appropriate frame as the sprite's texture region.
        setRegion(getFrame(dt));

        if (timeToDefineBigMario)
            defineBigMario();
        if (timeToRedefineMario)
            redefineMario();

    }

    public TextureRegion getFrame(float dt) {

        // What state is Mario in?
        currentState = getState();

        TextureRegion region;

        // Depending on the current state...
        switch (currentState) {

            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : littleMarioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true)
                        : littleMarioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : littleMarioStand;
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
        if (runGrowAnimation)
            return State.GROWING;
        else if (b2body.getLinearVelocity().y > 0
                || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;

    }

    public void grow () {
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight()*2);
        manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void hit() {
        if (marioIsBig) {
            marioIsBig = false;
            timeToRedefineMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() / 2);
            manager.get("audio/sounds/powerdown.wav", Sound.class).play();
        }
        else {
            manager.get("audio/sounds/mariodie.wav", Sound.class).play();
        }
    }

    public void redefineMario() {

        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        // Body definition.
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // The fixture definition.
        FixtureDef fdef = new FixtureDef();

        // We need a shape.
        CircleShape shape = new CircleShape();
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
        b2body.createFixture(fdef).setUserData(this);

        // Create a sensor for Mario's head.
        // An edgeshape is basically a line between two points.
        // Define that line to be above the head, a little to both sides.
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));

        // Set the head. This fixture shouldn't collide with anything in the world.
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        // Create fixture and uniquely identify this fixture as head.
        // Also set user data to itself (Mario)
        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;
    }

    public void defineBigMario() {

        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        // Body definition.
        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // The fixture definition.
        FixtureDef fdef = new FixtureDef();

        // We need a shape.
        CircleShape shape = new CircleShape();
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
        b2body.createFixture(fdef).setUserData(this);

        // Making another circle below...
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        // Create a sensor for Mario's head.
        // An edgeshape is basically a line between two points.
        // Define that line to be above the head, a little to both sides.
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));

        // Set the head. This fixture shouldn't collide with anything in the world.
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        // Create fixture and uniquely identify this fixture as head.
        // Also set user data to itself (Mario)
        b2body.createFixture(fdef).setUserData(this);

        timeToDefineBigMario = false;
    }

    public void defineMario() {

        // Body definition.
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // The fixture definition.
        FixtureDef fdef = new FixtureDef();

        // We need a shape.
        CircleShape shape = new CircleShape();
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
        b2body.createFixture(fdef).setUserData(this);

        // Create a sensor for Mario's head.
        // An edgeshape is basically a line between two points.
        // Define that line to be above the head, a little to both sides.
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));

        // Set the head. This fixture shouldn't collide with anything in the world.
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        // Create fixture and uniquely identify this fixture as head.
        // Also set user data to itself (Mario)
        b2body.createFixture(fdef).setUserData(this);
    }

    public boolean isBig(){
        return marioIsBig;
    }
}
