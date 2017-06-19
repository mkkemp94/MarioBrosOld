package com.mkemp.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mkemp.mariobros.MarioBros;
import com.mkemp.mariobros.Scenes.Hud;
import com.mkemp.mariobros.Sprites.Enemies.Enemy;
import com.mkemp.mariobros.Sprites.Items.Item;
import com.mkemp.mariobros.Sprites.Items.ItemDef;
import com.mkemp.mariobros.Sprites.Items.Mushroom;
import com.mkemp.mariobros.Sprites.Mario;
import com.mkemp.mariobros.Tools.B2WorldCreator;
import com.mkemp.mariobros.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kempm on 5/21/2017.
 */

public class PlayScreen implements Screen {

    private MarioBros game;
    private TextureAtlas atlas;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;


    // These are the tiled map variables...
    // mapLoader loads the map into the game and renderer renders it.
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // These are the Box2d variables...
    // b2dr gives us a graphical representation of the fixtures and bodies in our Box2d world.
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    // Sprites
    private Mario player;
    //private Goomba goomba;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    private AssetManager manager;
    private Music music;

    public PlayScreen(MarioBros game) {

        // The game is being passed around so that different screens can create other screens.
        this.game = game;

        // Manage assets, like music.
        manager = game.getManager();
        this.manager = manager;
        music = manager.get("audio/music/super_mario_world_remix.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();

        // Load the map.
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("../../../Tiled/My Tiles/custom_level.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);

        // Create a world: give it gravity and allow objects at rest to sleep.
        // Box2d does not calculate objects at rest during its calculations.
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new WorldContactListener());

        // This camera follows along in the game world and displays in the viewport.
        gameCam = new OrthographicCamera();

        // The game port can be scaled in multiple different ways.
        //gamePort = new ScreenViewport(gameCam);
        //gamePort = new StretchViewport(800, 480, gameCam);
        //gamePort = new FillViewport(800, 480, gameCam);
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gameCam);

        // Camera defaults to center at 0, 0. Change this to the center of the view port.
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        // Refers to where all the sprites for the characters are.
        // If loading a lot of resources, Libgdx's asset manager would be better to use here.
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        // Create a new hud. Pass it the sprite batch.
        hud = new Hud(game.batch);

        // Make this too...
        b2dr = new Box2DDebugRenderer();

        // Add bodies and fixtures to the game world, passing this screen as a constructor.
        // This contains enemies.
        creator = new B2WorldCreator(this);

        // Characters
        player = new Mario(this, manager);

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {

        if (Gdx.input.isTouched()) {
            gameCam.position.x += 100 * dt;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {

            // Use impulse for jump - immediate change.
            // Apply it to the center of the body. Also wake with the impulse.
            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {

            // Move, but not too fast.
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {

            // Move, but not too fast.
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    public void update(float dt) {

        handleInput(dt);
        handleSpawningItems();

        // For box2d to execute our physics simulation,
        // we need to tell it how many times to calculate per second -- 60.
        world.step(1/60f, 6, 2);

        // Update the character and enemy bodies, and the hud, based on how much time has passed.
        player.update(dt);

        for (Enemy enemy : creator.getGoombas()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 224 / MarioBros.PPM)
                enemy.b2body.setActive(true);
        }
        for (Item item : items) {
            item.update(dt);
        }

        hud.update(dt);

        // Update game cam. Only track x so the camera doesn't jump up and down.
        gameCam.position.x = player.b2body.getPosition().x;

        // Update camera with each update.
        gameCam.update();

        // Let the map renderer know what it needs to render.
        // Only render what the game cam can see.
        renderer.setView(gameCam);

    }

    @Override
    public void render(float delta) {

        // The render method is the only thing that's getting called over and over...
        // First do calculations to update everything.
        update(delta);

        // Clear the screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw what the camera can see.
        renderer.render();

        // Render our Box2DDebugLines.
        b2dr.render(world, gameCam.combined);

        // Render characters to the screen.
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);

        for (Enemy enemy : creator.getGoombas()) {
            enemy.draw(game.batch);
        }
        for (Item item : items) {
            item.draw(game.batch);
        }

        game.batch.end();

        // Set what will be shown via our camera, and draw it.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    /**
     * When we resize the screen, adjust the viewport here.
     * @param width of screen
     * @param height of screen
     */
    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public Hud getHud() {
        return hud;
    }

    public AssetManager getManager() {
        return manager;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
