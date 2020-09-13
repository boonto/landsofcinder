package de.loc.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.input.userinterface.UserInterface;
import de.loc.tools.Constants;

//TODO vorerst ein EntitySystem, muss es eigentlich nicht sein updateable sollte reichen
public class FeedbackSystem extends LandsOfCinderSystem implements EventListener {

    private final Label message;
    private float alpha;
    private float timer;
    private final float fadeInTime;
    private final float stayTime;
    private final float fadeOutTime;

    public FeedbackSystem(UserInterface ui) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(ui.getFont(), Color.BLACK);

        labelStyle.background = new NinePatchDrawable(ui.getNinePatch(Constants.UI_NINEPATCHES_PATH + "papier.png"));

        this.message = new Label("", labelStyle);
        this.message.setSize(500.0f, 100.0f);

        this.message.setPosition(Constants.WIDTH / 2.0f - 250.0f, (Constants.HEIGHT / 5.0f) * 4.0f - 40.0f);
        this.message.setAlignment(Align.center);

        this.message.setColor(this.message.getColor().r, this.message.getColor().g, this.message.getColor().b, 0.0f);

        this.fadeInTime = 1.0f;
        this.stayTime = 2.0f;
        this.fadeOutTime = 1.0f;
        this.timer = this.fadeInTime + this.stayTime + this.fadeOutTime;
        this.message.setTouchable(Touchable.disabled);
        ui.add(this.message);

        EventSystem.getInstance().addListener(this, EventSystem.EventType.QUEST_EVENT, EventSystem.EventType.FETCH_EVENT, EventSystem.EventType.LEVELUP_EVENT);
    }

    public void addedToEngine(Engine engine) {

    }

    @Override
    public void update(float deltaTime) {
        this.timer += deltaTime;

        if ( this.timer >= 0.0f && this.timer <= this.fadeInTime ) {
            this.alpha = this.timer;
        } else if ( this.timer >= this.fadeInTime && this.timer <= (this.fadeInTime + this.stayTime) ) {
            this.alpha = 1.0f;
        } else if ( this.timer >= (this.fadeInTime + this.stayTime) && this.timer <= (this.fadeInTime + this.stayTime + this.fadeOutTime) ) {
            this.alpha = 1.0f - (this.timer - (this.fadeInTime + this.stayTime));
        } else {
            this.alpha = 0.0f;
        }

        this.message.setColor(this.message.getColor().r, this.message.getColor().g, this.message.getColor().b, this.alpha);
    }

    @Override
    public void update(Event e) {
        this.displayMessage(e);
    }

    private void displayMessage(Event e) {
        if ( e.eventType == EventSystem.EventType.QUEST_EVENT ) {
            this.message.setText("Tagebucheintrag aktualisiert");
        } else if ( e.eventType == EventSystem.EventType.FETCH_EVENT ) {
            this.message.setText(e.args[0] + " erhalten");
        } else if ( e.eventType == EventSystem.EventType.LEVELUP_EVENT ) {
            this.message.setText("Dein Level ist gestiegen!");
        }

        this.timer = 0.0f;
    }

    @Override
    public void reset() {

    }
}
