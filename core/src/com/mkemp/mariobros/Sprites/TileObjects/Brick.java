package com.mkemp.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.mkemp.mariobros.MarioBros;
import com.mkemp.mariobros.Scenes.Hud;
import com.mkemp.mariobros.Screens.PlayScreen;
import com.mkemp.mariobros.Sprites.Mario;

/**
 * Created by kempm on 5/28/2017.
 */

public class Brick extends InteractiveTileObject {

    Hud hud;
    AssetManager manager;

    public Brick(PlayScreen screen, MapObject object) { //}, Hud hud, AssetManager manager) {
        super(screen, object);

        // Set the fixture tio be a brick.
        fixture.setUserData(this);

        // This is a brick filter.
        setCategoryFilter(MarioBros.BRICK_BIT);

        this.hud = screen.getHud();
        this.manager = screen.getManager();
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Brick", "Collision");

        if (mario.isBig()) {

            // Set it to the destroyed category.
            setCategoryFilter(MarioBros.DESTROYED_BIT);

            // Remove the texture for this cell. I think the object still exists.
            getCell().setTile(null);

            // Add to score when we hit a brick.
            hud.addScore(200);

            manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        else {
            manager.get("audio/sounds/bump.wav", Sound.class).play();
        }

    }
}
