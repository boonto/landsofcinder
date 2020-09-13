package de.loc.movement.pathfinding;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import de.loc.core.LevelManager;
import de.loc.graphics.CameraManager;
import de.loc.tools.Constants;

public class PathfindingDebugger {
    private static final ModelBatch modelBatch = new ModelBatch();
    private static final ModelBuilder modelBuilder = new ModelBuilder();
    private static final Material material = new Material(new ColorAttribute(ColorAttribute.createDiffuse(0.0f, 0.0f, 1.0f, 1.0f)));
    private static final Model
        sphere =
        modelBuilder.createSphere(0.3f, 0.3f, 0.3f, 8, 8, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates);

    public static void drawPath(Array<Vector2> path) {
        modelBatch.dispose();
        Iterator<Vector2> pathIterator = path.iterator();
        Vector2 priorVector = null;

        while ( pathIterator.hasNext() ) {
            Vector2 vector = pathIterator.next();

            modelBatch.begin(CameraManager.getCamera());

            ModelInstance sphereInstance = new ModelInstance(sphere);
            sphereInstance.transform.setTranslation(LevelManager.getInstance().getLevel().getWorldFromPosF(vector));
            sphereInstance.transform.translate(Constants.GRID_WIDTH_HALF, 0.0f, Constants.GRID_WIDTH_HALF);
            modelBatch.render(sphereInstance);
            modelBatch.end();

            if ( priorVector != null ) {

                modelBatch.begin(CameraManager.getCamera());

                Vector3 pos1 = LevelManager.getInstance().getLevel().getWorldFromPosF(vector);
                Vector3 pos2 = LevelManager.getInstance().getLevel().getWorldFromPosF(priorVector);
                pos1 = pos1.add(Constants.GRID_WIDTH_HALF, 0.0f, Constants.GRID_WIDTH_HALF);
                pos2 = pos2.add(Constants.GRID_WIDTH_HALF, 0.0f, Constants.GRID_WIDTH_HALF);

                Model arrow = modelBuilder.createArrow(pos2.x, pos2.y, pos2.z, pos1.x, pos1.y, pos1.z, 0.2f, 0.2f,
                    8,
                    GL20.GL_TRIANGLES,
                    material,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates);
                ModelInstance arrowInstance = new ModelInstance(arrow);

                modelBatch.render(arrowInstance);
                modelBatch.end();
            }

            priorVector = vector;
        }
    }
}