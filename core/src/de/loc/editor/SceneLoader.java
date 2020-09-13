package de.loc.editor;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;
import java.util.StringTokenizer;

import de.loc.core.GameGrid;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.core.TypeComponent;
import de.loc.game.LevelChangeComponent;
import de.loc.graphics.LightComponent;
import de.loc.graphics.ModelComponentCreator;
import de.loc.graphics.ParticleComponent;
import de.loc.item.InventoryComponent;
import de.loc.item.MerchantComponent;
import de.loc.sound.SoundEngine;
import de.loc.tools.Constants;
import de.loc.tools.ListItem;
import de.loc.tools.XmlHelper;

import static de.loc.editor.EntityFactory.createEntity;

public class SceneLoader {
    private Engine engine;

    private XmlReader.Element xmlScene;

    private final String currentPackage;

    private HashMap<String, ListItem> mobList;
    private HashMap<String, ListItem> itemList;
    private HashMap<String, ListItem> consumableList;
    private HashMap<String, ListItem> equippableList;
    private HashMap<String, ListItem> objectList;
    private HashMap<String, ListItem> playerList;
    private HashMap<String, ListItem> commentaryObjectList;
    private HashMap<String, ListItem> emptyList;
    private HashMap<String, ListItem> chestList;
    private HashMap<String, ListItem> tileList;

    public SceneLoader() {
        this.currentPackage = LevelManager.getInstance().getCurrentPackage();
    }

    public void load(String path, Engine engine) {
        path = this.convertForAssets(path);
        Gdx.app.log("LOADER", "Geladenes Level: " + path);

        this.engine = engine;

        this.mobList = XmlHelper.getHashMapXmlList(Constants.MOB_LIST_PATH);
        this.itemList = XmlHelper.getHashMapXmlList(Constants.ITEM_LIST_PATH);
        this.consumableList = XmlHelper.getHashMapXmlList(Constants.CONSUMABLE_LIST_PATH);
        this.equippableList = XmlHelper.getHashMapXmlList(Constants.EQUIPPABLE_LIST_PATH);
        this.objectList = XmlHelper.getHashMapXmlList(Constants.OBJECT_LIST_PATH);
        this.playerList = XmlHelper.getHashMapXmlList(Constants.PLAYER_LIST_PATH);
        this.commentaryObjectList = XmlHelper.getHashMapXmlList(Constants.COMMENTARY_OBJECT_LIST_PATH);
        this.emptyList = XmlHelper.getHashMapXmlList(Constants.EMPTY_LIST_PATH);
        this.chestList = XmlHelper.getHashMapXmlList(Constants.CHEST_LIST_PATH);
        this.tileList = XmlHelper.getHashMapXmlList(Constants.TILE_LIST_PATH);

        this.parseFile(path);
        this.parseBackground();
        this.parseSoundInformation();
        this.parseDirLight();
        this.parseGrid();
        this.parseEntityList();
    }

    private void parseFile(String path) {
        XmlReader xmlReader = new XmlReader();
        FileHandle file = null;

        if ( Gdx.app.getType() == Constants.DESKTOP ) {
            file = Gdx.files.local(path);
        } else if ( Gdx.app.getType() == Constants.ANDROID ) {
            file = Gdx.files.internal(path); //TODO local/internal
        } else {
            Gdx.app.log("Fehler", "Unbekannter Application Type!");
        }

        this.xmlScene = xmlReader.parse(file);
        LevelManager.getInstance().setCurrentLevel(file.name());
    }

    public String parseBackgroundPathOnly(String path) {
        this.parseFile(path);
        XmlReader.Element xmlBackground = this.xmlScene.getChildByName("Background");
        return xmlBackground.getText();
    }

    private void parseBackground() {
        XmlReader.Element xmlBackground = this.xmlScene.getChildByName("Background");
        String imagePath = xmlBackground.getText();
        float backgroundWidth = xmlBackground.getFloat("backgroundWidth");
        LevelManager.getInstance().getLevel().setBackgroundWidth(backgroundWidth);
        LevelManager.getInstance().getLevel().setBackgroundPath(imagePath);
        LevelManager.getInstance().getLevel().updateBackground(imagePath);
    }

    private void parseSoundInformation() {

        XmlReader.Element soundTag = this.xmlScene.getChildByName("Sound");
        if ( soundTag != null ) {

            XmlReader.Element musicTag = soundTag.getChildByName("AmbientMusic");
            if ( musicTag != null ) {
                LevelManager.getInstance().loadSettings.ambientMusic = musicTag.getText();
                SoundEngine soundengine = SoundEngine.getInstance();
                soundengine.setAmbientMusic(musicTag.getText());
                soundengine.playAmbientMusic();
            } else {
                LevelManager.getInstance().loadSettings.ambientMusic = "";

            }

            XmlReader.Element combatMusicTag = soundTag.getChildByName("CombatMusic");
            if ( combatMusicTag != null ) {
                LevelManager.getInstance().loadSettings.combatMusic = combatMusicTag.getText();
                SoundEngine soundengine = SoundEngine.getInstance();
                soundengine.setCombatMusic(combatMusicTag.getText());
            } else {
                LevelManager.getInstance().loadSettings.combatMusic = "";
            }
        } else {
            LevelManager.getInstance().loadSettings.ambientMusic = "";
            LevelManager.getInstance().loadSettings.combatMusic = "";

        }

    }

    private void parseDirLight() {

        XmlReader.Element lightPos = this.xmlScene.getChildByName("DirectionLightPosition");
        if ( lightPos != null ) {
            float x = Float.valueOf(lightPos.getAttribute("X"));
            float y = Float.valueOf(lightPos.getAttribute("Y"));
            float z = Float.valueOf(lightPos.getAttribute("Z"));
            LevelManager.getInstance().loadSettings.dirLightPos.set(x, y, z);
        }
    }

    private void parseGrid() {
        XmlReader.Element xmlGrid = this.xmlScene.getChildByName("Grid");
        int rowValue = Integer.parseInt(xmlGrid.getAttribute("rowValue"));
        int columnValue = Integer.parseInt(xmlGrid.getAttribute("columnValue"));
        GameGrid grid = new GameGrid(rowValue, columnValue);
        Array<XmlReader.Element> rowList = xmlGrid.getChildrenByName("Row");

        this.parseGridFields(rowList, grid);

        grid.setSize(rowValue, columnValue);
        LevelManager.getInstance().getLevel().setGameGrid(grid);
        LevelManager.getInstance().getLevel().scaleGameGrid(rowValue, columnValue, true);
        LevelManager.getInstance().getLevel().updateGameGrid(true);
    }

    private void parseGridFields(Array<XmlReader.Element> rowList, GameGrid grid) {

        for ( XmlReader.Element xmlRow : rowList ) {
            int rowID = Integer.parseInt(xmlRow.getAttribute("ID"));
            String content = xmlRow.getText();

            StringTokenizer token = new StringTokenizer(content, ";", false);

            for ( int x = 0; x < grid.getSize().x; x++ ) {
                String s = "0";
                if ( token.hasMoreTokens() ) {
                    s = token.nextToken();
                }
                int i = Integer.parseInt(s);
                byte b = (byte) i;
                grid.setByteAt(x, rowID, b);
            }
        }

    }

    private void parseEntityList() {
        XmlReader.Element xmlEntityList = this.xmlScene.getChildByName("EntityList");
        Array<XmlReader.Element> entityList = xmlEntityList.getChildrenByName("Entity");

        for ( XmlReader.Element xmlEntity : entityList ) {
            //parseComponents(entity, xmlEntity); //LADEVARIANTE 1
            Entity entity = this.parseEntity(xmlEntity); //LADEVARIANTE 2

            this.engine.addEntity(entity);
        }
    }

    private Entity parseEntity(XmlReader.Element xmlEntity) {

        if ( xmlEntity.getChildByName("TypeComponent") != null ) {
            Entity entity;
            //TypeComponent
            String name = xmlEntity.getChildByName("TypeComponent").getAttribute("Name");
            TypeComponent.Type type = TypeComponent.Type.fromString(xmlEntity.getChildByName("TypeComponent").getText());
            entity = this.createAnyEntity(type, name, xmlEntity);

            if ( xmlEntity.getChildByName("QuestComponent") != null ) {
                this.parseQuestComponent(entity, xmlEntity);
            }

            //ParticleComponent
            if ( xmlEntity.getChildByName("ParticleComponent") != null ) {
                this.parseParticleComponent(entity, xmlEntity);
            }
            if ( xmlEntity.getChildByName("InventoryComponent") != null ) {
                this.parseInventoryComponent(entity, xmlEntity);
            }

            if ( xmlEntity.getChildByName("MerchantComponent") != null ) {
                this.parseMerchantComponent(entity, xmlEntity);
            }
            if ( xmlEntity.getChildByName("LevelChangeComponent") != null ) {
                this.parseLevelChangeComponent(entity, xmlEntity);
            }
            if ( xmlEntity.getChildByName("LightComponent") != null ) {
                this.parseLightComponent(entity, xmlEntity);
            }

            return entity;

        } else {
            Gdx.app.log("Fehler", "Kein TypeComponent!");
            return null;
        }
    }

    private Entity createAnyEntity(TypeComponent.Type type, String name, XmlReader.Element xmlEntity) {

        ListItem entityNode;

        PositionComponent pC = this.parsePositionComponent(xmlEntity);

        float angle = 0;
        if ( xmlEntity.getChildByName("RotationComponent") != null ) {
            angle = Float.valueOf(xmlEntity.getChildByName("RotationComponent").getText());
        }

        if ( type == TypeComponent.Type.PLAYER ) {
            entityNode = this.playerList.get(name);

        } else if ( type == TypeComponent.Type.NPC ) {
            name = this.convertForAssets(name);
            String npcPath;
            npcPath = Constants.PACKAGE_FOLDER + this.currentPackage + "/npcs/" + name + "/" + name + ".xml";

            entityNode = (XmlHelper.parseXml(npcPath));
        } else if ( type == TypeComponent.Type.MOB ) {
            entityNode = this.mobList.get(name);
        } else if ( type == TypeComponent.Type.ITEM ) {
            entityNode = this.itemList.get(name);
        } else if ( type == TypeComponent.Type.CONSUMABLE ) {
            entityNode = this.consumableList.get(name);
        } else if ( type == TypeComponent.Type.EQUIPPABLE ) {
            entityNode = this.equippableList.get(name);
        } else if ( type == TypeComponent.Type.OBJECT ) {
            entityNode = this.objectList.get(name);
        } else if ( type == TypeComponent.Type.COMMENTARY_OBJECT ) {
            entityNode = this.commentaryObjectList.get(name);
        } else if ( type == TypeComponent.Type.EMPTY ) {
            entityNode = this.emptyList.get(name);
        } else if ( type == TypeComponent.Type.TILE ) {
            entityNode = this.objectList.get(name);
        }
        // Entities die nicht aus listen gelesen werden können

        else if ( type == TypeComponent.Type.PARTICLE ) {
            return createEntity(TypeComponent.Type.PARTICLE, name, pC, angle);
        } else if ( type == TypeComponent.Type.CHEST ) {
            //return createEntity(type, name, pC, angle);
            entityNode = this.chestList.get(name);
        } else {
            Gdx.app.log("Fehler", "Unbekannter Typ!");
            return null;
        }
        if ( entityNode == null ) {
            System.out.println("CREATE ANY ENTITY: ");
            System.out.println("Name: " + name);
            System.out.println("TYPE: " + type);
        }
        Entity entity = createEntity(entityNode.xmlElement, pC, angle);

        //TODO habs mal hier drin gelassen geht bestimmt auch schöner
        if ( type.equals(TypeComponent.Type.PLAYER) ) {
            LevelManager.getInstance().setPlayerEntity(entity);
        }

        return entity;
    }

    private PositionComponent parsePositionComponent(XmlReader.Element xmlEntity) {
        XmlReader.Element positionNode = xmlEntity.getChildByName("PositionComponent");
        int x = Integer.parseInt(positionNode.getAttribute("PosX"));
        int y = Integer.parseInt(positionNode.getAttribute("PosY"));
        PositionComponent pc = new PositionComponent(x, y);

        XmlReader.Element translation = positionNode.getChildByName("Translation");
        if ( translation != null ) {
            float xf = Float.parseFloat(translation.getAttribute("X"));
            float yf = Float.parseFloat(translation.getAttribute("Y"));
            float zf = Float.parseFloat(translation.getAttribute("Z"));
            pc.afterPositionTransform.translate(xf, yf, zf);
        }

        XmlReader.Element scale = positionNode.getChildByName("Scale");
        if ( scale != null ) {
            float xf = Float.parseFloat(scale.getAttribute("X"));
            float yf = Float.parseFloat(scale.getAttribute("Y"));
            float zf = Float.parseFloat(scale.getAttribute("Z"));
            pc.afterPositionTransform.scale(xf, yf, zf);
        }

        return pc;
    }

    private void parseQuestComponent(Entity entity, XmlReader.Element xmlEntity) {
        String questPath = Constants.PACKAGE_FOLDER + this.currentPackage + "/" + xmlEntity.getChildByName("QuestComponent").getText();
        EntityFactory.addQuest(entity, questPath);
    }

    private void parseParticleComponent(Entity entity, XmlReader.Element xmlEntity) {
        String particlePath = xmlEntity.getChildByName("ParticleComponent").getText();
        if ( particlePath != null ) {
            Vector3 particlePosition;
            if ( xmlEntity.getChildByName(("TypeComponent")).getText().equals("PARTICLE") ) {
                float x = Float.parseFloat(xmlEntity.getChildByName("ParticleComponent").getAttribute("X"));
                float y = Float.parseFloat(xmlEntity.getChildByName("ParticleComponent").getAttribute("Y"));
                float z = Float.parseFloat(xmlEntity.getChildByName("ParticleComponent").getAttribute("Z"));
                particlePosition = new Vector3(x, z, y); //Bei Partikeln sind die Z/Y-Achsen vertauscht
            } else {
                int x = Integer.parseInt(xmlEntity.getChildByName("PositionComponent").getAttribute("PosX"));
                int y = Integer.parseInt(xmlEntity.getChildByName("PositionComponent").getAttribute("PosY"));
                float z = Float.parseFloat(xmlEntity.getChildByName("ParticleComponent").getAttribute("Z"));
                particlePosition = new Vector3(x, z, y); //Bei Partikeln sind die Z/Y-Achsen vertauscht
            }
            ModelComponentCreator.getInstance().createParticle(particlePath, particlePosition);
            entity.add(new ParticleComponent(particlePath, particlePosition));
        } else {
            Gdx.app.log("3D Partikel", "Unbekannter Pfad");
        }
    }

    private void parseMerchantComponent(Entity entity, XmlReader.Element xmlEntity) {
        XmlReader.Element inventoryNode = xmlEntity.getChildByName("MerchantComponent");
        MerchantComponent mc = new MerchantComponent();
        for ( int i = 0; i < inventoryNode.getChildCount(); i++ ) {
            XmlReader.Element itemElement = inventoryNode.getChild(i);
            int amount;
            int price;
            try {
                String amountS = itemElement.getAttribute("Amount");
                String priceS = itemElement.getAttribute("Price");
                amount = Integer.parseInt(amountS);
                price = Integer.parseInt(priceS);
            } catch ( RuntimeException e ) {
                Gdx.app.log("Load", "Error parsing MerchantComponent");
                return;
            }
            ListItem entityNode = null;
            if ( itemElement.getName().equals("Equippable") ) {
                entityNode = this.equippableList.get(itemElement.getText());
            } else if ( itemElement.getName().equals("Consumable") ) {
                entityNode = this.consumableList.get(itemElement.getText());
            } else if ( itemElement.getName().equals("Item") ) {
                entityNode = this.itemList.get(itemElement.getText());
            }

            if ( entityNode != null ) {
                Entity item = EntityFactory.createInventoryItem(entityNode.xmlElement);
                mc.addItem(item, amount, price);
            }
        }

        entity.add(mc);

    }

    private void parseInventoryComponent(Entity entity, XmlReader.Element xmlEntity) {
        XmlReader.Element inventoryNode = xmlEntity.getChildByName("InventoryComponent");
        InventoryComponent ic = new InventoryComponent();
        for ( int i = 0; i < inventoryNode.getChildCount(); i++ ) {
            XmlReader.Element itemElement = inventoryNode.getChild(i);
            int amount = 1;
            try {
                String amountS = itemElement.getAttribute("Amount");
                amount = Integer.parseInt(amountS);
            } catch ( RuntimeException e ) {

            }
            ListItem entityNode = null;
            if ( itemElement.getName().equals("Equippable") ) {
                entityNode = this.equippableList.get(itemElement.getText());
            } else if ( itemElement.getName().equals("Consumable") ) {
                entityNode = this.consumableList.get(itemElement.getText());
            } else if ( itemElement.getName().equals("Item") ) {
                entityNode = this.itemList.get(itemElement.getText());
            }

            if ( entityNode != null ) {
                Entity item = EntityFactory.createInventoryItem(entityNode.xmlElement);
                ic.addItem(item, amount);
            }
        }

        entity.add(ic);

    }

    private void parseLevelChangeComponent(Entity entity, XmlReader.Element xmlEntity) {
        XmlReader.Element levelChange = xmlEntity.getChildByName("LevelChangeComponent");
        String levelPath = levelChange.getText();
        entity.add(new LevelChangeComponent(levelPath, this.currentPackage));
    }

    private void parseLightComponent(Entity entity, XmlReader.Element xmlEntity) {
        XmlReader.Element light = xmlEntity.getChildByName("LightComponent");
        float intensity = Float.parseFloat(light.getAttribute("Intensity"));

        float r = Float.parseFloat(light.getAttribute("R"));
        float g = Float.parseFloat(light.getAttribute("G"));
        float b = Float.parseFloat(light.getAttribute("B"));
        LightComponent l = new LightComponent();
        l.intensity = intensity;
        l.color.x = r;
        l.color.y = g;
        l.color.z = b;
        entity.add(l);
    }

    /* Wandelt einen Pfad für die Assets um (Unterstriche und Lowercase) */
    private String convertForAssets(String text) {
        text = text.toLowerCase().replace(" ", "_");
        return text.toLowerCase();
    }
}
