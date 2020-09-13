package de.loc.graphics;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

public class CustomShader extends DefaultShader {
    public CustomShader(Renderable renderable, Config config) {
        super(renderable, config);
    }

    @Override
    public void render(Renderable renderable) {
        ((CustomEffectData) renderable.userData).applyData(this.program, CustomEffectData.Effect.HIGHLIGHT, CustomEffectData.Effect.SHADOW);

        super.render(renderable);
    }
}
