package de.loc.editor;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;

import de.loc.combat.CombatComponent;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.core.TypeComponent;
import de.loc.dialog.Dialog;
import de.loc.dialog.DialogComponent;
import de.loc.dialog.NameComponent;
import de.loc.game.LevelChangeComponent;
import de.loc.graphics.LightComponent;
import de.loc.graphics.ModelComponent;
import de.loc.graphics.ParticleComponent;
import de.loc.graphics.RotationComponent;
import de.loc.item.ConsumableComponent;
import de.loc.item.EquippableComponent;
import de.loc.item.InventoryComponent;
import de.loc.item.ItemComponent;
import de.loc.item.MerchantComponent;
import de.loc.movement.EntityStateComponent;
import de.loc.quest.QuestComponent;
import de.loc.tools.Constants;

public class SceneSaver {
    private FileHandle file;
    private XmlWriter xmlWriter;
    private final ImmutableArray<Entity> entityList;
    private ImmutableArray<Component> componentList;

    public final int SAVE_TYPE;

    private final LevelManager manager;
    private final String currentPackage;
    private final String currentLevel;

    public static class Type {
        public static final int SAVE_GAME = 1;
        public static final int SAVE_EDITOR = 2;
    }

    public SceneSaver(int saveType, ImmutableArray<Entity> entityList) {
        this.SAVE_TYPE = saveType;
        this.entityList = entityList;
        this.manager = LevelManager.getInstance();
        this.currentPackage = this.manager.getCurrentPackage();
        this.currentLevel = this.manager.getCurrentLevelFileName();
    }

    public void setInput(String text) {
        if ( this.SAVE_TYPE == Type.SAVE_GAME ) {
            this.file = Gdx.files.local(Constants.GAME_SAVES_PATH + text + ".xml");
        } else {
            String packageName = LevelManager.getInstance().getCurrentPackage();
            String levelName = LevelManager.getInstance().getCurrentLevelFileName();
            this.file = Gdx.files.local(Constants.PACKAGE_FOLDER + packageName + "/levels/" + levelName);
            //file = Gdx.files.local(Constants.EDITOR_SAVES_PATH + text + ".xml");
        }

        this.save();
    }

    public void save(String fileName) {
        this.file = Gdx.files.local(fileName);

        StringWriter writer = new StringWriter();

        //file.writeString("", false, "UTF-8");
        //FileOutputStream fo = new FileOutputStream(file.file());
        //OutputStreamWriter os = new OutputStreamWriter(fo, Charset.forName("UTF-8") );
        this.xmlWriter = new XmlWriter(writer);

        this.saveFile();

        this.file.writeString(writer.toString(), false, "UTF-8");

    }

    private void save() {
        StringWriter writer = new StringWriter();
        this.xmlWriter = new XmlWriter(writer);

        this.saveFile();

        this.file.writeString(writer.toString(), false);
        Gdx.app.log("Speichern", "Gespeichert!");
    }

    private void saveFile() {
        try {
            this.xmlWriter.element("Scene");
            //saveStartPosition();
            this.saveDirLightPosition();
            this.saveMusic();
            this.saveBackground();
            this.saveGrid();
            this.saveEntities();
            this.xmlWriter.close();
        } catch ( IOException e ) {
            Gdx.app.log("Fehler", "Beim Speichern ist ein Fehler aufgetreten");
            e.printStackTrace();
        }
    }

    private void saveDirLightPosition() throws IOException {
        LevelManager lm = LevelManager.getInstance();
        this.xmlWriter.element("DirectionLightPosition")
                      .attribute("X", lm.loadSettings.dirLightPos.x)
                      .attribute("Y", lm.loadSettings.dirLightPos.y)
                      .attribute("Z", lm.loadSettings.dirLightPos.z)
                      .pop();
    }

    private void saveMusic() throws IOException {
        LevelManager lm = LevelManager.getInstance();

        this.xmlWriter.element("Sound");
        if ( false == lm.loadSettings.ambientMusic.equals("") ) {
            this.xmlWriter.element("AmbientMusic").text(lm.loadSettings.ambientMusic).pop();
        }
        if ( false == lm.loadSettings.combatMusic.equals("") ) {
            this.xmlWriter.element("CombatMusic").text(lm.loadSettings.combatMusic).pop();
        }
        this.xmlWriter.pop();
    }

    private void saveStartPosition() throws IOException {
        this.xmlWriter.element("StartPosition")
                      .text(LevelManager.getInstance().getLevel().getStartPosition().x + "" + LevelManager.getInstance().getLevel().getStartPosition().y)
                      .pop();
    }

    private void saveBackground() throws IOException {
        this.xmlWriter.element("Background")
                      .attribute("backgroundWidth", LevelManager.getInstance().getLevel().getBackgroundWidth())
                      .text(LevelManager.getInstance().getLevel().getBackgroundPath())
                      .pop();
    }

    private void saveGrid() throws IOException {
        this.xmlWriter.element("Grid")
                      .attribute("rowValue", LevelManager.getInstance().getLevel().getGameGrid().getSize().y)
                      .attribute("columnValue", LevelManager.getInstance().getLevel().getGameGrid().getSize().x);

        for ( int column = 0; column < LevelManager.getInstance().getLevel().getGameGrid().getSize().y; column++ ) {
            String status = "";
            for ( int row = 0; row < LevelManager.getInstance().getLevel().getGameGrid().getSize().x; row++ ) {
                byte field = LevelManager.getInstance().getLevel().getGameGrid().getByteAt(row, column);
                if ( field < -0x01 ) {
                    System.out.println("is kleiner");
                }
                status += (field + ";");
            }
            this.xmlWriter.element("Row").attribute("ID", column).text(status).pop();
        }
        this.xmlWriter.pop();
    }

    private void saveEntities() throws IOException {
        this.xmlWriter.element("EntityList");

        for ( int i = 0; i < this.entityList.size(); i++ ) {
            this.xmlWriter.element("Entity").attribute("ID", i);

            Entity entity = this.entityList.get(i);

            //saveComponents(entity); //SPEICHERVARIANTE 1
            this.saveEntity(entity); //SPEICHERVARIANTE 2

            this.xmlWriter.pop();
        }
    }

    private void saveEntity(Entity entity) throws IOException {
        this.savePositionComponent(entity);
        this.saveTypeComponent(entity);
        if ( entity.getComponent(RotationComponent.class) != null ) {
            this.saveRotationComponent(entity);
        }
        if ( entity.getComponent(QuestComponent.class) != null ) {
            this.saveQuestComponent(entity);
        }
        if ( entity.getComponent(TypeComponent.class).type.equals(TypeComponent.Type.PLAYER) ) {
            this.saveCombatComponent(entity);
        }
        if ( entity.getComponent(ParticleComponent.class) != null ) {
            this.saveParticleComponent(entity);
        }

        if ( entity.getComponent(InventoryComponent.class) != null ) {
            this.saveInventoryComponent(entity);
        }
        if ( entity.getComponent(MerchantComponent.class) != null ) {
            this.saveMerchantComponent(entity);
        }
        if ( entity.getComponent(LevelChangeComponent.class) != null ) {
            this.saveLevelChangeComponent(entity);
        }
        if ( entity.getComponent(LightComponent.class) != null ) {
            this.saveLightComponent(entity);
        }

    }

    private void saveComponents(Entity entity) throws IOException {
        this.componentList = entity.getComponents();

        for ( int i = 0; i < this.componentList.size(); i++ ) {
            Component component = this.componentList.get(i);

            if ( component.toString().contains("NameComponent") ) {
                this.saveNameComponent(entity);
            }
            if ( component.toString().contains("RenderableComponent") ) {
                this.saveRenderableComponent(entity);
            }
            if ( component.toString().contains("ModelComponent") ) {
                this.saveModelComponent(entity);
            }
            if ( component.toString().contains("PositionComponent") ) {
                this.savePositionComponent(entity);
            }
            if ( component.toString().contains("MovableComponent") ) {
                this.saveMovableComponent(entity);
            }
            if ( component.toString().contains("MovementAIComponent") ) {
                this.saveMovementAIComponent(entity);
            }
            if ( component.toString().contains("EntityStateComponent") ) {
                this.saveEntityStateComponent(entity);
            }
            if ( component.toString().contains("InputComponent") ) {
                this.saveInputComponent(entity);
            }
            if ( component.toString().contains("RotationComponent") ) {
                this.saveRotationComponent(entity);
            }
            if ( component.toString().contains("ClickableComponent") ) {
                this.saveClickableComponent(entity);
            }
            if ( component.toString().contains("ItemComponent") ) {
                this.saveItemComponent(entity);
            }
            if ( component.toString().contains("DialogComponent") ) {
                this.saveDialogComponent(entity);
            }
            if ( component.toString().contains("QuestComponent") ) {
                this.saveQuestComponent(entity);
            }
            if ( component.toString().contains("CombatComponent") ) {
                this.saveCombatComponent(entity);
            }
            if ( component.toString().contains("TypeComponent") ) {
                this.saveTypeComponent(entity);
            }
            if ( component.toString().contains("EquippableComponent") ) {
                this.saveEquippableComponent(entity);
            }
            if ( component.toString().contains("ParticleComponent") ) {
                this.saveParticleComponent(entity);
            }
        }
    }

    private void saveNameComponent(Entity entity) throws IOException {
        String name = entity.getComponent(NameComponent.class).getName();

        this.xmlWriter.element("NameComponent").text(name).pop();
    }

    private void saveInventoryComponent(Entity entity) throws IOException {
        InventoryComponent ic = entity.getComponent(InventoryComponent.class);

        this.xmlWriter.element("InventoryComponent");

        for ( Entity item : ic.contents.values() ) {
            ItemComponent itemComponent = item.getComponent(ItemComponent.class);
            if ( item.getComponent(EquippableComponent.class) != null ) {
                this.xmlWriter.element("Equippable").attribute("Amount", itemComponent.getAmount()).text(itemComponent.name).pop();
            } else if ( item.getComponent(ConsumableComponent.class) != null ) {
                this.xmlWriter.element("Consumable").attribute("Amount", itemComponent.getAmount()).text(itemComponent.name).pop();
            } else {
                this.xmlWriter.element("Item").attribute("Amount", itemComponent.getAmount()).text(itemComponent.name).pop();
            }
        }
        this.xmlWriter.pop();
    }

    private void saveMerchantComponent(Entity entity) throws IOException {
        MerchantComponent mc = entity.getComponent(MerchantComponent.class);

        this.xmlWriter.element("MerchantComponent");

        for ( Entity item : mc.merchantInventory.contents.values() ) {
            ItemComponent itemComponent = item.getComponent(ItemComponent.class);
            if ( item.getComponent(EquippableComponent.class) != null ) {
                this.xmlWriter.element("Equippable")
                              .attribute("Amount", itemComponent.getAmount())
                              .attribute("Price", mc.priceList.get(itemComponent.name))
                              .text(itemComponent.name)
                              .pop();
            } else if ( item.getComponent(ConsumableComponent.class) != null ) {
                this.xmlWriter.element("Consumable")
                              .attribute("Amount", itemComponent.getAmount())
                              .attribute("Price", mc.priceList.get(itemComponent.name))
                              .text(itemComponent.name)
                              .pop();
            } else {
                this.xmlWriter.element("Item")
                              .attribute("Amount", itemComponent.getAmount())
                              .attribute("Price", mc.priceList.get(itemComponent.name))
                              .text(itemComponent.name)
                              .pop();
            }
        }
        this.xmlWriter.pop();
    }

    private void saveRenderableComponent(Entity entity) throws IOException {
        this.xmlWriter.element("RenderableComponent").pop();
    }

    private void saveModelComponent(Entity entity) throws IOException {
        ModelComponent mc = entity.getComponent(ModelComponent.class);
        String path = mc.getModelPath();
        this.xmlWriter.element("ModelComponent").text(path);

        this.xmlWriter.pop();
    }

    private void savePositionComponent(Entity entity) throws IOException {

        PositionComponent pos = entity.getComponent(PositionComponent.class);
        if ( pos == null ) {
            return;
        }

        Vector3 translation = new Vector3();
        pos.afterPositionTransform.getTranslation(translation);

        Vector3 scale = new Vector3();
        pos.afterPositionTransform.getScale(scale);

        int x = pos.position.x;
        int y = pos.position.y;

        this.xmlWriter.element("PositionComponent").attribute("PosX", x).attribute("PosY", y);

        if ( translation.x != 0.f || translation.y != 0.f || translation.z != 0.f ) {
            this.xmlWriter.element("Translation").attribute("X", translation.x).attribute("Y", translation.y).attribute("Z", translation.z).pop();
        }

        if ( scale.x != 1.f || scale.y != 1.f || scale.z != 1.f ) {
            this.xmlWriter.element("Scale").attribute("X", scale.x).attribute("Y", scale.y).attribute("Z", scale.z).pop();
        }
        this.xmlWriter.pop();

    }

    private void saveMovableComponent(Entity entity) throws IOException {
        //int x = entity.getComponent(MovableComponent.class).position.x;
        //int y = entity.getComponent(MovableComponent.class).position.y;

        this.xmlWriter.element("MovableComponent").attribute("PosX", 0).attribute("PosY", 0).pop();
    }

    private void saveMovementAIComponent(Entity entity) throws IOException {
        this.xmlWriter.element("MovementAIComponent").pop();
    }

    private void saveEntityStateComponent(Entity entity) throws IOException {
        Byte state = EntityStateComponent.EntityState.STANDING;

        this.xmlWriter.element("EntityStateComponent").text(state).pop();
    }

    private void saveInputComponent(Entity entity) throws IOException {
        this.xmlWriter.element("InputComponent").pop();
    }

    private void saveRotationComponent(Entity entity) throws IOException {
        float angle = entity.getComponent(RotationComponent.class).angle;
        this.xmlWriter.element("RotationComponent").text(angle).pop();
    }

    private void saveClickableComponent(Entity entity) throws IOException {
        this.xmlWriter.element("ClickableComponent").pop();
    }

    private void saveItemComponent(Entity entity) throws IOException {
        String name = entity.getComponent(ItemComponent.class).name;
        String description = entity.getComponent(ItemComponent.class).description;

        this.xmlWriter.element("ItemComponent").attribute("name", name).text(description).pop();
    }

    private void saveDialogComponent(Entity entity) throws IOException {
        Dialog dialog = entity.getComponent(DialogComponent.class).dialogs.get(0);

        this.xmlWriter.element("DialogComponent").text(dialog.text).pop();
    }

    private void saveQuestComponent(Entity entity) throws IOException {
        String questPath = entity.getComponent(QuestComponent.class).path;
        questPath = questPath.substring((Constants.PACKAGE_FOLDER + this.currentPackage + "/").length());
        this.xmlWriter.element("QuestComponent").text(questPath).pop();
    }

    private void saveCombatComponent(Entity entity) throws IOException {
        String level = String.valueOf(entity.getComponent(CombatComponent.class).level);
        String health = String.valueOf(entity.getComponent(CombatComponent.class).curHealth);
        String attack = String.valueOf(entity.getComponent(CombatComponent.class).attack);
        String defense = String.valueOf(entity.getComponent(CombatComponent.class).defense);
        String strength = String.valueOf(entity.getComponent(CombatComponent.class).strength);
        String knowledge = String.valueOf(entity.getComponent(CombatComponent.class).knowledge);
        String maxSteam = String.valueOf(entity.getComponent(CombatComponent.class).curSteam);
        String maxActions = String.valueOf(entity.getComponent(CombatComponent.class).curActions);

        this.xmlWriter.element("CombatComponent")
                      .attribute("Level", level)
                      .attribute("Health", health)
                      .attribute("Attack", attack)
                      .attribute("Defense", defense)
                      .attribute("Strength", strength)
                      .attribute("Knowledge", knowledge)
                      .attribute("MaxSteam", maxSteam)
                      .attribute("MaxActions", maxActions);

		/*HashMap<Byte, String> skillList = entity.getComponent(CombatComponent.class).skillList;
		for (int i=0; i<skillList.size(); i++) {
			xmlWriter.element("Skill")
			.text(skillList.get(i))
			.pop();
		}*/
        this.xmlWriter.pop();
    }

    private void saveTypeComponent(Entity entity) throws IOException {
        String name = entity.getComponent(TypeComponent.class).name;
        String type = entity.getComponent(TypeComponent.class).type.toString();
        this.xmlWriter.element("TypeComponent").attribute("Name", name).text(type).pop();
    }

    private void saveEquippableComponent(Entity entity) throws IOException {
        String type = entity.getComponent(EquippableComponent.class).type.toString();

        this.xmlWriter.element("EquippableComponent").text(type).pop();
    }

    private void saveLevelChangeComponent(Entity entity) throws IOException {
        String path = entity.getComponent(LevelChangeComponent.class).levelPath;
        //path = //path.substring((Constants.PACKAGE_FOLDER + currentPackage + "/").length());

        this.xmlWriter.element("LevelChangeComponent").text(path).pop();
    }

    private void saveLightComponent(Entity entity) throws IOException {
        LightComponent lc = entity.getComponent(LightComponent.class);

        this.xmlWriter.element("LightComponent")
                      .attribute("R", lc.color.x)
                      .attribute("G", lc.color.y)
                      .attribute("B", lc.color.z)
                      .attribute("Intensity", lc.intensity)
                      .pop();
    }

    private void saveParticleComponent(Entity entity) throws IOException {
        String particlePath = entity.getComponent(ParticleComponent.class).particlePath;
        Vector3 particlePosition = entity.getComponent(ParticleComponent.class).particlePosition;

        this.xmlWriter.element("ParticleComponent")
                      .attribute("X", particlePosition.x + LevelManager.getInstance().getLevel().getGameGrid().getSize().x / 2)
                      .attribute("Y", particlePosition.z + LevelManager.getInstance().getLevel().getGameGrid().getSize().y / 2)
                      .attribute("Z", 0)
                      .text(particlePath)
                      .pop();
    }
}
