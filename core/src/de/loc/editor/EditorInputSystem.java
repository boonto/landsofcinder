package de.loc.editor;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;

import de.loc.core.GameGrid;
import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.core.TileComponent;
import de.loc.core.TypeComponent;
import de.loc.graphics.BackgroundComponent;
import de.loc.graphics.CameraManager;
import de.loc.graphics.CustomEffectData;
import de.loc.graphics.ModelComponent;
import de.loc.graphics.RotationComponent;
import de.loc.input.IInput;
import de.loc.tools.Position;

public class EditorInputSystem extends LandsOfCinderSystem implements IInput {

    private final EditorScreen.Type editorType;
    private XmlReader.Element entityXmlNode;

    private Entity currentEntity;

    private final ComponentMapper<PositionComponent> positionMapper;
    private final ComponentMapper<ModelComponent> modelMapper;
    private final ComponentMapper<RotationComponent> rotationMapper;
    private final ComponentMapper<TypeComponent> typeMapper;
    private final ImmutableArray<Entity> entities;

    private final EditorScreen editor;

    private boolean brushMode = false;
    private byte brushType;

    private final Position mousePosition;
    private Entity hoveredEntity;

    private enum InputState {
        EDIT_ENTITY,
        PLACE_ENTITY,
        MOVE_ENTITY,
        NOTHING_SELECTED
    }

    private InputState inputState;

    private final ArrayList<Entity> lastClickedEntities = new ArrayList<>();
    private int lastClickedCounter = 0;
    private Position lastClickedPosition = new Position(0, 0);

    public void update(float deltaTime) {
        if ( this.inputState == InputState.NOTHING_SELECTED ) {
            Ray pickRay = CameraManager.getPickRay(this.mousePosition.x, this.mousePosition.y);
            Position position = CameraManager.getGridFromScreenCoords(pickRay);
            if ( position != null ) {
                Entity entity = this.getFirstEntityAt(position);
                if ( entity != null ) {
                    if ( this.hoveredEntity == entity ) {
                        return;
                    }

                    if ( this.hoveredEntity != null ) {
                        ((CustomEffectData) this.modelMapper.get(this.hoveredEntity).model.userData).highlightColor.set(0.0f, 0.0f, 0.0f);
                    }
                    this.hoveredEntity = entity;
                    ((CustomEffectData) this.modelMapper.get(this.hoveredEntity).model.userData).highlightColor.set(0.4f, 0.4f, 0.4f);
                } else {
                    if ( this.hoveredEntity != null ) {
                        ((CustomEffectData) this.modelMapper.get(this.hoveredEntity).model.userData).highlightColor.set(0.0f, 0.0f, 0.0f);
                    }
                    this.hoveredEntity = null;
                }
            }
        } else if ( this.inputState == InputState.MOVE_ENTITY ) {
            Ray pickRay = CameraManager.getPickRay(this.mousePosition.x, this.mousePosition.y);
            Position position = CameraManager.getGridFromScreenCoords(pickRay);
            if ( position != null ) {
                TypeComponent t = this.typeMapper.get(this.currentEntity);
                if ( t.type == TypeComponent.Type.ITEM || t.type == TypeComponent.Type.CONSUMABLE || t.type == TypeComponent.Type.EQUIPPABLE ) {
                    this.positionMapper.get(this.currentEntity).setPosition(position.x, position.y);
                } else if ( LevelManager.getInstance().getLevel().getGameGrid().isWalkable(position) ) {
                    this.positionMapper.get(this.currentEntity).setPosition(position.x, position.y);
                }

            }
        } else if ( this.inputState == InputState.PLACE_ENTITY ) {
            Ray pickRay = CameraManager.getPickRay(this.mousePosition.x, this.mousePosition.y);
            Position position = CameraManager.getGridFromScreenCoords(pickRay);
            if ( position != null ) {
                this.positionMapper.get(this.currentEntity).setPosition(position.x, position.y);
            }
        }

    }

    @Override
    public void reset() {

    }

    public EditorInputSystem(EditorScreen screen, EditorScreen.Type editorType) {

        this.editor = screen;
        this.entityXmlNode = null;
        this.currentEntity = null;
        this.inputState = InputState.NOTHING_SELECTED;

        this.editorType = editorType;

        if ( editorType == EditorScreen.Type.NEW_WORLD ) {
            LevelManager.getInstance().getLevel().setDrawGrid(true);
        }

        this.positionMapper = ComponentMapper.getFor(PositionComponent.class);
        this.modelMapper = ComponentMapper.getFor(ModelComponent.class);
        this.rotationMapper = ComponentMapper.getFor(RotationComponent.class);
        this.typeMapper = ComponentMapper.getFor(TypeComponent.class);
        this.entities = screen.getEngine().getEntitiesFor(Family.all(PositionComponent.class).get());

        this.mousePosition = new Position();
    }

    private void selectEntity(Position position) {
        Entity entity = this.getEntityAt(position);
        this.setCurrentEntity(entity);
    }

    private void placeEntity(Position position) {
        //Note: currentEntity is the entity that the user wants to place
        // the entity at the position (getEntityAt(pos)) might be a tile
        // or some other object
        TypeComponent clickedEntityType = this.typeMapper.get(this.currentEntity);
        if ( clickedEntityType.type == TypeComponent.Type.TILE ) {
            this.placeTile(position);
        } else {
            this.placeGameObject(position);
        }

    }

    private void placeTile(Position position) {
        TileComponent t = this.currentEntity.getComponent(TileComponent.class);
        if ( LevelManager.getInstance().getLevel().getGameGrid().isTilePlaceable(t, position.x, position.y) ) {
            LevelManager.getInstance().getLevel().getGameGrid().addTile(t, position.x, position.y);
            this.setCurrentEntity(null);
            this.entityXmlNode = null;
            this.inputState = InputState.NOTHING_SELECTED;
        }
    }

    private void placeGameObject(Position position) {
        TypeComponent currentEntityType = this.typeMapper.get(this.currentEntity);

        // clicked Entity is the entity that is on field where the user wants to place the currentEntity!
        // (Can be null, can be currentEntity...)
        Entity clickedEntity = this.getEntityAt(position);
        TypeComponent clickedEntityType = this.typeMapper.get(clickedEntity);
        // Items can be placed everywhere, even on obstructed fields!
        if ( clickedEntityType.type != TypeComponent.Type.CHEST && (currentEntityType.type == TypeComponent.Type.ITEM
                                                                    || currentEntityType.type == TypeComponent.Type.CONSUMABLE
                                                                    || currentEntityType.type == TypeComponent.Type.EQUIPPABLE) ) {
            if ( clickedEntityType.type != TypeComponent.Type.NPC ) {
                this.setCurrentEntity(null);
                this.entityXmlNode = null;
                this.inputState = InputState.NOTHING_SELECTED;
            }
        }
        // TODO: this part of the code let the user place items in chests.
        // This is currently not possible because of the new tile system.
        // TODO: find aother solution (one that is more convenient)
        //        else if (entity != currentEntity)
        //        {
        //            // user clicked on a chest and wants to place an item?
        //            // then put it into the chest!
        //            String name = entityXmlNode.getName().toUpperCase();
        //            if (name.equals(TypeComponent.Type.EQUIPPABLE.toString())
        //                    || name.equals(TypeComponent.Type.CONSUMABLE.toString())
        //                    || name.equals(TypeComponent.Type.ITEM.toString()))
        //            {
        //                TypeComponent clickedEntityType2 = typeMapper.get(entity);
        //                if (clickedEntityType2.type == TypeComponent.Type.CHEST)
        //                {
        //                    // TODO:
        //                    // das ist eigentlich sau blöd hartgecoded und gehört hier nicht so rein,
        //                    // aber was besseres fällt mir auf die Schnelle auch nicht ein!
        //                    InventoryComponent chestInventoty = entity.getComponent(InventoryComponent.class);
        //                    Entity item = EntityFactory.createInventoryItem(entityXmlNode);
        //                    chestInventoty.addItem(item, 1);
        //
        //                }
        //            }
        //        }
        // no entity? place one
        else if ( LevelManager.getInstance().getLevel().getGameGrid().isWalkable(position) ) {
            PositionComponent p = this.positionMapper.get(this.currentEntity);
            LevelManager.getInstance().getLevel().getGameGrid().setToOccupied(p.position, p.size, this.rotationMapper.get(this.currentEntity).angle);
            this.setCurrentEntity(null);
            this.entityXmlNode = null;
            this.inputState = InputState.NOTHING_SELECTED;
        }
    }

    private void editEntity(Position position) {
        Entity entity = this.getEntityAt(position);

        if ( this.currentEntity.equals(entity) ) {
            this.inputState = InputState.MOVE_ENTITY;
            PositionComponent p = this.positionMapper.get(this.currentEntity);
            LevelManager.getInstance().getLevel().getGameGrid().setToWalkable(p.position, p.size, this.rotationMapper.get(this.currentEntity).angle);
            ((CustomEffectData) this.modelMapper.get(this.currentEntity).model.userData).highlightColor.set(0.0f, 1.0f, 0.0f);
            this.editor.hideContextMenu();
        } else {
            this.setCurrentEntity(entity);
        }
    }

    private void moveEntity(Position position) {
        PositionComponent p = this.positionMapper.get(this.currentEntity);
        TypeComponent t = this.typeMapper.get(this.currentEntity);

        if ( t.type == TypeComponent.Type.ITEM || t.type == TypeComponent.Type.CONSUMABLE || t.type == TypeComponent.Type.EQUIPPABLE ) {
            // dont check if a field is occupied when moving items
            // in order to place items on objects like tables
            LevelManager.getInstance().getLevel().getGameGrid().setToOccupied(p.position, p.size, this.rotationMapper.get(this.currentEntity).angle);
            this.setCurrentEntity(null);
        } else if ( LevelManager.getInstance().getLevel().getGameGrid().isWalkable(position) ) {
            LevelManager.getInstance().getLevel().getGameGrid().setToOccupied(p.position, p.size, this.rotationMapper.get(this.currentEntity).angle);
            this.setCurrentEntity(null);
        }

    }

    private void checkClickCounter(Position position) {
        if ( this.lastClickedPosition == null ) {
            Gdx.app.log("INPUT", "last clicked position is null!");
        }
        if ( position.equals(this.lastClickedPosition) ) {
            this.lastClickedCounter++;
        }
        Gdx.app.log("INPUT", "Clicked: " + this.lastClickedCounter);
    }

    @Override
    public void clicked(Ray pickRay, Position position) {

        if ( position == null ) {
            return;
        }

        this.editBrushType(position);

        this.checkClickCounter(position);
        if ( this.inputState == InputState.NOTHING_SELECTED ) {
            this.selectEntity(position);
        } else if ( this.inputState == InputState.PLACE_ENTITY ) {
            this.placeEntity(position);
        } else if ( this.inputState == InputState.EDIT_ENTITY ) {
            this.editEntity(position);
        } else if ( this.inputState == InputState.MOVE_ENTITY ) {
            this.moveEntity(position);
        }

        this.lastClickedPosition = position;

    }

    @Override
    public void dragged(Position position) {
        if ( this.editorType == EditorScreen.Type.NEW_WORLD && this.brushMode ) {
            this.editGameGrid(position);
        }
    }

    private void editBrushType(Position position) {
        if ( LevelManager.getInstance().getLevel().getGameGrid().isObstructed(position) ) {
            this.brushType = GameGrid.Type.WALKABLE;
        } else {
            this.brushType = GameGrid.Type.OBSTRUCTED;
        }
    }

    private void editGameGrid(Position position) {

        LevelManager.getInstance().getLevel().getGameGrid().setType(position, this.brushType);
        LevelManager.getInstance().getLevel().updateGameGrid(false);
    }

    private Entity getFirstEntityAt(Position position) {
        if ( position == null ) {
            return null;
        }
        for ( Entity entity : this.entities ) {
            if ( position.compareTo(this.positionMapper.get(entity).position) ) {
                return entity;
            }
        }
        return null;
    }

    private Entity getEntityAt(Position position) {
        if ( position.equals(this.lastClickedPosition) ) {

            if ( this.lastClickedEntities.isEmpty() ) {
                return null;
            }

            int index = ((this.lastClickedCounter) / 3);
            if ( this.lastClickedEntities.size() > (index) ) {
                Gdx.app.log("INPUT", "LastCounter: " + this.lastClickedCounter + "Size: " + this.lastClickedEntities.size() + "Ich returne Entity: " + index);
                return this.lastClickedEntities.get(index);
            } else {
                this.lastClickedCounter = 0;
                this.lastClickedPosition = null;
                return this.lastClickedEntities.get(0);
            }
        } else {
            this.lastClickedCounter = 0;
            this.lastClickedEntities.clear();
            for ( Entity entity : this.entities ) {
                if ( position.compareTo(this.positionMapper.get(entity).position) ) {
                    this.lastClickedEntities.add(entity);
                }
            }
            if ( this.lastClickedEntities.isEmpty() ) {
                return null;
            } else {
                return this.lastClickedEntities.get(0);
            }
        }

    }

    private void setCurrentEntity(Entity entity) {
        if ( this.editorType == EditorScreen.Type.EDIT_WORLD ) {
            // das alte Entity muss demarkiert werden
            if ( this.currentEntity != null ) {
                ((CustomEffectData) this.modelMapper.get(this.currentEntity).model.userData).highlightColor.set(0.0f, 0.0f, 0.0f);
            }

            this.currentEntity = entity;

            if ( this.currentEntity != null ) {
                ((CustomEffectData) this.modelMapper.get(this.currentEntity).model.userData).highlightColor.set(0.0f, 0.0f, 1.0f);
                this.editor.showContextMenu(entity, this.positionMapper.get(this.currentEntity).position);
                this.inputState = InputState.EDIT_ENTITY;
            } else {
                this.inputState = InputState.NOTHING_SELECTED;
                this.editor.hideContextMenu();
            }
        }
    }

    public void setXmlEntity(XmlReader.Element xmlNode) {
        this.entityXmlNode = xmlNode;
        if ( xmlNode != null ) {
            this.currentEntity = this.editor.placeEntity(xmlNode, new Position(0, 0));
            if ( this.currentEntity != null ) {
                this.inputState = InputState.PLACE_ENTITY;
            }

        }
    }

    public Entity getCurrentEntity() {

        return this.currentEntity;
    }

    public void toggleBrushMode() {
        this.brushMode = !this.brushMode;
    }

    public boolean getBrushMode() {
        return this.brushMode;
    }

    public void rightClick() {
        this.setCurrentEntity(null);
        this.lastClickedCounter = 0;
    }

    public void setObstructedFields(Engine engine) {
        GameGrid grid = LevelManager.getInstance().getLevel().getGameGrid();
        for ( Entity entity : engine.getEntitiesFor(Family.exclude(BackgroundComponent.class, TileComponent.class).get()) ) {
            PositionComponent p = this.positionMapper.get(entity);
            RotationComponent r = this.rotationMapper.get(entity);
            grid.setToOccupied(p.position, p.size, r.angle);
        }
    }

    public void setCurrentMousePosition(int x, int y) {
        this.mousePosition.x = x;
        this.mousePosition.y = y;
    }
}
