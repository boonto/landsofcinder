package de.loc.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CustomEffectData {
    public Matrix4 lightSpaceMatrix;
    public int depthMapHandle;
    public Vector2 resolution;
    public Vector3 lightPos;

    public Vector3 highlightColor;

    public CustomEffectData() {
        this.lightSpaceMatrix = null;
        this.depthMapHandle = 0;
        this.resolution = null;
        this.lightPos = null;

        this.highlightColor = new Vector3(0.0f, 0.0f, 0.0f);
    }

    public void applyData(ShaderProgram shader, Effect... effects) {
        Set<Effect> set = new HashSet<Effect>(Arrays.asList(effects));

        if ( set.contains(Effect.SHADOW) && (this.lightSpaceMatrix != null && this.depthMapHandle != 0 && this.resolution != null && this.lightPos != null) ) {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 2);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, this.depthMapHandle);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

            //shader.setUniformMatrix("u_lightSpaceMatrix", lightSpaceMatrix);
            //shader.setUniformi("u_depthMap", 2);
            //shader.setUniformf("u_resolution", resolution.x, resolution.y);
            //shader.setUniformf("u_lightPos", lightPos.x, lightPos.y, lightPos.z);
        }

        if ( set.contains(Effect.HIGHLIGHT) ) {
            shader.setUniformf("u_highlightColor", this.highlightColor.x, this.highlightColor.y, this.highlightColor.z);
        }
    }

    public enum Effect {
        SHADOW,
        HIGHLIGHT
    }
}
