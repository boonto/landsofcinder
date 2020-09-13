package de.loc.graphics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

import de.loc.core.EmptyComponent;
import de.loc.core.GameGrid;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.tools.Constants;

public class GameRenderSystem extends RenderSystem {
    private ComponentMapper<EmptyComponent> emptyMapper;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.emptyMapper = ComponentMapper.getFor(EmptyComponent.class);
    }

    @Override
    protected void renderEntities(ModelBatch modelBatch, Environment environment) {
        GameGrid grid = LevelManager.getInstance().getLevel().getGameGrid();

        for ( Entity entity : this.entities ) {
            ModelComponent modelComponent = this.modelMapper.get(entity);
            PositionComponent positionComponent = this.positionMapper.get(entity);
            RotationComponent rotationComponent = this.rotationMapper.get(entity);

            Vector3 worldPos = LevelManager.getInstance().getLevel().getWorldFromPosF(positionComponent.positionF);

            //Objekte sollen in der mitte vom gridfield stehen
            worldPos.x += Constants.GRID_WIDTH_HALF;
            worldPos.z += Constants.GRID_WIDTH_HALF;

            if ( positionComponent.position.x < grid.getSize().x && positionComponent.position.y < grid.getSize().y && positionComponent.position.x > 0
                 && positionComponent.position.y > 0 ) {
                worldPos.y = grid.getHeightF(positionComponent.position);
            }

            //machts simpler
            if ( rotationComponent != null ) {
                modelComponent.model.transform.setToRotation(Y_AXIS, rotationComponent.angle);
            }
            // mul(positionComponent.afterPositionTransform): dient dazu, die Position im Editor fein zu justieren.
            // dadurch müssen Entities nicht mehr nur noch in der Mitte des Feldes stehen.
            modelComponent.model.transform.setTranslation(worldPos).mul(positionComponent.afterPositionTransform);

            if ( this.emptyMapper.get(entity) == null ) {
                if ( environment != null ) {
                    modelBatch.render(modelComponent.model, environment);
                    //                    PhysicsComponent p = entity.getComponent(PhysicsComponent.class);
                    //                    if (p != null)
                    //                    {
                    //                        p.boundingModel.userData = new CustomEffectData();
                    //                        p.boundingModel.transform.setTranslation(worldPos).mul(positionComponent.afterPositionTransform).mul(new Matrix4().setToTranslation(0,1,0));;
                    //                        modelBatch.render(p.boundingModel);
                    //                    }

                } else {
                    modelBatch.render(modelComponent.model);
                    //                    PhysicsComponent p = entity.getComponent(PhysicsComponent.class);
                    //                    if (p != null)
                    //                    {
                    //                        p.boundingModel.userData = new CustomEffectData();
                    //                        p.boundingModel.transform.setTranslation(worldPos).mul(positionComponent.afterPositionTransform).mul(new Matrix4().setToTranslation(0,1,0));
                    //                        modelBatch.render(p.boundingModel);
                    //                    }
                }
            }
        }
    }
}
