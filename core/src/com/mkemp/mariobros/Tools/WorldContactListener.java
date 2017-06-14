package com.mkemp.mariobros.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mkemp.mariobros.MarioBros;
import com.mkemp.mariobros.Sprites.Enemies.Enemy;
import com.mkemp.mariobros.Sprites.Items.Item;
import com.mkemp.mariobros.Sprites.Mario;
import com.mkemp.mariobros.Sprites.TileObjects.InteractiveTileObject;

/**
 * Created by kempm on 5/29/2017.
 */

/**
 * A contact listener is what gets called when two fixtures in
 * Box2d collide with each other.
 */
public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Get the | between these...
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // If this is a collision with Mario's head...
        if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = (fixA.getUserData() == "head") ? fixA : fixB;
            Fixture object = (head == fixA) ? fixB : fixA;

            // If this object is an interactive tile object...
            if (object.getUserData() instanceof InteractiveTileObject) {

                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }

        switch (cDef) {

            // If mario collides with the enemy's head bit...
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else
                    ((Enemy)fixB.getUserData()).hitOnHead();
                break;

            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case MarioBros.ENEMY_BIT | MarioBros.MARIO_BIT:
                Gdx.app.log("Mario", "Died");
//                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
//                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
//                else
//                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                break;


        }
    }

    @Override
    public void endContact(Contact contact) {



    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

        // Once something has collided you can change the characteristics
        // of that collision.
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

        // Gives the results of what happened due to that collision:
        // what angles the fixtures will now move in, etc.
    }
}
