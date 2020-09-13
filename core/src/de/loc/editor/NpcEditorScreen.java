package de.loc.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;

import de.loc.core.LevelManager;
import de.loc.input.userinterface.BaseScreen;
import de.loc.input.userinterface.UserInterface;
import de.loc.main.LandsOfCinder;
import de.loc.tools.Constants;
import de.loc.tools.Helper;

public class NpcEditorScreen extends BaseScreen {

    private FileHandle file;
    private final EditorScreen editor;
    private XmlWriter xmlWriter;
    private String currentPackage;
    private String currentLevel;

    private SpriteBatch batch;
    private Label informationLog;

    private Table mainTable;
    private Table npcEditorTable;
    private Table npcTable;
    private Table npcImageTable;
    private Table menuTable;

    protected Npc npc;
    public Array<Model> allModels = new Array<>();
    private final Array<String> allModelNames = new Array<>();
    private Button modelImage;

    public NpcEditorScreen(LandsOfCinder game, EditorScreen editor) {
        super(game);
        this.editor = editor;
        this.setupNpcEditor();
    }

    private void setupNpcEditor() {
        this.currentPackage = LevelManager.getInstance().getCurrentPackage();
        this.currentLevel = LevelManager.getInstance().getCurrentLevelFileName();

        this.npc = new Npc();
        this.npc.model = new Model();
        this.batch = new SpriteBatch();

        this.setUpArrays();
        this.setUpUserInterface();

        this.ui.getMainTable().setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));
    }

    private void setUpArrays() {
        //TODO: Wird noch schöner!
        this.allModelNames.add("Select Model");

        Model alterMann = new Model();
        alterMann.modelName = "Alter Mann";
        alterMann.modelPath = "altermann/altermanngelb.g3db";
        alterMann.modelIcon = "npc/gesichtaltermanngruen_icon.png";
        this.allModels.add(alterMann);
        this.allModelNames.add(alterMann.modelName);

        Model mannMitHut = new Model();
        mannMitHut.modelName = "Mann mit Hut";
        mannMitHut.modelPath = "hutmann/hutmann.g3db";
        mannMitHut.modelIcon = "npc/gesichthutmann_icon.png";
        this.allModels.add(mannMitHut);
        this.allModelNames.add(mannMitHut.modelName);
    }

    private void setUpUserInterface() {
        this.inputHandler = new NpcEditorInputHandler(this, this.editor);
        this.ui = new UserInterface(this.inputHandler);
        this.ui.setScreenSize(18, Color.BLACK);

        this.setupMainTable();
        this.setupNpcEditorTable();
        this.setupNpcTable();
        this.setupNpcImageTable();
        this.setupNpcMenuTable();
    }

    private void setupMainTable() {
        this.mainTable = this.ui.getMainTable();
        this.mainTable.setFillParent(true);
    }

    private void setupNpcEditorTable() {
        this.npcEditorTable = new Table();
        this.npcEditorTable.pad(10.0f, 10.0f, 10.0f, 10.0f);

        this.mainTable.add(this.npcEditorTable).expand().top().row();
    }

    private void setupNpcTable() {
        this.npcTable = new Table();
        this.npcTable.pad(10.0f, 10.0f, 10.0f, 10.0f);

        //NPC Name
        Label labelNpcName = this.ui.addLabel("Name: ");
        TextField chooseNpcName = this.ui.addTextField("");
        this.npcTable.add(labelNpcName).expandX().left().row();
        this.npcTable.add(chooseNpcName).expandX().prefWidth(999.0f).row();

        //NPC HealthPoints
        Label labelNpcHealth = this.ui.addLabel("Health Points: ");
        TextField chooseNpcHealth = this.ui.addTextField("");
        this.npcTable.add(labelNpcHealth).expandX().left().row();
        this.npcTable.add(chooseNpcHealth).expandX().prefWidth(999.0f).row();

        //NPC Dialog
        Label labelDialog = this.ui.addLabel("Dialog: ");
        TextField chooseDialog = this.ui.addTextField("");
        this.npcTable.add(labelDialog).expandX().left().row();
        this.npcTable.add(chooseDialog).expandX().prefWidth(999.0f).row();

        //NPC Model
        SelectBox chooseNpcModel = this.ui.addSelectBox(this.allModelNames, "show_model");
        this.npcTable.add(chooseNpcModel).expandX().prefWidth(999.0f).row();

        this.npcEditorTable.add(this.npcTable).expand().top().left().row();

        this.npc.name = chooseNpcName;
        this.npc.dialog = chooseDialog;
        this.npc.healthpoints = chooseNpcHealth;
    }

    private void setupNpcImageTable() {
        this.npcImageTable = new Table();
        this.npcImageTable.pad(10.0f, 10.0f, 10.0f, 10.0f);

        //NPC Model Image
        this.modelImage = this.ui.addButton(Constants.ICON_PATH + "keinicon_icon.png", 0, 0, "model_clicked");
        this.npcImageTable.add(this.modelImage).row();

        this.npcEditorTable.add(this.npcImageTable).expand().top().row();
    }

    private void setupNpcMenuTable() {
        this.menuTable = new Table();
        this.menuTable.pad(10.0f, 10.0f, 10.0f, 10.0f);

        //Back to Editor
        Button buttonBackToEditor = this.ui.addTextButton("Back to Editor", 0.0f, 0.0f, "back_to_editor");
        this.menuTable.add(buttonBackToEditor);

        //Information Log
        this.informationLog = this.ui.addLabel("Hallo! Hier gibt es Informationen!");
        this.informationLog.setAlignment(Helper.Alignment.CENTER);
        this.menuTable.add(this.informationLog).expand().prefWidth(999.0f);

        //Save NPC
        Button saveNpc = this.ui.addTextButton("Save NPC", 0.0f, 0.0f, "save_npc");
        saveNpc.setWidth(150.0f);
        this.menuTable.add(saveNpc).expandX();

        this.mainTable.add(this.menuTable).expand().bottom().row();
    }

    public void saveNpc() {
        StringWriter writer = new StringWriter();
        this.xmlWriter = new XmlWriter(writer);

        if ( this.checkData() ) {
            try {
                this.xmlWriter.element("Npc");

                this.xmlWriter.element("Name").text(this.npc.name.getText()).pop();
                this.xmlWriter.element("ModelPath").text(this.npc.model.modelPath).pop();
                this.xmlWriter.element("Icon").text(this.npc.model.modelIcon).pop();
                this.xmlWriter.element("Dialog").text(this.npc.dialog.getText()).pop();
                this.xmlWriter.element("HP").text(this.npc.healthpoints.getText()).pop();

                this.xmlWriter.close();
            } catch ( IOException e ) {
                //setLogText("Error: Please check your data!");
                this.setLogText("Fehler: Bitte kontrolliere deine Eingaben.");
                e.printStackTrace();
            }

            this.file =
                Gdx.files.local(Constants.PACKAGE_FOLDER
                                + this.currentPackage
                                + "/npcs/"
                                + this.npc.name.getText().replace(" ", "_").toLowerCase()
                                + "/"
                                + this.npc.name.getText().replace(" ", "_")
                                + ".xml");
            this.file.writeString(writer.toString(), false);

            this.setLogText("NPC gespeichert!");
        }
    }

    private boolean checkData() {
        //Check if the npc has a name
        if ( this.npc.name.getText().isEmpty() ) {
            //setLogText("Please choose a name for your npc!");
            this.setLogText("Bitte wähle einen Namen für den NPC.");
            return false;
        }

        if ( this.npc.model.modelPath == null ) {
            this.setLogText("Bitte wähle ein Model für den NPC.");
            return false;
        }

        //Check if inserted values are valid
        try {
            int num = Integer.parseInt(this.npc.healthpoints.getText());
            if ( num < 1 ) {
                //setLogText("Invalid number! Check your values!");
                this.setLogText("Ungültige Healthpoints! Bitte kontrolliere deine Eingaben!");
                return false;
            }
        } catch ( NumberFormatException e ) {
            //setLogText("Invalid number! Check your values!");
            this.setLogText("Ungültige Healthpoints! Bitte kontrolliere deine Eingaben!");
            return false;
        }

        return true;
    }

    public void setLogText(String text) {
        this.informationLog.clear();
        this.informationLog.setText(text);
    }

    public void setModelImage(String path) {
        this.modelImage = this.ui.addButton(path, 0, 0, "model_clicked");
        this.npcImageTable.clear();
        this.npcImageTable.add(this.modelImage).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.batch.begin();
        this.npcTable.draw(this.batch, 1.0f);
        this.batch.end();

        super.render(delta);
    }

    public class Npc {
        TextField name;
        TextField dialog;
        TextField healthpoints;
        Model model;
    }

    public class Model {
        String modelName;
        String modelPath;
        String modelIcon;
    }
}