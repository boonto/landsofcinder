package de.loc.tools;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Constants {

    public static final Application.ApplicationType DESKTOP = Application.ApplicationType.Desktop;
    public static final Application.ApplicationType ANDROID = Application.ApplicationType.Android;

    public static final int APP_WIDTH = 1024;
    public static final int APP_HEIGHT = 720;

    public static final float CAMERA_DEGREE = 30.0f;

    // !- all values in meters!
    public static final float VIEWPORT_HEIGHT = 5.0f;
    public static final float VIEWPORT_WIDTH = VIEWPORT_HEIGHT * ((float) APP_WIDTH / (float) APP_HEIGHT);

    public static final float GRID_WIDTH = 1.0f;
    public static final float GRID_WIDTH_HALF = GRID_WIDTH / 2.0f;
    public static final float GRID_HEIGHT_STEP = 0.1f;
    public static final int GRID_HEIGHT_RANGE = 32;
    public static final float CAMERA_ANGLE = 30.0f;
    public static final float GRID_HEIGHT = DimensionHelper.getGridHeight(CAMERA_ANGLE, GRID_WIDTH);
    public static final float STANDARD_SCENE_WIDTH = 30.0f;
    public static final Position STANDARD_GRID_SIZE = new Position(10, 10);

    //MOVEMENTSYSTEM
    public static final float VELOCITY_WALKING = 0.05f;
    public static final float VELOCITY_RUNNING = 3.0f;

    // File-paths and stuff:
    public static final String PACKAGE_FOLDER = "content_packages/offline_packages/";
    public static final String ONLINE_FOLDER = "content_packages/downloaded_packages/";
    public static final String STD_PACKAGE = "lands_of_cinder";

    public static final String LEVELS_PATH = "levels/";
    public static final String AVAILABLE_SCENES_PATH = "availablescenes/";

    public static final String LISTS_PATH = "lists/";
    public static final String ITEM_LIST_PATH = LISTS_PATH + "items/itemlist.xml";
    public static final String CONSUMABLE_LIST_PATH = LISTS_PATH + "items/consumablelist.xml";
    public static final String EQUIPPABLE_LIST_PATH = LISTS_PATH + "items/equippablelist.xml";
    public static final String OBJECT_LIST_PATH = LISTS_PATH + "objects/objectlist.xml";
    public static final String COMMENTARY_OBJECT_LIST_PATH = "lists/objects/commentaryobjectlist.xml";
    public static final String MOB_LIST_PATH = LISTS_PATH + "mobs/moblist.xml";
    public static final String PLAYER_LIST_PATH = LISTS_PATH + "player/playerlist.xml";
    public static final String STD_SOUND_LIST_PATH = LISTS_PATH + "sounds/standardsoundlist.xml";
    public static final String SKILL_LIST_PATH = LISTS_PATH + "skills/skilllist.xml";
    public static final String EMPTY_LIST_PATH = LISTS_PATH + "empties/emptylist.xml";
    public static final String MUSIC_LIST_PATH = LISTS_PATH + "sounds/musiclist.xml";
    public static final String CHEST_LIST_PATH = LISTS_PATH + "items/chest_list.xml";
    public static final String TILE_LIST_PATH = LISTS_PATH + "tiles/tile_list.xml";
    public static final String COOKIE_PATH = "cookies/session.cookie";
    public static final String USER_ID_PATH = "cookies/userid";

    public static final String TWO_D_PATH = "2d/";
    public static final String ICON_PATH = TWO_D_PATH + "icons/";
    public static final String EMPTIES_ICON_PATH = ICON_PATH + "empties/";
    public static final String ITEMS_ICON_PATH = TWO_D_PATH + "icons/items/";
    public static final String BACKGROUNDS_PATH = TWO_D_PATH + "backgrounds/";
    public static final String HEIGHTMAP_PATH = TWO_D_PATH + "heightmaps/";

    public static final String EDITOR_SAVES_PATH = "saves/editor/";
    public static final String GAME_SAVES_PATH = "saves/game/";

    public static final String THREE_D_PATH = "3d/";
    public static final String MODELS_PATH = THREE_D_PATH + "models/";
    public static final String PARTICLES_PATH = THREE_D_PATH + "partikel/";
    public static final String SHADER_PATH = THREE_D_PATH + "shader/";

    public static final String SOUND_PATH = "sounds/";
    public static final String MUSIC_PATH = "music/";
    public static final String ITEMS_MODEL_PATH = MODELS_PATH + "truhe/truhe.g3db";
    public static final String GAME_TMP_DIRECTORY = "saves/tmp/";
    public static final String DIALOG_PATH = "dialogs/";

    public static final String UI_PATH = "ui/";
    public static final String UI_DATA_PATH = UI_PATH + "data/";
    public static final String UI_ICONS_PATH = UI_PATH + "icons/";
    public static final String UI_FONTS_PATH = UI_PATH + "fonts/";
    public static final String UI_ICON_BACK = UI_ICONS_PATH + "arrow48.png";
    public static final String UI_ICON_PLUS = UI_ICONS_PATH + "add_circle_outline.png";
    public static final String UI_ICON_MINUS = UI_ICONS_PATH + "remove_circle_outline.png";
    public static final String UI_ICON_BRUSH = UI_ICONS_PATH + "ic_format_paint_white_48dp.png";

    //NinePatches
    public static final String UI_NINEPATCHES_PATH = UI_PATH + "ninepatches/";
    public static final String UI_NINEPATCH_PAPER_BRIGHT = UI_NINEPATCHES_PATH + "papier.9.png";
    public static final String UI_NINEPATCH_PAPER_DARK = UI_NINEPATCHES_PATH + "papier_dunkel.png";
    public static final String UI_NINEPATCH_GRAY = UI_NINEPATCHES_PATH + "gray.png";
    public static final String UI_NINEPATCH_EDITORMENU = UI_NINEPATCHES_PATH + "editormenu.png";
    public static final String UI_NINEPATCH_QUICKSLOT_ACTIVE = UI_NINEPATCHES_PATH + "quickslot_active.png";
    public static final String UI_NINEPATCH_QUICKSLOT_INACTIVE = UI_NINEPATCHES_PATH + "quickslot.png";
    public static final String UI_NINEPATCH_FLAG_ACTIVE = UI_NINEPATCHES_PATH + "flag_active.png";
    public static final String UI_NINEPATCH_FLAG_INACTIVE = UI_NINEPATCHES_PATH + "flag_inactive.png";
    public static final String UI_NINEPATCH_EMPTYITEM = UI_NINEPATCHES_PATH + "empty_item.png";
    public static final String UI_NINEPATCH_MOUSEOVER = UI_NINEPATCHES_PATH + "mouseover.png";

    //Icons
    public static final String UI_ICON_PLAYER = UI_ICONS_PATH + "player.png";
    public static final String UI_ICON_NPCS = UI_ICONS_PATH + "npcs.png";
    public static final String UI_ICON_MOBS = UI_ICONS_PATH + "mobs.png";
    public static final String UI_ICON_ITEMS = UI_ICONS_PATH + "items.png";
    public static final String UI_ICON_CONSUMABLES = UI_ICONS_PATH + "consumables.png";
    public static final String UI_ICON_EQUIPPABLES = UI_ICONS_PATH + "equippables.png";
    public static final String UI_ICON_OBJECTS = UI_ICONS_PATH + "objects.png";
    public static final String UI_ICON_CHEST = UI_ICONS_PATH + "chest.png";
    public static final String UI_ICON_EMPTIES = UI_ICONS_PATH + "empties.png";
    public static final String UI_ICON_QUESTS = UI_ICONS_PATH + "quests.png";
    public static final String UI_ICON_DIALOGS = UI_ICONS_PATH + "dialogs.png";
    public static final String UI_ICON_SETTINGS = UI_ICONS_PATH + "settings.png";
    public static final String UI_ICON_MAINMENU = UI_ICONS_PATH + "mainmenu.png";
    public static final String UI_ICON_EDITORMENU = UI_ICONS_PATH + "editormenu.png";
    public static final String UI_ICON_INVENTAR = UI_ICONS_PATH + "inventar.png";
    public static final String UI_ICON_ONLINE = UI_ICONS_PATH + "online.png";
    public static final String UI_ICON_DOWNLOAD = UI_ICONS_PATH + "download.png";
    public static final String UI_ICON_UPLOAD = UI_ICONS_PATH + "upload.png";

    //UI
    public static final float WIDTH = (float) Gdx.graphics.getWidth();
    public static final float HEIGHT = (float) Gdx.graphics.getHeight();

    public static final float UI_RATIO = 0.2f;
    public static final float UI_RATIO_I = 1.0f - UI_RATIO;
    public static final float UI_IMAGE_SIZE = 50.0f;

    public static final int UI_LEFT_RIGHT_AMOUNT = 9;
    public static final int UI_TOP_BOTTOM_AMOUNT = 5;
    public static final int UI_MIDDLE_AMOUNT = 4;
    public static final int UI_ICON_SIZE = 10;

    //Breite & Höhe für MenuTable STANDARD
    public static final float UI_MENU_BUTTON_WIDTH = WIDTH * UI_RATIO * 2.0f;
    public static final float UI_MENU_BUTTON_HEIGHT = HEIGHT * UI_RATIO;

    //Breite & Höhe für MenuTable CHOOSEWORLD
    public static final int UI_MENU_CHOOSE_AMOUNT_HOR = 4;
    public static final int UI_MENU_CHOOSE_AMOUNT_VER = 3;
    public static final float UI_MENU_CHOOSE_WIDTH = WIDTH / (float) UI_MENU_CHOOSE_AMOUNT_HOR;
    public static final float UI_MENU_CHOOSE_HEIGHT = HEIGHT / (float) UI_MENU_CHOOSE_AMOUNT_VER;

    //Breite & Höhe für MenuWindow LEFT RIGHT
    public static final float UI_LEFT_RIGHT_WIDTH = WIDTH * UI_RATIO;
    public static final float UI_LEFT_RIGHT_HEIGHT = HEIGHT;
    public static final float UI_LEFT_X = 0.0f;
    public static final float UI_RIGHT_X = WIDTH - (WIDTH * UI_RATIO);
    public static final float UI_LEFT_RIGHT_Y = 0.0f;

    //Breite & Höhe für MenuWindow MIDDLE
    public static final float UI_MIDDLE_WIDTH = WIDTH - (WIDTH * UI_RATIO * 2.0f);
    public static final float UI_MIDDLE_HEIGHT = HEIGHT;
    public static final float UI_MIDDLE_X = WIDTH * UI_RATIO;
    public static final float UI_MIDDLE_Y = 0.0f;

    //Breite & Höhe für MenuWindow TOP BOTTOM
    public static final float UI_TOP_BOTTOM_WIDTH = WIDTH;
    public static final float UI_TOP_BOTTOM_HEIGHT = HEIGHT * UI_RATIO;
    public static final float UI_TOP_BOTTOM_X = 0.0f;
    public static final float UI_TOP_BOTTOM_RIGHT = WIDTH - (WIDTH / 1.2f);
    public static final float UI_TOP_Y = HEIGHT - (HEIGHT * UI_RATIO);
    public static final float UI_BOTTOM_Y = 0.0f;

    //Breite & Höhe für COMBATBASE, COMBATACTION, HEALTHBAR, STEAMMETER
    //TODO philipp keine magic numbers! Aber ich liebe Magic Numbers :D

    //Breite & Höhe für addTwoButtonMenu
    public static final float UI_WINDOW_WIDTH = WIDTH * UI_RATIO_I;
    public static final float UI_WINDOW_HEIGHT = HEIGHT * UI_RATIO_I;
    public static final float UI_WINDOW_X = WIDTH * (UI_RATIO / 2.0f);
    public static final float UI_WINDOW_Y = HEIGHT * (UI_RATIO / 2.0f);

    public static final boolean SHOW_FPS = false;
    public static final boolean LOG_EVENTS = true;

    // Tag um bei einem ADD_DIALOG Event den Questgeber darzustellen
    public static final String CLIENT_NAME = "CLIENT_NAME";

    public static final String NAME_OF_CURRENCY = "Gold";

    //CombatSystem
    public static final int MAX_ENEMIES = 4;

    //Größe für die SkillWindow
    public static final int SKILL_IMAGE_SIZE = (int) WIDTH / 14;
    public static final int SKILL_IMAGE_SPACE = (int) HEIGHT / 100;
    public static final int SKILL_IMAGE_TOP = (int) HEIGHT - SKILL_IMAGE_SIZE - SKILL_IMAGE_SPACE;

    public static final int AGGRO_RANGE = 5;

}
