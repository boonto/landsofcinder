package de.loc.editor;

import de.loc.input.InputHandler;

public class DialogEditorInputHandler extends InputHandler {

    private final EditorScreen editor;
    private final DialogEditorScreen screen;

    public DialogEditorInputHandler(DialogEditorScreen screen, EditorScreen editor) {
        super(screen);
        this.screen = screen;
        this.editor = editor;
    }

    @Override
    public boolean handle(String handleID) {

        if ( handleID.equals("button_back") ) {
            if ( this.screen.launchedFromQuesteditor ) {
                this.screen.getGame().setScreen(this.screen.getQuestEditor());
            } else {
                this.screen.getGame().setScreen(this.editor);
            }
            return true;
        }

        if ( handleID.startsWith("selectbox_speaker") ) {
            String speaker = handleID.substring("selectbox_speaker".length());
            this.screen.setSpeaker(speaker);
            return true;
        }

        if ( handleID.equals("button_saveDialog") ) {
            this.screen.showSaveWindow();
            return true;
        }

        if ( handleID.equals("dialogeditor_window_back") ) {
            this.screen.hideWindow();
            return true;
        }

        if ( handleID.equals("dialogeditor_window_save") ) {
            this.screen.saveDialog();
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
