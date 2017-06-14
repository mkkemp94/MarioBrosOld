package com.mkemp.mariobros.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mkemp.mariobros.MarioBros;

/**
 * Created by kempm on 5/21/2017.
 */

public class Hud implements Disposable {

    // A stage contains hierarchies of actors.
    // It handles the viewport and distributes input events.
    // This is an empty box that holds widgets.
    // A table should be used, else the widgets would fall and have no organization.
    public Stage stage;

    // This viewport is specific to our HUD. It does not move with the world's screen.
    private Viewport viewport;

    // Timer for the world.
    private Integer worldTimer;

    // How much time has passed.
    private float timeCount;

    // The player's score. This was static. For better programming practice I
    // have the classes that use this (ie brick) use a hud object.
    // To do that, I had to pass the hud object from the play screen to its constructor.
    private Integer score;

    // Scene2d calls these widgets "Label"s
    private Label countdownLabel;

    // This was static.
    private Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label marioLabel;

    public Hud(SpriteBatch sb) {

        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_WIDTH, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();

        // Align at the top of the stage, rather than the center.
        table.top();

        // Fill the stage.
        table.setFillParent(true);

        // Countdown is 3 digits long. Score is 6, etc...
        countdownLabel = new Label(String.format("%03d", worldTimer),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        scoreLabel = new Label(String.format("%06d", score),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        marioLabel = new Label("MARIO", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // Add these labels to the table.
        // Having multiple lables on the same row expanding X divides their spacing equally.
        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);

        // Create new row.
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        // Add the table to our stage.
        stage.addActor(table);
    }

    public void update(float dt) {

        timeCount += dt;
        if (timeCount >= 1) {

            // A second has gone by. Update the screen.
            worldTimer--;
            countdownLabel.setText(String.format("%03d", worldTimer));

            // Reset time count. It only keeps track of single seconds.
            timeCount = 0;
        }
    }

    /**
     * Making this static is a bad programming practice.
     * @param value
     */
    public void addScore(int value) {

        // Update score and display it.
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {

        stage.dispose();
    }
}
