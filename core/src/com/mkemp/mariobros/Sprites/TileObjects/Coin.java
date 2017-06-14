package com.mkemp.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mkemp.mariobros.MarioBros;
import com.mkemp.mariobros.Scenes.Hud;
import com.mkemp.mariobros.Screens.PlayScreen;
import com.mkemp.mariobros.Sprites.Items.ItemDef;
import com.mkemp.mariobros.Sprites.Items.Mushroom;

/**
 * Created by kempm on 5/28/2017.
 */

public class Coin extends InteractiveTileObject {

    // I'm not making this static...
    private TiledMapTileSet tileSet;

    Hud hud;
    AssetManager manager;

    // In tiled, the coin is at index 27.
    // We start at index 1 here.
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, Rectangle bounds) { //}, Hud hud, AssetManager manager) {
        super(screen, bounds);

        // Get the tileset tileset_gutter from Tiled
        tileSet = map.getTileSets().getTileSet("tileset_gutter");

        // Set the fixture to be a coin.
        fixture.setUserData(this);

        // This should be of the coin category.
        setCategoryFilter(MarioBros.COIN_BIT);

        this.hud = screen.getHud();
        this.manager = screen.getManager();
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Coin", "Collision");

        if (getCell().getTile().getId() == BLANK_COIN)
            manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            manager.get("audio/sounds/coin.wav", Sound.class).play();
            screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x,
                    body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
        }

        // Change the tile image and add to the score.
        getCell().setTile(tileSet.getTile(BLANK_COIN));

        hud.addScore(100);


    }
}
