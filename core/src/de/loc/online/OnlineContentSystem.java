package de.loc.online;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.game.GameScreen;
import de.loc.input.ClickableComponent;
import de.loc.input.userinterface.LandsOfCinderWindow;
import de.loc.input.userinterface.UserInterface;
import de.loc.tools.Constants;
import de.loc.tools.XmlHelper;

public class OnlineContentSystem extends LandsOfCinderSystem {
    private final GameScreen gameScreen;
    private final UserInterface ui;

    private LandsOfCinderWindow requestWindow;
    private final NinePatchDrawable windowBackground;

    private final ComponentMapper<ClickableComponent> clickableMapper;
    private ComponentMapper<OnlineContentComponent> onlineMapper;

    private ImmutableArray<Entity> entities;

    /* Window Breite und Höhe */
    private static final float WINDOW_WIDTH = Constants.WIDTH / 2.0f;
    private static final float WINDOW_HEIGHT = Constants.HEIGHT / 5.0f;

    public OnlineContentSystem(GameScreen gameScreen, UserInterface ui) {
        this.gameScreen = gameScreen;
        this.ui = ui;

        this.windowBackground = new NinePatchDrawable(ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT));
        this.clickableMapper = ComponentMapper.getFor(ClickableComponent.class);
    }

    public void addedToEngine(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(ClickableComponent.class, OnlineContentComponent.class).get());
    }

    private void createRequestWindow(final String packageName, final OnlineStatus status) {
        this.requestWindow = new LandsOfCinderWindow(this.ui.getSkin());

        Table requestTable = new Table();
        Table requestContentTable = new Table();
        Table buttonContainerTable = new Table();

        Label labelRequest = new Label("Möchten Sie diese Community-Welt spielen?", this.ui.getSkin());
        Label labelPackageName = new Label(packageName, this.ui.getSkin());

        TextButton button1 = new TextButton("Annehmen", this.ui.getSkin());
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                status.packageId = packageName;

                OnlineHelper.sendHttpGetPackage(status);
                while ( !status.downloaded && !status.cancelled && !status.failed ) {
                    Gdx.app.log("ONLINE", "Warten!");
                }
                String startLevel = XmlHelper.parseStartLevel(Constants.PACKAGE_FOLDER + packageName);
                LevelManager.getInstance().setCurrentPackage(packageName);
                LevelManager.getInstance().setCurrentLevel(startLevel);

                OnlineContentSystem.this.gameScreen.scheduleLoad(Constants.PACKAGE_FOLDER + packageName + "/" + Constants.LEVELS_PATH + startLevel);

                OnlineContentSystem.this.hideRequestWindow();
            }
        });

        TextButton button2 = new TextButton("Ablehnen", this.ui.getSkin());
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                OnlineContentSystem.this.hideRequestWindow();
            }
        });

        requestContentTable.add(labelRequest).row();
        requestContentTable.add(labelPackageName).row();
        buttonContainerTable.add(button1);
        buttonContainerTable.add(button2);
        requestTable.add(requestContentTable).row();
        requestTable.add(buttonContainerTable);
        this.requestWindow.add(requestTable);
        this.ui.add(this.requestWindow);

        this.requestWindow.setBackground(this.windowBackground);
        this.requestWindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.requestWindow.setPosition(Constants.WIDTH / 2.0f - WINDOW_WIDTH / 2.0f, Constants.HEIGHT / 2.0f - WINDOW_HEIGHT / 2.0f);
    }

    /* Lässt das aktive Anfragefenster verschwinden */
    public void hideRequestWindow() {
        if ( this.requestWindow != null ) {
            this.requestWindow.remove();
            this.requestWindow.clearChildren();
        }
    }

    @Override
    public void update(float deltaTime) {
        for ( Entity entity : this.entities ) {
            ClickableComponent c = this.clickableMapper.get(entity);
            if ( c.clicked ) {
                Gdx.app.log("ONLINE", "Okey, bald gibt es Online-Content für dich!");

                OnlineStatus status = new OnlineStatus();

                if ( OnlineHelper.waitForHttpPostLogin(status) ) {
                    if ( OnlineHelper.waitForHttpGetPackages(status) ) {
                        String packageName = status.getPackageIds().first();
                        this.createRequestWindow(packageName, status);
                    }
                }
            }
        }
    }

    @Override
    public void reset() {

    }
}
