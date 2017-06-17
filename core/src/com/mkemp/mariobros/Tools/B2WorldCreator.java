package com.mkemp.mariobros.Tools;

/**
 * Created by kempm on 5/28/2017.
 */

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mkemp.mariobros.MarioBros;
import com.mkemp.mariobros.Screens.PlayScreen;
import com.mkemp.mariobros.Sprites.TileObjects.Brick;
import com.mkemp.mariobros.Sprites.TileObjects.Coin;
import com.mkemp.mariobros.Sprites.Enemies.Goomba;

/**
 * This is where the Box2D objects are created.
 */
public class B2WorldCreator {

    private World world;
    private TiledMap map;
    private BodyDef bdef;
    private PolygonShape shape;
    private FixtureDef fdef;

    private Array<Goomba> goombas;

    /**
     * This constructor takes in a PlayScreen, which contains (has get() methods for)
     * the world, map, hud, and asset manager.
     * Hud and asset manager will be used in brick and coin.
     * @param screen
     */
    public B2WorldCreator(PlayScreen screen) {

        world = screen.getWorld();
        map = screen.getMap();

        // Define what the body consists of.
        bdef = new BodyDef();
        shape = new PolygonShape();
        fdef = new FixtureDef();

        // They are not InteractiveTileObjects so they need to be created fully here..
        createGroundLayer();
        createPipesLayer();

        // For each object in layer 5, make it a brick.
        // Bricks extend InteractiveTileObject, which takes care of creating the bodies.
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {

            // Get the rectangle object.
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // Create a brick with object oriented design!
            new Brick(screen, object);
        }

        // Same with coins.
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Coin(screen, object);
        }

        // Create all goombas
        goombas = new Array<Goomba>();
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX() / MarioBros.PPM, rect.getY() / MarioBros.PPM));
        }
    }

    private void createPipesLayer() {

        // Pipes layer...
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {

            // Get the rectangle object.
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // Define our body.
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            // Add this body to the Box2d world.
            Body body = world.createBody(bdef);

            // Define the polygon shape.
            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);

            // Define the fixture.
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fdef);
        }
    }

    private void createGroundLayer() {

        // Create a body and fixture at every corresponding object in our tiled map layers.
        // Start with the ground layer...
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {

            // Get the rectangle object.
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // Define our body.
            // Dynamic body: is effected by forces like gravity and velocity eg. the player
            // Static body: these don't move and aren't effected by forces.
            // Kinetic bodies: aren't affected by forces like gravity but can be effected by velocity.
            bdef.type = BodyDef.BodyType.StaticBody;

            // Set the position. (to the center of the rectangle?)
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            // Add this body to the Box2d world.
            Body body = world.createBody(bdef);

            // For the fixture, define the polygon shape. Position is centered,
            // and the rest of the box is around that point.
            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);

            // Make fixture and add it to the body.
            fdef.shape = shape;
            body.createFixture(fdef);
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
}
