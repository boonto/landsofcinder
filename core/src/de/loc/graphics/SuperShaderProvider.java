package de.loc.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

import de.loc.tools.Constants;

public class SuperShaderProvider extends DefaultShaderProvider {

    private static final String VERTEX_SHADER = Constants.SHADER_PATH + "shadow3d.vert";
    private static final String FRAGMENT_SHADER = Constants.SHADER_PATH + "shadow3d.frag";

    @Override
    protected Shader createShader(Renderable renderable) {
        DefaultShader.Config config = new DefaultShader.Config();
        config.vertexShader = Gdx.files.internal(VERTEX_SHADER).readString();
        config.fragmentShader = Gdx.files.internal(FRAGMENT_SHADER).readString();

        return new CustomShader(renderable, config);
    }
}
