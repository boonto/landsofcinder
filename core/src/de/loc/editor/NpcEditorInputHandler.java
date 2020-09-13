package de.loc.editor;

import de.loc.input.InputHandler;
import de.loc.tools.Constants;

public class NpcEditorInputHandler extends InputHandler {

    private final EditorScreen editor;
    private final NpcEditorScreen screen;

    public NpcEditorInputHandler(NpcEditorScreen screen, EditorScreen editor) {
        super(screen);
        this.screen = screen;
        this.editor = editor;
    }

    @Override
    public boolean handle(String handleID) {

        if ( handleID.startsWith("show_model") ) {
            String model = handleID.substring(10);

            for ( int i = 0; i < this.screen.allModels.size; i++ ) {
                if ( this.screen.allModels.get(i).modelName.equals(model) ) {
                    String modelPath = this.screen.allModels.get(i).modelPath;
                    String iconPath = this.screen.allModels.get(i).modelIcon;
                    this.screen.setModelImage(Constants.ICON_PATH + iconPath);

                    this.screen.npc.model.modelName = model;
                    this.screen.npc.model.modelPath = modelPath;
                    this.screen.npc.model.modelIcon = iconPath;
                }
            }
            return true;
        }

        if ( handleID.equals("back_to_editor") ) {
            this.screen.getGame().setScreen(this.editor);
            return true;
        }

        if ( handleID.equals("save_npc") ) {
            this.screen.saveNpc();
            return true;
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
}
