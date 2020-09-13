package de.loc.input.userinterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.loc.editor.EditorMenu;
import de.loc.editor.Quickbar;
import de.loc.input.InputHandler;
import de.loc.tools.Constants;

public class UserInterface {

    //Disposen!
    private final Stage stage;
    private Skin skin;
    private TextureAtlas textureAtlas;
    private final AssetManager assetManager;
    private BitmapFont bitmapFont; //wird von skin.dispose() disposed

    private final Table mainTable;
    private final InputHandler inputHandler;

    public UserInterface(InputHandler inputHandler) {
        this.inputHandler = inputHandler;

        this.assetManager = new AssetManager();
        this.stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        this.stage.addActor(this.mainTable);
        this.setScreenSize(25, Color.WHITE);

        this.inputHandler.addProcessor(this.stage);
    }

    public void setScreenSize(int size, Color color) {
        // Unterschiedliche Schriftgröße bei den Bildschirmgrößen
        int screenWidth = Gdx.graphics.getWidth();
        if ( screenWidth <= 960 ) {
            this.skin = this.setSkinFont(Constants.UI_FONTS_PATH + "bebasUI.ttf", size, color);
        } else {
            this.skin = this.setSkinFont(Constants.UI_FONTS_PATH + "bebasUI.ttf", size, color); //LinLibertine_RZI.ttf
        }
    }

    private Skin setSkinFont(String path, int fontSize, Color color) {
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        //FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //parameter.color = color;
        //parameter.size = fontSize;

        this.bitmapFont = new BitmapFont();
        //bitmapFont = generator.generateFont(parameter); // font size 12 pixels
        //generator.dispose();
        //bitmapFont = new BitmapFont();
        this.bitmapFont.setColor(Color.BLACK);
        Skin skin = new Skin();
        skin.add("default-font", this.bitmapFont);

        this.textureAtlas = this.getAsset(Constants.UI_DATA_PATH + "uiskin.atlas", TextureAtlas.class);

        skin.addRegions(this.textureAtlas);
        skin.load(Gdx.files.internal(Constants.UI_DATA_PATH + "uiskin.json"));

        return skin;
    }

    public <T> T getAsset(String path, Class<T> type) {
        T asset;

        if ( this.assetManager.isLoaded(path) ) {
            //System.out.println(type.toString() + ": GIBTS SCHON!");
            asset = this.assetManager.get(path, type);
        } else {
            this.assetManager.load(path, type);
            while ( !this.assetManager.update() ) {
                //System.out.println(assetManager.getProgress());
            }
            asset = this.assetManager.get(path, type);
            //System.out.println(type.toString() + ": NEU!");
        }

        return asset;
    }

    public Texture getTextureWithMipmap(String path) {
        Texture asset;

        if ( this.assetManager.isLoaded(path) ) {
            //System.out.println(type.toString() + ": GIBTS SCHON!");
            asset = this.assetManager.get(path, Texture.class);
        } else {
            TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
            param.minFilter = Texture.TextureFilter.Linear;
            param.genMipMaps = true;
            this.assetManager.load(path, Texture.class, param);
            while ( !this.assetManager.update() ) {
                //System.out.println(assetManager.getProgress());
            }
            asset = this.assetManager.get(path, Texture.class);
            //System.out.println(type.toString() + ": NEU!");
        }

        return asset;
    }

    public BitmapFont getFont() {
        return this.bitmapFont;
    }

    public void add(Actor actor) {
        this.mainTable.addActor(actor);
    }

    public void remove(Actor actor) {
        this.mainTable.removeActor(actor);
    }

    public Skin getSkin() {
        return this.skin;
    }

    public Table getMainTable() {
        return this.mainTable;
    }

    public Label addLabel(String text) {
        return new Label(text, this.skin);
    }

    public TextField addTextField(String text) {
        return new TextField(text, this.skin);
    }

    public CheckBox addCheckBox(String text) {
        CheckBox checkbox = new CheckBox(text, this.skin);
        this.add(checkbox);
        return checkbox;
    }

    public LocTextArea addLocTextArea(String text) {
        return new LocTextArea(text, this.skin);
    }

    public TextArea addTextArea(String text) {
        return new TextArea(text, this.skin);
    }

    public SelectBox addSelectBox(Array<String> entries, String handleID) {
        SelectBox<String> selectBox = new SelectBox<String>(this.getSkin());
        selectBox.setName(handleID);
        selectBox.setItems(entries);

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox<String> selectBox = (SelectBox<String>) actor;
                UserInterface.this.inputHandler.handle(actor.getName() + selectBox.getSelected());
            }
        });
        this.add(selectBox);
        return selectBox;
    }

    public ProgressBar addProgress() {

        TextureRegionDrawable
            textureBar =
            new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(Constants.UI_NINEPATCHES_PATH + "blau.png"))));
        ProgressBar.ProgressBarStyle
            barStyle =
            new ProgressBar.ProgressBarStyle(this.skin.newDrawable("white", Color.BLACK), this.skin.newDrawable("white", Color.BLACK));
        barStyle.knobBefore = barStyle.knob;

        ProgressBar progress = new ProgressBar(0, 5, 0.5f, false, barStyle);
        //progress.setWidth(Constants.WIDTH);
        //progress.setHeight(Constants.HEIGHT / 2);
        //progress.setSize(100, 100);
        progress.setSize(290, 50);
        progress.setAnimateDuration(2);
        progress.setValue(1.2f);

        return progress;
    }

    public Button addButton(String path, int x, int y, String handleUI) {
        Button button = new Button(new Image(this.getAsset(path, Texture.class)).getDrawable());
        button.setName(handleUI);
        button.setWidth(Constants.UI_IMAGE_SIZE);
        button.setHeight(Constants.UI_IMAGE_SIZE);
        button.setX(x);
        button.setY(y);
        this.add(button);

        this.addClickListener(button);
        this.add(button);
        return button;
    }

    /* Zusätzliches einstellen der Width und Height */
    public Button addButton(String path, int x, int y, int width, int height, String handleUI) {
        Button button = new Button(new Image(this.getAsset(path, Texture.class)).getDrawable());
        button.setName(handleUI);
        button.setWidth(width);
        button.setHeight(height);
        button.setX(x);
        button.setY(y);
        this.add(button);

        this.addClickListener(button);
        this.add(button);
        return button;
    }

    public void addClickListener(final Actor actor) {
        if ( actor.getName() != null ) {
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    UserInterface.this.inputHandler.handle(actor.getName());
                }
            });
        } else {
            System.out.println("kein Inputhandle angegeben");
        }
    }

    public Slider addSlider(int x, int y, final String handleUI) {
        final Slider slider = new Slider(1, 50, 1, false, this.skin);
        slider.setName(handleUI);
        slider.setWidth(Constants.UI_MIDDLE_WIDTH);
        slider.setHeight(Constants.UI_IMAGE_SIZE);
        slider.setX(x);
        slider.setY(y);
        slider.setValue(10);

        this.add(slider);

        slider.addListener(new DragListener() {

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                return UserInterface.this.inputHandler.handle(handleUI + slider.getValue());
            }

            public void drag(InputEvent event, float x, float y, int pointer) {
                UserInterface.this.inputHandler.handle(handleUI + slider.getValue());
            }
        });

        return slider;
    }

    public void nextScreen(String path) {
        this.inputHandler.handle(path);
    }

    public TextButton addTextButton(String text, float x, float y, String handleUI) {

        TextButton textButton = new TextButton(text, this.skin);
        textButton.setName(handleUI);
        textButton.setWidth(Constants.UI_IMAGE_SIZE);
        textButton.setHeight(Constants.UI_IMAGE_SIZE);
        textButton.setX(x);
        textButton.setY(y);
        this.add(textButton);

        this.addClickListener(textButton);
        this.add(textButton);
        return textButton;
    }

    public TextButton addTextButtonOtherSize(String text, float x, float y, float width, float height, String handleUI) {

        TextButton textButton = new TextButton(text, this.skin);
        textButton.setName(handleUI);
        textButton.setWidth(width);
        textButton.setHeight(height);
        textButton.setX(x);
        textButton.setY(y);
        this.add(textButton);

        this.addClickListener(textButton);
        this.add(textButton);
        return textButton;
    }

    public TextButton addTextButtonWithoutListener(String text, float x, float y) {
        TextButton textButton = new TextButton(text, this.skin);
        textButton.setWidth(Constants.UI_IMAGE_SIZE);
        textButton.setHeight(Constants.UI_IMAGE_SIZE);
        textButton.setX(x);
        textButton.setY(y);

        this.add(textButton);
        return textButton;
    }

    public Button addButtonWithoutListener(String path, int x, int y) {

        Button button = new Button(new TextureRegionDrawable(new TextureRegion(this.getAsset(path, Texture.class))));
        button.setSkin(this.getSkin());
        button.setWidth(Constants.UI_IMAGE_SIZE);
        button.setHeight(Constants.UI_IMAGE_SIZE);
        button.setX(x);
        button.setY(y);

        this.add(button);
        return button;
    }

    public Button addSkillButtonWithoutListener(String path, float x, float y, float width, float height) {

        Button button = new Button(new TextureRegionDrawable(new TextureRegion(this.getAsset(path, Texture.class))));
        button.setSkin(this.getSkin());
        button.setWidth(width);
        button.setHeight(height);
        button.setX(x);
        button.setY(y);

        this.add(button);
        return button;
    }

    public Button addSkillButtonWithoutListener(String path, String disabledPath, float x, float y, float width, float height) {

        Button button = new Button(
            new TextureRegionDrawable(new TextureRegion(this.getAsset(path, Texture.class))),
            new TextureRegionDrawable(new TextureRegion(this.getAsset(disabledPath, Texture.class))),
            new TextureRegionDrawable(new TextureRegion(this.getAsset(disabledPath, Texture.class))));
        button.setSkin(this.getSkin());
        button.setWidth(width);
        button.setHeight(height);
        button.setX(x);
        button.setY(y);

        this.add(button);
        return button;
    }

    public void dragAndDrop(final EditorMenu source, final Quickbar target) {
        DragAndDrop dragAndDrop = new DragAndDrop();
        dragAndDrop.setKeepWithinStage(true);
        dragAndDrop.addSource(new DragAndDrop.Source(source) {
            final DragAndDrop.Payload payload = new DragAndDrop.Payload();

            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                Actor item = source.hit(x, y, true);

                if ( source.isAscendantOfTable(item) && !item.getParent().getName().equals("settings") ) {
                    this.payload.setObject(item.getParent());
                    //source.removeActorTable(item);
                    Actor itemcopy = item;
                    this.payload.setDragActor(itemcopy);
                    source.reload(item.getName());
                } else {
                    this.payload.setObject(null);
                    this.payload.setDragActor(null);
                }
                return this.payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if ( target == null ) {
                    Table item = (Table) payload.getObject();
                    if ( item != null ) {
                        //source.addItemToTable(item);
                    }
                }
            }
        });

        dragAndDrop.addTarget(new DragAndDrop.Target(target) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Actor item = payload.getDragActor();
                if ( item != null ) {
                    target.addItemToTable(item);
                }
            }
        });
    }

    public LandsOfCinderWindow addTwoButtonWindow(
        Actor actor, String textLeft, String textRight, String handleLeft, String handleRight) {
        LandsOfCinderWindow landsOfCinderWindow = new LandsOfCinderWindow(this.skin);

        Table table = new Table();

        TextButton buttonLeft = new TextButton(textLeft, this.skin);
        buttonLeft.setName(handleLeft);
        this.addClickListener(buttonLeft);

        TextButton buttonRight = new TextButton(textRight, this.skin);
        buttonRight.setName(handleRight);
        this.addClickListener(buttonRight);

        table.add(actor).colspan(2);
        table.row();
        table.add(buttonLeft);
        table.add(buttonRight);

        landsOfCinderWindow.setWidth(Constants.UI_WINDOW_WIDTH);
        landsOfCinderWindow.setHeight(Constants.UI_WINDOW_HEIGHT);
        landsOfCinderWindow.setX(Constants.UI_WINDOW_X);
        landsOfCinderWindow.setY(Constants.UI_WINDOW_Y);

        landsOfCinderWindow.add(table);
        this.add(landsOfCinderWindow);

        return landsOfCinderWindow;
    }

    public void render() {
        this.stage.act();
        this.stage.draw();
    }

    public void dispose() {
        this.stage.dispose();
        this.skin.dispose();
        this.textureAtlas.dispose();
        this.assetManager.dispose();
    }

    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
    }

    public InputHandler getInputHandler() {
        return this.inputHandler;
    }

    public NinePatch getNinePatch(String name) {
        Texture t = new Texture(Gdx.files.internal(name));

        return new NinePatch(new TextureRegion(t, 1, 1, t.getWidth() - 2, t.getHeight() - 2), 10, 10, 10, 10);
    }
}
