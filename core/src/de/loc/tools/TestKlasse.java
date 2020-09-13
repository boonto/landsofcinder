package de.loc.tools;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class TestKlasse {

    public TestKlasse() {
        this.teste();
    }

    private void teste() {

        FileHandle dirHandle;

        Array<String> items = new Array<>();

        if ( Gdx.app.getType() == Application.ApplicationType.Android ) {
            dirHandle = Gdx.files.local("./assets/EditorSaves/");
        } else {
            dirHandle = Gdx.files.local("./bin/EditorSaves/");
        }

        for ( FileHandle entry : dirHandle.list(".xml") ) {
            items.add(entry.toString());
        }

        System.out.println(items);

        System.out.println();
    }
}

