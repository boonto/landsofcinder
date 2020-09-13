package de.loc.editor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;
import java.util.StringTokenizer;

import de.loc.combat.CombatComponent;
import de.loc.combat.Skill;
import de.loc.core.EmptyComponent;
import de.loc.core.GameGrid;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.core.TileComponent;
import de.loc.core.TypeComponent;
import de.loc.dialog.DialogComponent;
import de.loc.dialog.DialogParser;
import de.loc.dialog.NameComponent;
import de.loc.game.LevelChangeComponent;
import de.loc.graphics.AnimationComponent;
import de.loc.graphics.IconComponent;
import de.loc.graphics.LightComponent;
import de.loc.graphics.ModelComponent;
import de.loc.graphics.ModelComponentCreator;
import de.loc.graphics.ParticleComponent;
import de.loc.graphics.RenderableComponent;
import de.loc.graphics.RotationComponent;
import de.loc.input.ClickableComponent;
import de.loc.input.InputComponent;
import de.loc.input.InteractableComponent;
import de.loc.item.ConsumableComponent;
import de.loc.item.EquipmentComponent;
import de.loc.item.EquippableComponent;
import de.loc.item.InventoryComponent;
import de.loc.item.ItemComponent;
import de.loc.item.Stat;
import de.loc.item.StatComponent;
import de.loc.movement.EntityStateComponent;
import de.loc.movement.MovableComponent;
import de.loc.movement.MovementAIComponent;
import de.loc.movement.steering.SteeringAgent;
import de.loc.movement.steering.SteeringSetup;
import de.loc.online.OnlineContentComponent;
import de.loc.physics.PhysicsGenerator;
import de.loc.quest.QuestComponent;
import de.loc.quest.QuestParser;
import de.loc.tools.Constants;
import de.loc.tools.Position;

public final class EntityFactory {

    private EntityFactory() {
    }

    private static Entity createPlayer(
        String name, String modelPath, Byte entityState, int level, int health, int attack, int defense, int strength, int knowledge, int maxSteam,
        int maxActions,
        HashMap<Skill.Type, String> skillList,
        PositionComponent pC,
        float angle) {
        Entity entity = new Entity();

        entity.add(new TypeComponent(TypeComponent.Type.PLAYER, name));

        entity.add(new RenderableComponent());

        ModelComponent modelComponent = ModelComponentCreator.getInstance().createModelComponent(Constants.MODELS_PATH + modelPath);
        //PhysicsComponent physicsComponent = PhysicsGenerator.getInstance().createPhysicsComponent(modelComponent.model.model);
        entity.add(modelComponent);
        //entity.add(physicsComponent);

        EntityStateComponent entityStateComponent = new EntityStateComponent();
        entityStateComponent.setState(entityState);
        entity.add(entityStateComponent);

        entity.add(new RotationComponent(angle));
        entity.add(pC);

        entity.add(new MovableComponent(pC.position, new SteeringSetup()));
        entity.add(new InputComponent());
        entity.add(new CombatComponent(level, strength, knowledge, health / 2, maxActions, maxSteam, attack, defense, skillList));
        entity.add(new AnimationComponent(modelComponent,
                                          AnimationComponent.Animation.IDLE,
                                          AnimationComponent.Animation.WALK,
                                          AnimationComponent.Animation.RUN,
                                          AnimationComponent.Animation.ATTACK,
                                          AnimationComponent.Animation.HURT,
                                          AnimationComponent.Animation.DIE,
                                          AnimationComponent.Animation.UPPERCUT,
                                          AnimationComponent.Animation.PICKUP,
                                          AnimationComponent.Animation.COMBAT_IDLE,
                                          AnimationComponent.Animation.DAMPFSTRAHL,
                                          AnimationComponent.Animation.VICTORY,
                                          AnimationComponent.Animation.TALK));
        entity.add(new EquipmentComponent());

        entity.add(new InventoryComponent());

        return entity;
    }

    private static Entity createNpc(String name, String modelPath, PositionComponent pC, float angle, String dialog, String iconPath) {
        Entity entity = new Entity();

        entity.add(new TypeComponent(TypeComponent.Type.NPC, name));
        entity.add(new NameComponent(name));
        entity.add(new RenderableComponent());
        entity.add(new EntityStateComponent());
        entity.add(new RotationComponent(angle));
        entity.add(pC);

        ModelComponent modelComponent = ModelComponentCreator.getInstance().createModelComponent(Constants.MODELS_PATH + modelPath);
        entity.add(modelComponent);
        entity.add(new AnimationComponent(modelComponent,
                                          AnimationComponent.Animation.IDLE,
                                          AnimationComponent.Animation.WALK,
                                          AnimationComponent.Animation.RUN));

        entity.add(new MovableComponent(pC.position, new SteeringSetup(SteeringAgent.Steering.FACE)));
        entity.add(new MovementAIComponent());

        entity.add(new ClickableComponent());
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponentForNpcs());
        //entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));
        entity.add(DialogParser.createDialog(name, dialog));

        entity.add(new IconComponent(iconPath));

        return entity;
    }

    private static Entity createItem(String name, String description, String modelPath, String iconPath, PositionComponent pC, float angle) {
        Entity entity = new Entity();

        entity.add(new TypeComponent(TypeComponent.Type.ITEM, name));
        entity.add(new RenderableComponent());
        entity.add(ModelComponentCreator.getInstance().createModelComponent(Constants.MODELS_PATH + modelPath));
        entity.add(new RotationComponent(angle));
        entity.add(new ClickableComponent());
        entity.add(new ItemComponent(name, description, iconPath));
        entity.add(pC);
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));

        return entity;
    }

    private static Entity createConsumable(String name, String description, String modelPath, String iconPath, PositionComponent pC, float angle, Stat stat) {
        Entity entity = new Entity();

        entity.add(new TypeComponent(TypeComponent.Type.CONSUMABLE, name));
        entity.add(new RenderableComponent());
        entity.add(ModelComponentCreator.getInstance().createModelComponent(Constants.MODELS_PATH + modelPath));
        entity.add(new RotationComponent(angle));
        entity.add(new ClickableComponent());
        entity.add(new ItemComponent(name, description, iconPath));
        entity.add(new ConsumableComponent());
        entity.add(pC);
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));

        StatComponent statComponent = new StatComponent();
        statComponent.add(stat);
        entity.add(statComponent);

        return entity;
    }

    private static Entity createEquippable(
        String name, String description, String modelPath, String iconPath, PositionComponent pC, float angle, EquippableComponent.Type equipType, Stat stat) {
        Entity entity = new Entity();

        entity.add(new TypeComponent(TypeComponent.Type.EQUIPPABLE, name));
        entity.add(new RenderableComponent());
        entity.add(ModelComponentCreator.getInstance().createModelComponent(Constants.MODELS_PATH + modelPath));
        entity.add(new RotationComponent(angle));
        entity.add(new ClickableComponent());
        entity.add(new ItemComponent(name, description, iconPath));
        entity.add(new EquippableComponent(equipType));
        entity.add(pC);
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));

        StatComponent statComponent = new StatComponent();
        statComponent.add(stat);
        entity.add(statComponent);

        return entity;
    }

    private static Entity createObject(String name, String modelPath, PositionComponent pC, float angle) {
        Entity entity = new Entity();

        entity.add(new TypeComponent(TypeComponent.Type.OBJECT, name));
        entity.add(new RenderableComponent());
        entity.add(ModelComponentCreator.getInstance().createModelComponent(Constants.MODELS_PATH + modelPath));
        entity.add(new RotationComponent(angle));
        entity.add(pC);

        return entity;
    }

    private static Entity createCommentaryObject(String name, String modelPath, PositionComponent pC, float angle, String dialog, String iconPath) {
        Entity entity = new Entity();

        entity.add(new TypeComponent(TypeComponent.Type.COMMENTARY_OBJECT, name));
        entity.add(new RenderableComponent());
        ModelComponent m = ModelComponentCreator.getInstance().createModelComponent(Constants.MODELS_PATH + modelPath);
        entity.add(m);
        entity.add(new RotationComponent(angle));
        entity.add(pC);

        entity.add(new ClickableComponent());
        entity.add(DialogParser.createDialog(name, dialog));
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(m.model.model));

        entity.add(new InteractableComponent());

        entity.add(new IconComponent(iconPath));

        return entity;
    }

    private static Entity createMob(
        String name,
        String modelPath,
        int level,
        int health,
        int strength,
        int knowledge,
        int maxSteam,
        int maxActions,
        int attack,
        int defense,
        HashMap<Skill.Type, String> skillList,
        PositionComponent pC,
        float angle) {
        Entity entity = new Entity();

        entity.add(new TypeComponent(TypeComponent.Type.MOB, name));
        entity.add(pC);
        entity.add(new RotationComponent(angle));

        entity.add(new RenderableComponent());
        entity.add(new ClickableComponent());
        entity.add(new CombatComponent(level, strength, knowledge, health, maxActions, maxSteam, attack, defense, skillList));
        entity.add(new EntityStateComponent());

        ModelComponent modelComponent = ModelComponentCreator.getInstance().createModelComponent(Constants.MODELS_PATH + modelPath);
        entity.add(modelComponent);
        entity.add(new AnimationComponent(modelComponent,
                                          AnimationComponent.Animation.IDLE,
                                          AnimationComponent.Animation.WALK,
                                          AnimationComponent.Animation.RUN));

        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));

        entity.add(new MovementAIComponent(pC.position, 3));
        entity.add(new MovableComponent(pC.position, new SteeringSetup()));

        return entity;
    }

    public static Entity addQuest(Entity entity, String xmlPath) {
        String clientName = entity.getComponent(NameComponent.class).name;
        QuestComponent q = QuestParser.parseQuestComponent(xmlPath, clientName);
        entity.add(q);
        // add the Quest-Dialog to the Npc
        // TODO: der Dialog sollte über eine zentrale Stelle den NPCS hinzugefügt werden!

        LevelManager levelmanager = LevelManager.getInstance();
        String currentPackage = levelmanager.getCurrentPackage();
        entity.add(DialogParser.loadDialog(Constants.PACKAGE_FOLDER + currentPackage + q.DIALOG));

        //        DialogComponent d = new DialogComponent(q.DIALOG.dialog);
        //        entity.add(d);

        return entity;
    }

    private static Entity createNpcFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        String name = node.getChildByName("Name").getText();
        String modelPath = node.getChildByName("ModelPath").getText();
        String dialog = node.getChildByName("Dialog").getText();

        String iconPath = node.getChildByName("Icon").getText();

        return createNpc(name, modelPath, pC, angle, dialog, iconPath);
    }

    private static Entity createChestFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        Entity entity = new Entity();
        entity.add(new TypeComponent(TypeComponent.Type.CHEST, node.getChildByName("Name").getText()));
        entity.add(new RenderableComponent());
        String modelPath = Constants.MODELS_PATH + node.getChildByName("ModelPath").getText();
        entity.add(ModelComponentCreator.getInstance().createModelComponent(modelPath));
        entity.add(new RotationComponent(angle));
        entity.add(new ClickableComponent());
        entity.add(pC);
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));
        entity.add(new InventoryComponent());
        return entity;
    }

    private static Entity createItemFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        String name = node.getChildByName("Name").getText();
        String description = node.getChildByName("Description").getText();
        String modelPath = node.getChildByName("ModelPath").getText();
        String iconPath = node.getChildByName("Icon").getText();

        return createItem(name, description, modelPath, iconPath, pC, angle);
    }

    private static Entity createConsumableFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        String name = node.getChildByName("Name").getText();
        String description = node.getChildByName("Description").getText();
        String modelPath = node.getChildByName("ModelPath").getText();
        String iconPath = node.getChildByName("Icon").getText();
        String[] statParts = node.getChildByName("Stat").getText().split(",");
        Stat stat = new Stat(Stat.Type.valueOf(statParts[0]), Integer.parseInt(statParts[1]));

        return createConsumable(name, description, modelPath, iconPath, pC, angle, stat);
    }

    private static Entity createEquippableFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        String name = node.getChildByName("Name").getText();
        String description = node.getChildByName("Description").getText();
        String modelPath = node.getChildByName("ModelPath").getText();
        String iconPath = node.getChildByName("Icon").getText();
        EquippableComponent.Type equipType = EquippableComponent.Type.fromString(node.getChildByName("Equipment").getText());
        String[] statParts = node.getChildByName("Stat").getText().split(",");
        Stat stat = new Stat(Stat.Type.valueOf(statParts[0]), Integer.parseInt(statParts[1]));

        return createEquippable(name, description, modelPath, iconPath, pC, angle, equipType, stat);
    }

    private static Entity createObjectFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        String name = node.get("Name");
        String modelPath = node.getChildByName("ModelPath").getText();
        String[] sizeParts = node.getChildByName("Size").getText().split(",");
        pC.size = new Position(Integer.parseInt(sizeParts[0]), Integer.parseInt(sizeParts[1]));

        return createObject(name, modelPath, pC, angle);
    }

    private static Entity createTileFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        String name = node.get("Name");
        String modelPath = node.getChildByName("ModelPath").getText();

        String iconPath = node.getChildByName("Icon").getText();

        String[] sizeParts = node.getChildByName("Size").getText().split(",");
        pC.size = new Position(Integer.parseInt(sizeParts[0]), Integer.parseInt(sizeParts[1]));

        int sizeX = Integer.parseInt(sizeParts[0]);
        int sizeY = Integer.parseInt(sizeParts[1]);
        byte[][] typeMap = new byte[sizeX][sizeY];
        byte[][] heightMap = new byte[sizeX][sizeY];

        StringTokenizer heightTokens = new StringTokenizer(node.get("Height"), ";", false);
        StringTokenizer fieldTokens = new StringTokenizer(node.get("Fields"), ";", false);

        for ( int x = 0; x < sizeX; x++ ) {
            for ( int y = 0; y < sizeY; y++ ) {
                typeMap[x][y] = (Integer.parseInt(fieldTokens.nextToken()) == 1 ? GameGrid.Type.WALKABLE : GameGrid.Type.OBSTRUCTED);
                heightMap[x][y] = (Byte.parseByte(heightTokens.nextToken()));
            }
        }
        return createTile(name, modelPath, typeMap, heightMap, pC);
    }

    public static Entity createTile(String name, String modelPath, byte[][] typeMap, byte[][] heightMap, PositionComponent pc) {
        Entity entity = new Entity();

        entity.add(pc);
        entity.add(new TileComponent(typeMap, heightMap));

        entity.add(new TypeComponent(TypeComponent.Type.TILE, name));
        entity.add(new RotationComponent(0));
        entity.add(new RenderableComponent());
        ModelComponent modelComponent = ModelComponentCreator.getInstance().createModelComponent(Constants.MODELS_PATH + modelPath);
        entity.add(modelComponent);
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));

        return entity;
    }

    private static Entity createCommentaryObjectFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        String name = node.get("Name");
        String modelPath = node.getChildByName("ModelPath").getText();

        String iconPath = node.getChildByName("Icon").getText();
        String dialog = node.getChildByName("Dialog").getText();
        String[] sizeParts = node.getChildByName("Size").getText().split(",");
        pC.size = new Position(Integer.parseInt(sizeParts[0]), Integer.parseInt(sizeParts[1]));

        return createCommentaryObject(name, modelPath, pC, angle, dialog, iconPath);
    }

    private static Entity createMobFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        String name = node.getChildByName("Name").getText();
        String modelPath = node.getChildByName("ModelPath").getText();

        int level = Integer.parseInt(node.getChildByName("Level").getText());
        int health = Integer.parseInt(node.getChildByName("Health").getText());
        int attack = Integer.parseInt(node.getChildByName("Attack").getText());
        int defense = Integer.parseInt(node.getChildByName("Defense").getText());
        int strength = Integer.parseInt(node.getChildByName("Strength").getText());
        int knowledge = Integer.parseInt(node.getChildByName("Knowledge").getText());
        int maxSteam = Integer.parseInt(node.getChildByName("MaxSteam").getText());
        int maxActions = Integer.parseInt(node.getChildByName("MaxActions").getText());

        XmlReader.Element xmlSkillList = node.getChildByName("SkillList");
        HashMap<Skill.Type, String> skillList = new HashMap<>();
        for ( XmlReader.Element skill : xmlSkillList.getChildrenByName("Skill") ) {
            skillList.put(Skill.Type.fromString(skill.getText()), skill.getText());
        }

        return createMob(name, modelPath, level, health, strength, knowledge, maxSteam, maxActions, attack, defense, skillList, pC, angle);
    }

    private static Entity createPlayerFromXml(XmlReader.Element node, PositionComponent pC, float angle) {
        String name = node.getChildByName("Name").getText();
        String modelPath = node.getChildByName("ModelPath").getText();
        Byte entityState = Byte.valueOf(node.getChildByName("EntityState").getText());

        int level = Integer.parseInt(node.getChildByName("Level").getText());
        int health = Integer.parseInt(node.getChildByName("Health").getText());
        int attack = Integer.parseInt(node.getChildByName("Attack").getText());
        int defense = Integer.parseInt(node.getChildByName("Defense").getText());
        int strength = Integer.parseInt(node.getChildByName("Strength").getText());
        int knowledge = Integer.parseInt(node.getChildByName("Knowledge").getText());
        int maxSteam = Integer.parseInt(node.getChildByName("MaxSteam").getText());
        int maxActions = Integer.parseInt(node.getChildByName("MaxActions").getText());

        XmlReader.Element xmlSkillList = node.getChildByName("SkillList");
        HashMap<Skill.Type, String> skillList = new HashMap<>();
        for ( XmlReader.Element skill : xmlSkillList.getChildrenByName("Skill") ) {
            skillList.put(Skill.Type.fromString(skill.getText()), skill.getText());
        }

        return createPlayer(name, modelPath, entityState, level, health, attack, defense, strength, knowledge, maxSteam, maxActions, skillList, pC, angle);
    }

    private static Entity addLevelChange(Entity entity) {
        entity.add(new ClickableComponent());
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));
        entity.add(new LevelChangeComponent("", ""));
        return entity;
    }

    private static Entity addLight(Entity entity) {
        entity.add(new LightComponent());
        return entity;
    }

    private static Entity addOnlineContentComponent(Entity entity) {
        entity.add(new ClickableComponent());
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));
        entity.add(new OnlineContentComponent());
        return entity;
    }

    private static Entity addCommentary(Entity entity) {
        entity.add(new ClickableComponent());
        entity.add(PhysicsGenerator.getInstance().createPhysicsComponent(entity.getComponent(ModelComponent.class).model.model));

        // TODO: commentary empties implementieren!
        DialogComponent d = DialogParser.createDialog("HALLO", "GUTEN TAG");
        entity.add(d);
        entity.add(new IconComponent("keinicon_icon.png"));

        return entity;
    }

    private static Entity createEmpty(String name, PositionComponent pC) {
        Entity entity = new Entity();
        entity.add(new TypeComponent(TypeComponent.Type.EMPTY, name));
        entity.add(pC);

        entity.add(new RenderableComponent());
        entity.add(ModelComponentCreator.getInstance().createEmpty(""));

        entity.add(new EmptyComponent());
        entity.add(new RotationComponent());

        if ( name.equals("Light") ) {
            addLight(entity);
        } else if ( name.equals("LevelChange") ) {
            addLevelChange(entity);
        } else if ( name.equals("Commentary") ) {
            addCommentary(entity);
        } else if ( name.equals("OnlineContent") ) {
            addOnlineContentComponent(entity);
        }

        return entity;
    }

    private static Entity createEmptyFromXml(XmlReader.Element node, PositionComponent pC) {
        String name = node.getChildByName("Name").getText();

        return createEmpty(name, pC);
    }

    private static Entity createParticleFromXml(String path, PositionComponent pC) {
        Vector3 particlePosition = new Vector3((float) pC.position.x, (float) pC.position.y, 0.0f);

        Entity particleEntity = new Entity();
        particleEntity.add(new TypeComponent(TypeComponent.Type.PARTICLE, path));
        particleEntity.add(pC);
        particleEntity.add(new RotationComponent());
        particleEntity.add(new ParticleComponent(path, particlePosition));
        return particleEntity;
    }

    public static Entity createEntity(XmlReader.Element entityXmlNode, PositionComponent pC, float angle) {

        String name = entityXmlNode.getName();

        if ( name.equals("Item") ) {
            return createItemFromXml(entityXmlNode, pC, angle);
        } else if ( name.equals("Consumable") ) {
            return createConsumableFromXml(entityXmlNode, pC, angle);
        } else if ( name.equals("Equippable") ) {
            return createEquippableFromXml(entityXmlNode, pC, angle);
        } else if ( name.equals("Npc") ) {
            return createNpcFromXml(entityXmlNode, pC, angle);
        } else if ( name.equals("Object") ) {
            return createObjectFromXml(entityXmlNode, pC, angle);
        } else if ( name.equals("Mob") ) {
            return createMobFromXml(entityXmlNode, pC, angle);
        } else if ( name.equals("Player") ) {
            return createPlayerFromXml(entityXmlNode, pC, angle);
        } else if ( name.equals("CommentaryObject") ) {
            return createCommentaryObjectFromXml(entityXmlNode, pC, angle);
        } else if ( name.equals("Empty") ) {
            return createEmptyFromXml(entityXmlNode, pC);
        } else if ( name.equals("Chest") ) {
            return createChestFromXml(entityXmlNode, pC, angle);
        } else if ( name.equals("Tile") ) {
            return createTileFromXml(entityXmlNode, pC, angle);
        } else {
            return null;
        }
    }

    public static Entity createEntity(TypeComponent.Type type, String name, PositionComponent pC, float angle) {
        //if (type.equals(Type.CHEST)) {
        // return createChestFromXml(pC, angle);
        //} else
        if ( type == TypeComponent.Type.PARTICLE ) {
            return createParticleFromXml(name, pC);
        } else {
            return null;
        }
    }

    public static Entity createInventoryItem(XmlReader.Element itemXmlNode) {
        Entity entity = new Entity();

        String name = itemXmlNode.getChildByName("Name").getText();
        String description = itemXmlNode.getChildByName("Description").getText();
        String iconPath = itemXmlNode.getChildByName("Icon").getText();

        // EQUIP
        //TODO: Equipment parsen scheint nicht mehr zu funktionieren! Finde heraus warum!
        if ( itemXmlNode.getName().equals("Equippable") ) {
            EquippableComponent.Type equipType = EquippableComponent.Type.valueOf(itemXmlNode.getChildByName("Equipment").getText());
            String[] statParts = itemXmlNode.getChildByName("Stat").getText().split(",");
            Stat stat = new Stat(Stat.Type.valueOf(statParts[0]), Integer.parseInt(statParts[1]));

            entity.add(new ItemComponent(name, description, iconPath));
            entity.add(new EquippableComponent(equipType));
            entity.add(new TypeComponent(TypeComponent.Type.ITEM, name));
            StatComponent statComponent = new StatComponent();
            statComponent.add(stat);
            entity.add(statComponent);
        }
        // ITEM
        else if ( itemXmlNode.getName().equals("Item") ) {
            entity.add(new ItemComponent(name, description, iconPath));
            entity.add(new TypeComponent(TypeComponent.Type.ITEM, name));
        }
        // CONSUMABLE
        else if ( itemXmlNode.getName().equals("Consumable") ) {
            String[] statParts = itemXmlNode.getChildByName("Stat").getText().split(",");
            Stat stat = new Stat(Stat.Type.valueOf(statParts[0]), Integer.parseInt(statParts[1]));

            entity.add(new ItemComponent(name, description, iconPath));
            entity.add(new ConsumableComponent());
            entity.add(new TypeComponent(TypeComponent.Type.ITEM, name));
            StatComponent statComponent = new StatComponent();
            statComponent.add(stat);
            entity.add(statComponent);
        } else {
            return null;
        }

        return entity;
    }
}
