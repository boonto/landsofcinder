package de.loc.quest;

import de.loc.editor.EditorScreen;
import de.loc.input.InputHandler;

public class QuestEditorInputHandler extends InputHandler {
    private final EditorScreen editor;
    private final QuestEditorScreen screen;

    public QuestEditorInputHandler(QuestEditorScreen screen, EditorScreen editor) {
        super(screen);
        this.screen = screen;
        this.editor = editor;
    }

    @Override
    public boolean handle(String handleID) {
        if ( handleID.equals("button_backToEditor") ) {
            this.screen.getGame().setScreen(this.editor);
            return true;
        }

        if ( handleID.equals("button_saveQuest") ) {
            this.screen.saveQuest();
            return true;
        }

        if ( handleID.startsWith("selectbox_questEventSelection") ) {
            String selectedEvent = handleID.substring(29);
            if ( !selectedEvent.equals(QuestEditorScreen.EVENT_SELECTION_HEADER) ) {
                this.screen.setupQuestEventTable(selectedEvent);
            }
            return true;
        }

        if ( handleID.startsWith("selectbox_speaker") ) {
            String selectedSpeaker = handleID.substring(17);
            this.screen.updateActiveDialogSelectBox(selectedSpeaker);
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
