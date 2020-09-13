package de.loc.graphics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import de.loc.core.GameGrid;
import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.tools.Constants;

public class RenderSystem extends LandsOfCinderSystem implements Disposable {

    //private static final String VERTEX_SHADER = Constants.SHADER_PATH + "default2d.vert";
    //private static final String FRAGMENT_SHADER = Constants.SHADER_PATH + "shadow2d.frag";
    private static final Color AMBIENT_LIGHT_COLOR = new Color(0.7f, 0.7f, 0.7f, 1.0f);
    private static final Color DIRECTIONAL_LIGHT_COLOR = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    private static final int DEPTH_MAP_RESOLUTION = 2048;

    protected static final Vector3 Y_AXIS = new Vector3(0.0f, 1.0f, 0.0f);
    private final DepthShaderProvider depthShaderProvider;
    private final SuperShaderProvider superShaderProvider;

    //private SpriteBatch spriteBatch;
    private final ModelBatch depthModelBatch;
    private final ModelBatch colorModelBatch;
    private final Environment environment;

    private final FrameBuffer fboDepth;
    //private FrameBuffer fboColor;
    //private TextureRegion fboRegionColor;

    private final IsoCamera lightCamera;
    private final DirectionalLight dirLight;
    private final ModelInstance groundModel;

    //private ShaderProgram default2DShader;

    protected ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> backgroundEntities;

    protected ComponentMapper<PositionComponent> positionMapper;
    protected ComponentMapper<ModelComponent> modelMapper;
    protected ComponentMapper<RotationComponent> rotationMapper;

    public RenderSystem() {
        //spriteBatch = new SpriteBatch();
        //String default2DVert = Gdx.files.internal(VERTEX_SHADER).readString();
        //String default2DFrag = Gdx.files.internal(FRAGMENT_SHADER).readString();
        //default2DShader = new ShaderProgram(default2DVert, default2DFrag);
        //spriteBatch.setShader(default2DShader);

        //macht die schatten besser, verstehe ich aber nicht ganz, eigentlich müsste es andersrum sein
        DepthShader.Config config = new DepthShader.Config();
        config.defaultCullFace = GL20.GL_BACK;

        this.depthShaderProvider = new DepthShaderProvider(config);
        this.superShaderProvider = new SuperShaderProvider();

        this.depthModelBatch = new ModelBatch(this.depthShaderProvider);
        this.colorModelBatch = new ModelBatch(this.superShaderProvider);
        this.environment = new Environment();

        this.fboDepth = new FrameBuffer(Pixmap.Format.RGBA8888, DEPTH_MAP_RESOLUTION, DEPTH_MAP_RESOLUTION, true);
        //fboColor = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        //fboRegionColor = new TextureRegion(fboColor.getColorBufferTexture());
        //fboRegionColor.flip(false, true);

        this.lightCamera = new IsoCamera();
        this.lightCamera.near = 1.0f;
        this.lightCamera.far = 50.0f;
        this.lightCamera.zoom = 6.0f;

        ModelBuilder mb = new ModelBuilder(); //TODO magic numbers rausnehmen
        Material material = new Material(ColorAttribute.createDiffuse(0.0f, 0.0f, 0.0f, 0.0f), new BlendingAttribute());
        this.groundModel = new ModelInstance(mb.createBox(
            500.0f * Constants.GRID_WIDTH,
            0.0000001f,
            500.0f * Constants.GRID_WIDTH,
            material,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        this.groundModel.transform.translate(0.0f, -0.01f, 0.0f);
        this.groundModel.userData = new CustomEffectData();

        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, AMBIENT_LIGHT_COLOR));
        this.dirLight = new DirectionalLight().set(DIRECTIONAL_LIGHT_COLOR, this.lightCamera.direction);
        this.environment.add(this.dirLight);

        this.positionMapper = ComponentMapper.getFor(PositionComponent.class);
        this.modelMapper = ComponentMapper.getFor(ModelComponent.class);
        this.rotationMapper = ComponentMapper.getFor(RotationComponent.class);

        PointSpriteParticleBatch pointSpriteBatch = new PointSpriteParticleBatch();
        pointSpriteBatch.setCamera(CameraManager.getCamera());
        ModelComponentCreator.getInstance().particleSystem.add(pointSpriteBatch);
        BillboardParticleBatch billboardPatch = new BillboardParticleBatch();
        billboardPatch.setCamera(CameraManager.getCamera());
        ModelComponentCreator.getInstance().particleSystem.add(billboardPatch);
    }

    public void setLightPosition(float x, float y, float z) {
        this.lightCamera.position.set(x, y, z);
        this.lightCamera.lookAt(0.0f, 0.0f, 0.0f);
        this.lightCamera.up.set(0.0f, 1.0f, 0.0f);
        this.lightCamera.update();
        this.dirLight.setDirection(this.lightCamera.direction);

        Matrix4 lightSpaceMatrix = this.lightCamera.projection.mul(this.lightCamera.view);
        Vector2 resolution = new Vector2((float) this.fboDepth.getWidth(), (float) this.fboDepth.getHeight());

        for ( Entity e : this.entities ) {
            CustomEffectData effects = (CustomEffectData) this.modelMapper.get(e).model.userData;
            effects.lightSpaceMatrix = lightSpaceMatrix;
            effects.depthMapHandle = this.fboDepth.getDepthBufferHandle();
            effects.resolution = resolution;
            effects.lightPos = this.lightCamera.position;
        }

        CustomEffectData effects = (CustomEffectData) this.groundModel.userData;
        effects.lightSpaceMatrix = lightSpaceMatrix;
        effects.depthMapHandle = this.fboDepth.getDepthBufferHandle();
        effects.resolution = resolution;
        effects.lightPos = this.lightCamera.position;
    }

    public Vector3 getLightPosition() {
        return this.lightCamera.position;
    }

    public void addedToEngine(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(PositionComponent.class, RenderableComponent.class, ModelComponent.class).get());
        this.backgroundEntities = engine.getEntitiesFor(Family.all(ModelComponent.class, BackgroundComponent.class, RenderableComponent.class).get());
    }

    private void renderBackgroundEntities(ModelBatch modelBatch, Environment environment) {
        for ( Entity entity : this.backgroundEntities ) {
            ModelComponent modelComponent = this.modelMapper.get(entity);

            modelBatch.render(modelComponent.model, environment);
        }
    }

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

            if ( environment != null ) {
                modelBatch.render(modelComponent.model, environment);
            } else {
                modelBatch.render(modelComponent.model);
            }
        }
    }

    private static void renderParticles(ModelBatch modelBatch) {
        if ( ModelComponentCreator.getInstance().particleSystem.getBatches() != null ) {
            ModelComponentCreator.getInstance().renderParticles();
            modelBatch.render(ModelComponentCreator.getInstance().particleSystem);
        }
    }

    @Override
    public void update(float deltaTime) {
        // DEPTH --------------------
        this.fboDepth.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        this.depthModelBatch.begin(this.lightCamera);

        this.renderEntities(this.depthModelBatch, null);
        this.depthModelBatch.render(this.groundModel);

        this.depthModelBatch.end();
        this.fboDepth.end();

        // COLOR ---------------------
        //fboColor.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        this.colorModelBatch.begin(CameraManager.getCamera());

        Gdx.gl20.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);

        this.renderBackgroundEntities(this.colorModelBatch, this.environment);
        this.renderEntities(this.colorModelBatch, this.environment);
        renderParticles(this.colorModelBatch);
        this.colorModelBatch.render(this.groundModel);

        this.colorModelBatch.end();
        //fboColor.end();

        // COMBINE ------------------
        //spriteBatch.begin();
        //spriteBatch.draw(fboRegionColor, 0.0f, 0.0f);
        //spriteBatch.end();
    }

    @Override
    public void dispose() {
        this.depthShaderProvider.dispose();
        this.superShaderProvider.dispose();
        //spriteBatch.dispose();
        this.depthModelBatch.dispose();
        this.colorModelBatch.dispose();
        this.fboDepth.dispose();
        //fboColor.dispose();
        this.groundModel.model.dispose();
        //default2DShader.dispose();
    }

    @Override
    public void reset() {
        Vector3 pos = LevelManager.getInstance().loadSettings.dirLightPos;
        this.setLightPosition(pos.x, pos.y, pos.z);

        IsoCamera cam = CameraManager.getCamera();
        cam.zoom = 1.0f;
    }
}
