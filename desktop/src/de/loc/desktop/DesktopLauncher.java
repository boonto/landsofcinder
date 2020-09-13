package de.loc.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.loc.main.LandsOfCinder;
import de.loc.tools.Constants;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Lands of Cinder";

        config.width = Constants.APP_WIDTH;
        config.height = Constants.APP_HEIGHT;
        config.fullscreen = false;

        config.addIcon("ui/loc_icon.png", Files.FileType.Internal);

        new LwjglApplication(new LandsOfCinder(), config);
    }
}
