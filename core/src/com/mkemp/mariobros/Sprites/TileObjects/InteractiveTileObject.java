package com.mkemp.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mkemp.mariobros.MarioBros;
import com.mkemp.mariobros.Screens.PlayScreen;

/**
 * Created by kempm on 5/28/2017.
 */

public abstract class InteractiveTileObject {

    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected PlayScreen screen;

    public InteractiveTileObject(PlayScreen screen, Rectangle bounds) {

        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // Define our body.
        bdef.type = BodyDef.BodyType.StaticBody;

        // Set the position.
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM, (bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM);

        // Add this body to the Box2d world.
        body = world.createBody(bdef);

        // For the fixture, define the polygon shape.
        shape.setAsBox(bounds.getWidth() / 2 / MarioBros.PPM, bounds.getHeight() / 2 / MarioBros.PPM);

        // Make fixture and add it to the body.
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
    }

    // Classes that implement this should include this method.
    public abstract void onHeadHit();

    public void setCategoryFilter(short filterBit) {

        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell() {

        // Get the Tiled Graphics Layer
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        // Get the tile at this position and scale it up back to the size that Tiled works with.
        return layer.getCell((int)(body.getPosition().x * MarioBros.PPM / 16),
                (int) (body.getPosition().y * MarioBros.PPM / 16));
    }
}
