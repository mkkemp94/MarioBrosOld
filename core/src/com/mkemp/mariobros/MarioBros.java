package com.mkemp.mariobros;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mkemp.mariobros.Screens.PlayScreen;

// This used to extend ApplicationAdapter. Now it extends Game, which allows for there to be one
// or many screens.
public class MarioBros extends Game {

	// These are the virtual width and height for the game.
	public static final int V_WIDTH = 480;
	public static final int V_HEIGHT = 208;

	/** Pixels per meter. */
	public static final float PPM = 100;

	// Filters. Every fixture has one. There are two parts:
	// Category - what is this fixture? Mario? Brick? Coin?
	// Mask - what can this fixture collide with?
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;

	// Container holding images and textures, etc.
	public SpriteBatch batch;

	/**
	 * WARNING: Using asset manager in a static way can cause issues,
	 * especially with android.  Making the asset manager static is
	 * a bad idea. The tutorial does it to save time, but I won't.
	 */
	public AssetManager manager;

	@Override
	public void create () {

		// Create the sprite batch. Remember to do this only once!
		batch = new SpriteBatch();

		manager = new AssetManager();
		manager.load("audio/music/super_mario_world_remix.ogg", Music.class);
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/breakblock.wav", Sound.class);
		manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
		manager.load("audio/sounds/powerup.wav", Sound.class);
		manager.load("audio/sounds/powerdown.wav", Sound.class);
		manager.load("audio/sounds/stomp.wav", Sound.class);
		manager.load("audio/sounds/mariodie.wav", Sound.class);

		// ... but we're gonna do a synchronous load here instead.
		// It blocks everything until the assets are finished loading.
		manager.finishLoading();

		// Set the screen to a new play screen.
		// Pass it this game so that it can create screens itself.
		setScreen(new PlayScreen(this));
	}

	public AssetManager getManager() {
		return manager;
	}

	@Override
	public void render () {

		// Delegates the render method to whatever screen is active.
		super.render();

		// Call the manager to continuously load the assets.
		// This returns true or false - are all assets loaded?
		// This is how it should be done. It is asynchronous.
//		if (manager.update()) {
//
//		}
	}
	
	@Override
	public void dispose () {
		super.dispose();

		manager.dispose();
		batch.dispose();
	}
}
