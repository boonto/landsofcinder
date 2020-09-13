package de.loc.input.userinterface;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

public class LocTextArea extends TextArea {

    public LocTextArea(String text, Skin skin) {
        super(text, skin);
    }

    @Override
    protected int[] wordUnderCursor(int at) {
        String text = this.text;
        int start = Math.min(text.length(), at), right = text.length(), left = 0, index = start;
        for ( ; index < right; index++ ) {
            if ( !this.isWordCharacter(text.charAt(index)) ) {
                right = index;
                break;
            }
        }
        for ( index = start - 1; index > -1; index-- ) {
            if ( !this.isWordCharacter(text.charAt(index)) ) {
                left = index + 1;
                break;
            }
        }
        return new int[] { left, right };
    }
}
