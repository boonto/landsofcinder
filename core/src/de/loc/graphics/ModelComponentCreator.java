package de.loc.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import de.loc.core.GameGrid;
import de.loc.core.LevelManager;
import de.loc.physics.PhysicsGenerator;
import de.loc.tools.Constants;
import de.loc.tools.DimensionHelper;
import de.loc.tools.Position;

public final class ModelComponentCreator {

    private final AssetManager assetManager;
    private final ModelBuilder modelBuilder;

    private Material gridMaterial;
    private Material gridMaterial2;
    private Material gridMaterial3;

    private Material fieldMaterialObstructed;
    private Material fieldMaterialOccupied;
    private Material boundMaterial;

    public ParticleSystem particleSystem;
    private final ParticleEffectLoader.ParticleEffectLoadParameter loadParam;
    private final Array<ParticleEffect> currentEffects;

    private static final class ModelComponentCreatorHolder {
        private static final ModelComponentCreator modelComponentCreator = new ModelComponentCreator();
    }

    public static ModelComponentCreator getInstance() {
        return ModelComponentCreatorHolder.modelComponentCreator;
    }

    private ModelComponentCreator() {
        this.assetManager = new AssetManager();
        this.modelBuilder = new ModelBuilder();
        this.currentEffects = new Array<>(5);

        this.particleSystem = new ParticleSystem();
        this.loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(this.particleSystem.getBatches());
    }

    public ModelComponent createModelComponent(String path) {
        Model model;

        if ( this.assetManager.isLoaded(path) ) {
            model = this.assetManager.get(path, Model.class);
        } else {
            this.assetManager.load(path, Model.class);
            while ( !this.assetManager.update() ) {
                // lalala
            }
            model = this.assetManager.get(path, Model.class);
        }

        ModelInstance instance = new ModelInstance(model);

        for ( Material m : instance.materials ) {
            m.remove(ColorAttribute.Specular);
        }

        ModelComponent component = new ModelComponent(instance);
        component.modelPath = path;
        return component;

    }

    public ModelComponent createGrid(GameGrid gameGrid, Position gridSize) {

        ModelInstance fieldModel;

        if ( this.gridMaterial == null ) {
            this.gridMaterial = new Material();
            this.gridMaterial.set(ColorAttribute.createDiffuse(0.0f, 0.0f, 0.0f, 1.0f));
            this.gridMaterial.set(new BlendingAttribute());
        }

        if ( this.gridMaterial2 == null ) {
            this.gridMaterial2 = new Material();
            this.gridMaterial2.set(ColorAttribute.createDiffuse(0.2f, 0.4f, 0.1f, 0.15f));
            this.gridMaterial2.set(new BlendingAttribute());
        }
        if ( this.gridMaterial3 == null ) {
            this.gridMaterial3 = new Material();
            this.gridMaterial3.set(ColorAttribute.createDiffuse(0.3f, 0.7f, 0.15f, 0.15f));
            this.gridMaterial3.set(new BlendingAttribute());
        }

        // Transparente Polygone der HeightCubes
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        MeshPartBuilder pb = mb.part("grid", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, this.gridMaterial2);
        Vector3 startXZ = new Vector3(-(((float) gridSize.x * Constants.GRID_WIDTH) / 2.0f), 0.0f, -(((float) gridSize.y * Constants.GRID_WIDTH) / 2.0f));
        for ( int x = 0; x < gridSize.x; x++ ) {
            for ( int z = 0; z < gridSize.y; z++ ) {
                float y = gameGrid.getHeightF(x, z);
                // Wenn die höhe 0 ist braucht man keinen heightcube
                if ( y != 0.0f ) {
                    Vector3 vec = new Vector3(startXZ.x + (Constants.GRID_WIDTH * (float) x), y, startXZ.z + (Constants.GRID_WIDTH * (float) z));

                    ModelComponentCreator.createHeightCube(pb, vec);
                }

            }
        }
        // Wireframe des HeightCubes
        MeshPartBuilder pb2 = mb.part("grid_wireframe", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, this.gridMaterial);
        for ( int x = 0; x < gridSize.x; x++ ) {
            for ( int z = 0; z < gridSize.y; z++ ) {
                float y = gameGrid.getHeightF(x, z);
                // Wenn die höhe 0 ist braucht man keinen heightcube
                // allerdings braucht man für das obere Polygon eine wireframe umrandung
                // (sieht einfach schoener aus)
                if ( y == 0.0f ) {
                    Vector3 vec = new Vector3(startXZ.x + (Constants.GRID_WIDTH * (float) x), 0.0f, startXZ.z + (Constants.GRID_WIDTH * (float) z));

                    pb2.rect(vec,
                             new Vector3(vec.x, vec.y, vec.z + (Constants.GRID_WIDTH)),
                             new Vector3(vec.x + Constants.GRID_WIDTH, vec.y, vec.z + (Constants.GRID_WIDTH)),
                             new Vector3(vec.x + Constants.GRID_WIDTH, vec.y, vec.z),
                             new Vector3(0.0f, 1.0f, 0.0f));
                } else {
                    Vector3 vec = new Vector3(startXZ.x + (Constants.GRID_WIDTH * (float) x), y, startXZ.z + (Constants.GRID_WIDTH * (float) z));

                    ModelComponentCreator.createHeightCube(pb2, vec);
                }
            }
        }
        //Oberes Gridfeld
        MeshPartBuilder pb3 = mb.part("grid_top", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, this.gridMaterial3);
        for ( int x = 0; x < gridSize.x; x++ ) {
            for ( int z = 0; z < gridSize.y; z++ ) {
                float y = gameGrid.getHeightF(x, z);
                Vector3 vec = new Vector3(startXZ.x + (Constants.GRID_WIDTH * (float) x), y, startXZ.z + (Constants.GRID_WIDTH * (float) z));

                pb3.rect(vec,
                         new Vector3(vec.x, vec.y, vec.z + (Constants.GRID_WIDTH)),
                         new Vector3(vec.x + Constants.GRID_WIDTH, vec.y, vec.z + (Constants.GRID_WIDTH)),
                         new Vector3(vec.x + Constants.GRID_WIDTH, vec.y, vec.z),
                         new Vector3(0.0f, 1.0f, 0.0f));

            }
        }
        Model m = mb.end();
        fieldModel = new ModelInstance(m);

        return new ModelComponent(fieldModel);
    }

    private static void createHeightCube(MeshPartBuilder pb, Vector3 vec) {

        Vector3 vec2 = new Vector3(vec.x, vec.y, vec.z + (Constants.GRID_WIDTH));
        Vector3 vec3 = new Vector3(vec.x + Constants.GRID_WIDTH, vec.y, vec.z + (Constants.GRID_WIDTH));
        Vector3 vec4 = new Vector3(vec.x + Constants.GRID_WIDTH, vec.y, vec.z);

        //Süd
        pb.rect(vec3, new Vector3(vec3.x, 0.0f, vec3.z), new Vector3(vec4.x, 0.0f, vec4.z), vec4, new Vector3(-1.0f, 0.0f, 0.0f));

        //West
        pb.rect(vec2, new Vector3(vec2.x, 0.0f, vec2.z), new Vector3(vec3.x, 0.0f, vec3.z), vec3, new Vector3(0.0f, 0.0f, -1.0f));
    }

    public ModelComponent createEmpty(String type) {

        if ( this.boundMaterial == null ) {
            this.boundMaterial = new Material();
            this.boundMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            this.boundMaterial.set(ColorAttribute.createDiffuse(0.3f, 0.8f, 1.0f, 0.5f));
        }

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        MeshPartBuilder pb = mb.part("quads",
                                     GL20.GL_TRIANGLES,
                                     VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal,
                                     this.boundMaterial);

        BoxShapeBuilder.build(pb, -1.0f, -0.5f, -1.0f, 1.0f, 1.0f, 1.0f);

        Model model = mb.end();

        ModelInstance backgroundModel = new ModelInstance(model);

        ModelComponent modelC = new ModelComponent(backgroundModel);
        modelC.modelPath = "DEBUG";

        return modelC;

    }

    public ModelComponent createBackground(String backgroundPath, float width) {

        Texture backgroundTexture;

        if ( this.assetManager.isLoaded(Constants.BACKGROUNDS_PATH + backgroundPath) ) {

            backgroundTexture = this.assetManager.get(Constants.BACKGROUNDS_PATH + backgroundPath, Texture.class);
        } else {
            this.assetManager.load(Constants.BACKGROUNDS_PATH + backgroundPath, Texture.class);
            while ( !this.assetManager.update() ) {
                // lalala
            }
            backgroundTexture = this.assetManager.get(Constants.BACKGROUNDS_PATH + backgroundPath, Texture.class);
        }

        float ratio = (float) (backgroundTexture.getHeight()) / (float) backgroundTexture.getWidth();

        Vector2 sceneSize = new Vector2(width, width * ratio);
        LevelManager.getInstance().getLevel().setBackgroundSize(sceneSize);

        Material backgroundMaterial = new Material();
        backgroundMaterial.set(TextureAttribute.createDiffuse(backgroundTexture));

        float zco = sceneSize.y / 2.0f;
        float xco = (sceneSize.x / 2.0f);

        Model model = this.modelBuilder.createRect(-xco,
                                                   0.0f,
                                                   zco,
                                                   xco,
                                                   0.0f,
                                                   zco,
                                                   xco,
                                                   0.0f,
                                                   -zco,
                                                   -xco,
                                                   0.0f,
                                                   -zco,
                                                   0.0f,
                                                   1.0f,
                                                   0.0f,
                                                   backgroundMaterial,
                                                   VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);

        ModelInstance backgroundModel = new ModelInstance(model);

        Matrix4 transform = new Matrix4();
        transform.idt();
        transform.translate(-10.0f, -10.0f * DimensionHelper.getCameraHeight(Constants.CAMERA_ANGLE), -10.0f);
        transform.rotate(0.0f, 1.0f, 0.0f, 45.0f).rotate(1.0f, 0.0f, 0.0f, 90.0f - Constants.CAMERA_ANGLE);

        backgroundModel.transform.mul(transform);

        return new ModelComponent(backgroundModel);

    }

    public Model createModelFromBounds(BoundingBox bb) {
        Model m;
        if ( this.boundMaterial == null ) {
            this.boundMaterial = new Material();
            this.boundMaterial.set(ColorAttribute.createDiffuse(0.3f, 0.8f, 1.0f, 1.0f));
        }

        m =
            this.modelBuilder.createBox(bb.getWidth(),
                                        bb.getHeight(),
                                        bb.getDepth(),
                                        this.boundMaterial,
                                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return m;
    }

    public ModelComponent updatedGameGrid(GameGrid grid) {

        ModelInstance fieldModel;

        if ( this.fieldMaterialObstructed == null ) {
            this.fieldMaterialObstructed = new Material();
            this.fieldMaterialObstructed.set(ColorAttribute.createDiffuse(0.8f, 0.0f, 0.0f, 0.15f));
            this.fieldMaterialObstructed.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        }

        if ( this.fieldMaterialOccupied == null ) {
            this.fieldMaterialOccupied = new Material();
            this.fieldMaterialOccupied.set(ColorAttribute.createDiffuse(0.0f, 0.0f, 0.8f, 0.15f));
            this.fieldMaterialOccupied.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        }

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        MeshPartBuilder pb = mb.part("quads", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, this.fieldMaterialObstructed);

        Vector3
            startXZ =
            new Vector3(-(((float) grid.getSize().x * Constants.GRID_WIDTH) / 2.0f), 0.0f, -(((float) grid.getSize().y * Constants.GRID_WIDTH) / 2.0f));
        for ( int x = 0; x < grid.getSize().x; x++ ) {
            for ( int z = 0; z < grid.getSize().y; z++ ) {
                if ( grid.isObstructed(x, z) ) {
                    float y = LevelManager.getInstance().getLevel().getGameGrid().getHeightF(x, z);

                    Vector3 vec = new Vector3(startXZ.x + (Constants.GRID_WIDTH * (float) x), y, startXZ.z + (Constants.GRID_WIDTH * (float) z));

                    pb.rect(
                        vec,
                        new Vector3(vec.x, y, vec.z + (Constants.GRID_WIDTH)),
                        new Vector3(vec.x + Constants.GRID_WIDTH, y, vec.z + (Constants.GRID_WIDTH)),
                        new Vector3(vec.x + Constants.GRID_WIDTH, y, vec.z),
                        new Vector3(0.0f, 1.0f, 0.0f));
                }
            }
        }
        pb = mb.part("quads", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, this.fieldMaterialOccupied);
        for ( int x = 0; x < grid.getSize().x; x++ ) {
            for ( int z = 0; z < grid.getSize().y; z++ ) {
                if ( grid.isOccupied(x, z) ) {
                    float y = LevelManager.getInstance().getLevel().getGameGrid().getHeightF(x, z);

                    Vector3 vec = new Vector3(startXZ.x + (Constants.GRID_WIDTH * (float) x), y, startXZ.z + (Constants.GRID_WIDTH * (float) z));

                    pb.rect(vec,
                            new Vector3(vec.x, y, vec.z + (Constants.GRID_WIDTH)),
                            new Vector3(vec.x + Constants.GRID_WIDTH, y, vec.z + (Constants.GRID_WIDTH)),
                            new Vector3(vec.x + Constants.GRID_WIDTH, y, vec.z),
                            new Vector3(0.0f, 1.0f, 0.0f));
                }
            }
        }
        Model m = mb.end();
        fieldModel = new ModelInstance(m);

        return new ModelComponent(fieldModel);
    }

    public void createParticle(String path, Vector3 position) {
        try {
            if ( !this.assetManager.isLoaded(Constants.PARTICLES_PATH + path) ) {
                this.assetManager.load(Constants.PARTICLES_PATH + path, ParticleEffect.class, this.loadParam);
                while ( !this.assetManager.update() ) {
                }
                this.assetManager.finishLoading();
            }

            ParticleEffect originalEffect = this.assetManager.get(Constants.PARTICLES_PATH + path);
            ParticleEffect effect = originalEffect.copy();
            this.currentEffects.add(effect);

            position.x -= (float) LevelManager.getInstance().getLevel().getGameGrid().getSize().x / 2.0f;
            position.z -= (float) LevelManager.getInstance().getLevel().getGameGrid().getSize().y / 2.0f;
            position.y += 0.5f;
            effect.getControllers().first().translate(position);

            effect.init();
            effect.start();
            this.particleSystem.add(effect);
        } catch ( RuntimeException e ) {
            Gdx.app.log("MODEL", "Es gibt Probleme beim Partikel laden!");
        }
    }

    public void renderParticles() {
        this.particleSystem.update();
        this.particleSystem.begin();
        this.particleSystem.draw();
        this.particleSystem.end();
    }

    public void dispose() {
        for ( ParticleEffect effect : this.currentEffects ) {
            effect.dispose();
        }
        PhysicsGenerator.getInstance().dispose(); //TODO willi schau dir das mal an
    }
}
