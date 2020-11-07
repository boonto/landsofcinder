package de.loc.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.collision.Ray;

import de.loc.core.LandsOfCinderScreen;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.event.Event;
import de.loc.event.EventSystem;
import de.loc.graphics.CameraManager;
import de.loc.input.InputHandler;
import de.loc.item.ConsumableComponent;
import de.loc.item.EquippableComponent;
import de.loc.main.MainMenuScreen;
import de.loc.tools.Position;

public class GameInputHandler extends InputHandler {

    private final ComponentMapper<ConsumableComponent> consumableMapper;
    private final ComponentMapper<EquippableComponent> equippableMapper;

    public GameInputHandler(LandsOfCinderScreen screen, GameInputSystem gameInputSystem) {
        super(screen, gameInputSystem);

        this.consumableMapper = ComponentMapper.getFor(ConsumableComponent.class);
        this.equippableMapper = ComponentMapper.getFor(EquippableComponent.class);
    }

    public boolean handle(String handleID) {

        if ( handleID.equals("game_settings") ) {
            ((GameScreen) this.screen).toggleSettings();
            return true;
        }

        if ( handleID.equals("game_settings_close") ) {
            ((GameScreen) this.screen).toggleSettings();
            return true;
        }

        if ( handleID.equals("game_toggle_grid") ) {
            LevelManager.getInstance().getLevel().toggleGameGrid();
            return true;
        }

        if ( handleID.equals("game_back_menu") ) {
            this.screen.dispose();
            this.screen.getGame().setScreen(new MainMenuScreen(this.screen.getGame()));
            return true;
        }

        if ( handleID.equals("game_inventory") ) {
            ((GameScreen) this.screen).showWindow(GameScreen.Window.INVENTORY);
            return true;
        }

        if ( handleID.equals("game_questlog") ) {
            EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.EMPTY_EVENT, ""));
            ((GameScreen) this.screen).showWindow(GameScreen.Window.QUESTLOG);
            return true;
        }

        if ( handleID.equals("game_save") ) {
            ((GameScreen) this.screen).showSaveWindow();
            return true;
        }

        if ( handleID.equals("game_window_back") ) {
            ((GameScreen) this.screen).hideWindow();
            return true;
        }

        if ( handleID.equals("game_window_save") ) {
            ((GameScreen) this.screen).save();
            return true;
        }

        if ( handleID.startsWith("game_load_") ) {
            String path = handleID.substring("game_load_".length());
            if ( !path.equals("Load EditorSave") && !path.equals("Load GameSave") && !path.equals("Load Save") ) {
                ((GameScreen) this.screen).scheduleLoad(path);
            }
            return true;
        }

        if ( handleID.equals("game_close_window") ) {
            ((GameScreen) this.screen).hideWindows();
        }

        if ( handleID.equals("game_skill_tree") ) {
            ((GameScreen) this.screen).showSkillTree();
        }

        if ( handleID.startsWith("inventory_") ) {
            Long id = Long.valueOf(handleID.substring("inventory_".length()));

            //            //TODO schöner machen
            //            Engine engine = ((GameScreen)screen).getEngine();
            //            Entity entity = engine.getEntity(id);
            //
            //            Long playerID = LevelManager.getInstance().getPlayerEntity().getId();
            //            if (entity != null) { //TODO warum null?
            //                if (consumableMapper.has(entity)) {
            //                    ConsumableComponent consumableComponent = entity.getComponent(ConsumableComponent.class);
            //                    //EventSystem.getInstance().commitEvent(new Event(EventType.CONSUME_ITEM, String.valueOf(id), String.valueOf(playerID)));
            //                } else if (consumableMapper.has(entity)) {
            //                    EquippableComponent equippableComponent = entity.getComponent(EquippableComponent.class);
            //                    //EventSystem.getInstance().commitEvent(new Event(EventType.EQUIP_ITEM, String.valueOf(id), String.valueOf(playerID)));
            //                }
            //            }
        }

        if ( handleID.equals("game_back_choose") ) {
            this.screen.dispose();
            this.screen.getGame().setScreen(new MainMenuScreen(this.screen.getGame()));
            return true;
        }

        if ( handleID.equals("game_dialog_window") ) {
            EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.DIALOG_WINDOW_CLICKED));
            return true;
        }

        return false;
    }

    //Desktop
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {

        //TODO: Geht noch schöner
        Entity player = LevelManager.getInstance().getPlayerEntity();
        PositionComponent positionComponent = player.getComponent(PositionComponent.class);
        int positionY = positionComponent.position.y;
        int positionX = positionComponent.position.x;

        switch ( character ) {
            case 'w':
                positionY -= 1;
                break;
            case 'a':
                positionX -= 1;
                break;
            case 's':
                positionY += 1;
                break;
            case 'd':
                positionX += 1;
                break;
            default:
                break;
        }

        Position screen = CameraManager.getScreenCoordsFromGrid(new Position(positionX, positionY));
        Ray pickRay = CameraManager.getPickRay(screen.x, screen.y);
        Position position = new Position(positionX, positionY);
        this.inputSystem.clicked(pickRay, position);

        return false;
    }

    //Android
    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        this.setPosition(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    private void setPosition(int screenX, int screenY) {
        Ray pickRay = CameraManager.getPickRay(screenX, screenY);
        Position position = CameraManager.getGridFromScreenCoords(pickRay);
        this.inputSystem.clicked(pickRay, position);
    }
}
