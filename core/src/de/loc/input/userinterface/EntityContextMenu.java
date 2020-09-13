package de.loc.input.userinterface;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.core.TypeComponent;
import de.loc.game.LevelChangeComponent;
import de.loc.graphics.CameraManager;
import de.loc.graphics.LightComponent;
import de.loc.item.InventoryComponent;
import de.loc.item.ItemComponent;
import de.loc.tools.Constants;
import de.loc.tools.Position;

public class EntityContextMenu {

    private final Button addDialog;
    private final Button addQuest;
    private final Button deleteEntity;
    private final Button rotateEntity;
    private final Button scaleEntity;
    private final Button translateEntity;
    private final Button chooseLevel;
    private final Button editLight;
    private final Button showInventory;
    private final Table item;

    private Entity currentEntity = null;
    private final UserInterface ui;

    private static final int UI_OFFSET = 10;

    public EntityContextMenu(UserInterface ui) {
        this.ui = ui;
        this.addDialog = ui.addButton("UI/Icons/adddialog_icon_small.png", 0, 0, "editor_showDialogEditor");
        this.addQuest = ui.addButton("UI/Icons/addquest_icon_small.png", 0, 0, "editor_add_quest");
        this.deleteEntity = ui.addButton("UI/Icons/delete_icon_small.png", 0, 0, "editor_delete_entity");
        this.rotateEntity = ui.addButton("UI/Icons/rotate_icon_small.png", 0, 0, "editor_rotate_entity");

        this.scaleEntity = ui.addButton("UI/Icons/scale_icon_small.png", 0, 0, "editor_scale_entity");
        this.scaleEntity.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityContextMenu.this.item.clearChildren();
                EntityContextMenu.this.item.add(EntityContextMenu.this.createScalingMenu());
            }
        });

        this.translateEntity = ui.addButton("UI/Icons/translate_icon_small.png", 0, 0, "editor_translate_entity");
        this.translateEntity.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityContextMenu.this.item.clearChildren();
                EntityContextMenu.this.item.add(EntityContextMenu.this.createPositionTransformMenu());
            }
        });

        this.chooseLevel = new TextButton("Choose Level", ui.getSkin());
        this.chooseLevel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityContextMenu.this.item.clearChildren();
                EntityContextMenu.this.item.add(EntityContextMenu.this.createLevelChooser());
            }
        });

        this.editLight = new TextButton("Edit Light", ui.getSkin());
        this.editLight.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityContextMenu.this.item.clearChildren();
                EntityContextMenu.this.item.add(EntityContextMenu.this.createLightMenu());
            }
        });

        this.showInventory = new TextButton("Show Inventory", ui.getSkin());
        this.showInventory.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityContextMenu.this.item.clearChildren();
                EntityContextMenu.this.item.add(EntityContextMenu.this.showInventoryWindow());
            }
        });

        ui.remove(this.addDialog);
        ui.remove(this.addQuest);
        ui.remove(this.deleteEntity);
        ui.remove(this.rotateEntity);
        ui.remove(this.scaleEntity);
        ui.remove(this.translateEntity);
        this.item = new Table();

    }

    private Actor showInventoryWindow() {

        Table table = new Table();
        final InventoryComponent inventory = this.currentEntity.getComponent(InventoryComponent.class);

        for ( final Entity e : inventory.contents.values() ) {
            ItemComponent ic = e.getComponent(ItemComponent.class);
            String itemName = ic.name;
            String itemDescription = ic.description;
            String iconPath = ic.iconPath;

            Button button = new TextButton(itemName, this.ui.getSkin());
            button.row();
            table.add(button);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    inventory.removeItem(e, 1);
                    EntityContextMenu.this.item.clearChildren();
                    EntityContextMenu.this.item.add(EntityContextMenu.this.showInventoryWindow());
                }
            });
            table.add(button);
        }
        return table;
    }

    public void show(TypeComponent type, Position position, Entity entity) {
        this.item.clearChildren();
        this.currentEntity = entity;
        Position entityPos = CameraManager.getScreenCoordsFromGrid(position);

        if ( type.type == TypeComponent.Type.NPC ) {
            this.item.add(this.addDialog).pad(UI_OFFSET);
            this.item.add(this.addQuest).pad(UI_OFFSET);
        } else if ( type.type == TypeComponent.Type.EMPTY && type.name.equals("LevelChange") ) {
            this.item.add(this.chooseLevel);
        } else if ( type.type == TypeComponent.Type.EMPTY && type.name.equals("Light") ) {
            this.item.add(this.editLight);
        } else if ( type.type == TypeComponent.Type.CHEST ) {
            this.item.add(this.showInventory);
        }

        this.item.add(this.rotateEntity).pad(UI_OFFSET);
        this.item.add(this.translateEntity).pad(UI_OFFSET);
        this.item.add(this.scaleEntity).pad(UI_OFFSET);
        this.item.add(this.deleteEntity).pad(UI_OFFSET);

        this.item.setPosition(entityPos.x, entityPos.y - 50);
        this.ui.add(this.item);

    }

    private Table createLevelChooser() {
        Table table = new Table();

        final LevelChangeComponent lc = this.currentEntity.getComponent(LevelChangeComponent.class);
        final Label label = new Label(lc.levelPath, this.ui.getSkin());

        final SelectBox<String> box = new SelectBox<String>(this.ui.getSkin());
        Array<String> contents = new Array<String>();
        contents.add("Select a level");

        String packagePath = Constants.PACKAGE_FOLDER + LevelManager.getInstance().getCurrentPackage() + "/" + Constants.LEVELS_PATH;

        FileHandle levelsFolder = Gdx.files.local(packagePath);
        for ( FileHandle level : levelsFolder.list() ) {
            contents.add(level.name());
        }
        box.setItems(contents);

        box.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String path = box.getSelected();
                lc.levelPath = path;
                lc.packagePath = LevelManager.getInstance().getCurrentPackage();
                System.out.println(path);
                label.setText(path);
            }

        });
        table.add(label);
        table.add(box);

        return table;
    }

    private Table createScalingMenu() {
        Table table = new Table();

        Label labelX = new Label("X: ", this.ui.getSkin());
        Label labelY = new Label("Y: ", this.ui.getSkin());
        Label labelZ = new Label("Z: ", this.ui.getSkin());

        Button plusX = new TextButton("+", this.ui.getSkin());
        Button plusY = new TextButton("+", this.ui.getSkin());
        Button plusZ = new TextButton("+", this.ui.getSkin());

        Button minusX = new TextButton("-", this.ui.getSkin());
        Button minusY = new TextButton("-", this.ui.getSkin());
        Button minusZ = new TextButton("-", this.ui.getSkin());

        table.add(labelX);
        table.add(plusX);
        table.add(minusX);

        labelY.setPosition(0, 30);
        table.add(labelY);
        plusY.setPosition(30, 30);
        table.add(plusY);
        table.add(minusY);

        labelY.setPosition(0, 60);
        table.add(labelZ);
        plusY.setPosition(30, 60);
        table.add(plusZ);
        table.add(minusZ);

        plusX.addListener(this.createScaleClickListener(1.1f, 1.f, 1.f));
        minusX.addListener(this.createScaleClickListener(0.9f, 1.f, 1.f));

        plusY.addListener(this.createScaleClickListener(1.0f, 1.1f, 1.f));
        minusY.addListener(this.createScaleClickListener(1.f, 0.9f, 1.f));

        plusZ.addListener(this.createScaleClickListener(1.f, 1.f, 1.1f));
        minusZ.addListener(this.createScaleClickListener(1.f, 1.f, 0.9f));

        return table;
    }

    private Table createPositionTransformMenu() {
        Table table = new Table();

        Label labelX = new Label("X: ", this.ui.getSkin());
        Label labelY = new Label("Y: ", this.ui.getSkin());
        Label labelZ = new Label("Z: ", this.ui.getSkin());

        Button plusX = new TextButton("+", this.ui.getSkin());
        Button plusY = new TextButton("+", this.ui.getSkin());
        Button plusZ = new TextButton("+", this.ui.getSkin());

        Button minusX = new TextButton("-", this.ui.getSkin());
        Button minusY = new TextButton("-", this.ui.getSkin());
        Button minusZ = new TextButton("-", this.ui.getSkin());

        table.add(labelX);
        table.add(plusX);
        table.add(minusX);

        labelY.setPosition(0, 30);
        table.add(labelY);
        plusY.setPosition(30, 30);
        table.add(plusY);
        table.add(minusY);

        labelY.setPosition(0, 60);
        table.add(labelZ);
        plusY.setPosition(30, 60);
        table.add(plusZ);
        table.add(minusZ);

        plusX.addListener(this.createTransformationClickListener(0.1f, 0.f, 0.f));
        minusX.addListener(this.createTransformationClickListener(-0.1f, 0.f, 0.f));

        plusY.addListener(this.createTransformationClickListener(0.0f, 0.1f, 0.f));
        minusY.addListener(this.createTransformationClickListener(0.0f, -0.1f, 0.f));

        plusZ.addListener(this.createTransformationClickListener(0.0f, 0.f, 0.1f));
        minusZ.addListener(this.createTransformationClickListener(0.f, 0.f, -0.1f));

        table.bottom();
        return table;
    }

    private Table createLightMenu() {
        Table table = new Table();

        final LightComponent lc = this.currentEntity.getComponent(LightComponent.class);

        final Slider sliderR = new Slider(0.f, 1.f, 0.01f, false, this.ui.getSkin());
        final Slider sliderG = new Slider(0.f, 1.f, 0.01f, false, this.ui.getSkin());
        final Slider sliderB = new Slider(0.f, 1.f, 0.01f, false, this.ui.getSkin());

        final Slider sliderIntensity = new Slider(0.f, 100.f, 1.f, false, this.ui.getSkin());

        sliderIntensity.setValue(lc.intensity);
        sliderR.setValue(lc.color.x);
        sliderG.setValue(lc.color.y);
        sliderB.setValue(lc.color.z);

        final Label labelR = new Label("R: " + lc.color.x, this.ui.getSkin());
        final Label labelG = new Label("G: " + lc.color.y, this.ui.getSkin());
        final Label labelB = new Label("B: " + lc.color.z, this.ui.getSkin());

        final Label label = new Label("Intesity: " + lc.intensity, this.ui.getSkin());

        sliderR.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float v = sliderR.getValue();
                lc.color.x = v;
                System.out.println(v);
                labelR.setText("R: " + lc.color.x);
            }
        });

        sliderG.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float v = sliderG.getValue();
                lc.color.y = v;
                System.out.println(v);
                labelG.setText("G: " + lc.color.y);
            }
        });

        sliderB.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float v = sliderB.getValue();
                lc.color.z = v;
                System.out.println(v);
                labelB.setText("B: " + lc.color.z);
            }
        });

        sliderIntensity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float v = sliderIntensity.getValue();
                lc.intensity = v;
                System.out.println(v);
                label.setText("Intensity: " + v);
            }
        });
        table.add(labelR).row();
        table.add(sliderR).row();
        table.add(labelG).row();
        table.add(sliderG).row();
        table.add(labelB).row();
        table.add(sliderB).row();
        table.add(label).row();
        table.add(sliderIntensity);

        return table;
    }

    public void hide() {
        this.ui.getMainTable().removeActor(this.item);
        this.currentEntity = null;
    }

    private ClickListener createTransformationClickListener(final float xT, final float yT, final float zT) {
        return (new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PositionComponent pc = EntityContextMenu.this.currentEntity.getComponent(PositionComponent.class);
                pc.afterPositionTransform.translate(xT, yT, zT);
            }
        });
    }

    private ClickListener createScaleClickListener(final float xT, final float yT, final float zT) {
        return (new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PositionComponent pc = EntityContextMenu.this.currentEntity.getComponent(PositionComponent.class);
                pc.afterPositionTransform.scale(xT, yT, zT);
            }
        });
    }
}


