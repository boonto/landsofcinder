package de.loc.input.userinterface;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

import de.loc.input.InputHandler;
import de.loc.item.ItemComponent;
import de.loc.tools.Constants;

public class SkillTreeWindow extends SkillWindow {

    private ComponentMapper<ItemComponent> itemMapper;
    private final ArrayList<Button> TreePoints;
    private final int width;
    private final int startY;
    private final SkillTreeInputHandler skillTreeInputHandler;
    private Button Line;
    private Button LineActivated;
    private Button punch;
    private Button Line2;
    private Button Line2Activated;
    private Button Line3;
    private Button Line4;
    private Button Line3Activated;
    private Button Line4Activated;
    private boolean punchTree;

    public SkillTreeWindow(UserInterface ui) {
        super(ui, Type.BASE);
        this.mainTable = this;
        this.TreePoints = new ArrayList<Button>();
        float mainTableWidth = this.getWidth();
        float mainTableHeight = this.getHeight();
        float mainTableX = this.getX();
        float mainTableY = this.getY();

        this.startY = (int) (mainTableHeight - mainTableHeight / 4);
        this.width = (int) (mainTableWidth - mainTableX);
        System.out.println("SkillTree: " + this.width + ", " + mainTableHeight + ", " + mainTableY);

        this.skillTreeInputHandler = new SkillTreeInputHandler();
        this.punchTree = false;

    }

    public void buildSkillTreePoint(String decision) {

        int widthPosition = this.width / 3;
        //int a = 50;
        String path = Constants.UI_NINEPATCHES_PATH + "swordframe.9.png";
        //    	ArrayList<Button> buttons = new ArrayList<Button>();
        if ( decision.equals("punch") ) {
            this.punch =
                this.ui.addSkillButtonWithoutListener(path,
                                                      widthPosition - Constants.SKILL_IMAGE_SIZE / 2,
                                                      this.startY,
                                                      Constants.SKILL_IMAGE_SIZE,
                                                      Constants.SKILL_IMAGE_SIZE);
            this.addActor(this.punch);
            Button punch1 = this.ui.addSkillButtonWithoutListener(path,
                                                                  widthPosition - Constants.SKILL_IMAGE_SIZE / 2,
                                                                  this.startY / 2 + Constants.SKILL_IMAGE_SIZE / 2,
                                                                  Constants.SKILL_IMAGE_SIZE,
                                                                  Constants.SKILL_IMAGE_SIZE);
            this.addActor(punch1);
            Button punch2 = this.ui.addSkillButtonWithoutListener(path,
                                                                  widthPosition - Constants.SKILL_IMAGE_SIZE / 2,
                                                                  (int) this.getY() + Constants.SKILL_IMAGE_SIZE / 2,
                                                                  Constants.SKILL_IMAGE_SIZE,
                                                                  Constants.SKILL_IMAGE_SIZE);
            this.addActor(punch2);
            this.TreePoints.add(this.punch);
            this.TreePoints.add(punch1);
            this.TreePoints.add(punch2);
            this.addLine();

            this.punch.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    SkillTreeWindow.this.skillTreeInputHandler.handle("punch");
                }
            });
            punch1.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    SkillTreeWindow.this.skillTreeInputHandler.handle("punch1");
                }
            });

        } else if ( decision.equals("uppercut") ) {
            Button
                uppercut =
                this.ui.addSkillButtonWithoutListener(path, widthPosition * 2, this.startY, Constants.SKILL_IMAGE_SIZE, Constants.SKILL_IMAGE_SIZE);
            this.addActor(uppercut);
            Button
                uppercut2 =
                this.ui.addSkillButtonWithoutListener(path,
                                                      widthPosition * 2,
                                                      (int) this.getY() + Constants.SKILL_IMAGE_SIZE / 2,
                                                      Constants.SKILL_IMAGE_SIZE,
                                                      Constants.SKILL_IMAGE_SIZE);
            this.addActor(uppercut2);

            uppercut.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    SkillTreeWindow.this.skillTreeInputHandler.handle("uppercut");
                }
            });

        } else if ( decision.equals("steam") ) {
            Button
                steam =
                this.ui.addSkillButtonWithoutListener(path,
                                                      widthPosition * 3 + Constants.SKILL_IMAGE_SIZE / 2,
                                                      this.startY,
                                                      Constants.SKILL_IMAGE_SIZE,
                                                      Constants.SKILL_IMAGE_SIZE);
            this.addActor(steam);
            Button steam2 = this.ui.addSkillButtonWithoutListener(path,
                                                                  widthPosition * 3 + Constants.SKILL_IMAGE_SIZE / 2,
                                                                  this.startY / 2 + Constants.SKILL_IMAGE_SIZE / 2,
                                                                  Constants.SKILL_IMAGE_SIZE,
                                                                  Constants.SKILL_IMAGE_SIZE);
            this.addActor(steam2);

            steam.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    SkillTreeWindow.this.skillTreeInputHandler.handle("steam");
                }
            });
        } else {

        }

        TextField tf = new TextField("SkillTree", this.ui.getSkin());
        tf.setX(this.getWidth() / 2 - tf.getWidth() / 4);
        tf.setY(this.getHeight() - this.getHeight() / 13);
        this.addActor(tf);

    }

    private void addLine() {
        //Image Line = new Image(new NinePatchDrawable(ui.getNinePatch("UI/NinePatches/red.9.png")));
        int widthPosition = this.width / 3;
        this.Line = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "skilltree.png",
                                                          widthPosition - Constants.SKILL_IMAGE_SIZE / 4,
                                                          this.startY / 2 + Constants.SKILL_IMAGE_SIZE + Constants.SKILL_IMAGE_SIZE / 2,
                                                          Constants.SKILL_IMAGE_SIZE / 2,
                                                          140);
        this.Line2 = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "skilltree.png",
                                                           widthPosition - Constants.SKILL_IMAGE_SIZE / 4,
                                                           (int) this.getY() + Constants.SKILL_IMAGE_SIZE + Constants.SKILL_IMAGE_SIZE / 2,
                                                           Constants.SKILL_IMAGE_SIZE / 2,
                                                           175);
        this.Line3 = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "skilltree.png",
                                                           widthPosition * 2 + Constants.SKILL_IMAGE_SIZE / 4,
                                                           (int) this.getY() + Constants.SKILL_IMAGE_SIZE + Constants.SKILL_IMAGE_SIZE / 2,
                                                           Constants.SKILL_IMAGE_SIZE / 2,
                                                           385);
        this.Line4 = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "skilltree.png",
                                                           widthPosition * 3 + Constants.SKILL_IMAGE_SIZE / 2 + Constants.SKILL_IMAGE_SIZE / 4,
                                                           this.startY / 2 + Constants.SKILL_IMAGE_SIZE + Constants.SKILL_IMAGE_SIZE / 2,
                                                           Constants.SKILL_IMAGE_SIZE / 2,
                                                           140);

        this.addActor(this.Line);
        this.addActor(this.Line2);
        this.addActor(this.Line3);
        this.addActor(this.Line4);

        this.LineActivated = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "skilltreefilled.png",
                                                                   widthPosition - Constants.SKILL_IMAGE_SIZE / 4,
                                                                   this.startY / 2 + Constants.SKILL_IMAGE_SIZE + Constants.SKILL_IMAGE_SIZE / 2,
                                                                   Constants.SKILL_IMAGE_SIZE / 2,
                                                                   140);
        this.Line2Activated = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "skilltreefilled.png",
                                                                    widthPosition - Constants.SKILL_IMAGE_SIZE / 4,
                                                                    (int) this.getY() + Constants.SKILL_IMAGE_SIZE + Constants.SKILL_IMAGE_SIZE / 2,
                                                                    Constants.SKILL_IMAGE_SIZE / 2,
                                                                    175);
        this.Line3Activated = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "skilltreefilled.png",
                                                                    widthPosition * 2 + Constants.SKILL_IMAGE_SIZE / 4,
                                                                    (int) this.getY() + Constants.SKILL_IMAGE_SIZE + Constants.SKILL_IMAGE_SIZE / 2,
                                                                    Constants.SKILL_IMAGE_SIZE / 2,
                                                                    385);
        this.Line4Activated = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "skilltreefilled.png",
                                                                    widthPosition * 3 + Constants.SKILL_IMAGE_SIZE / 2 + Constants.SKILL_IMAGE_SIZE / 4,
                                                                    this.startY / 2 + Constants.SKILL_IMAGE_SIZE + Constants.SKILL_IMAGE_SIZE / 2,
                                                                    Constants.SKILL_IMAGE_SIZE / 2,
                                                                    140);

        this.LineActivated.setVisible(false);
        this.Line2Activated.setVisible(false);
        this.Line3Activated.setVisible(false);
        this.Line4Activated.setVisible(false);

        this.addActor(this.LineActivated);
        this.addActor(this.Line2Activated);
        this.addActor(this.Line3Activated);
        this.addActor(this.Line4Activated);

        Button LineTest = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "skilltreefilled.png", 50, 150, 100, 20);
        LineTest.setRotation(135);
        this.addActor(LineTest);

    }

    private class SkillTreeInputHandler extends InputHandler {

        SkillTreeInputHandler() {
        }

        @Override
        public boolean handle(String handleID) {
            //combat handles
            System.out.println("Test: " + handleID);
            if ( handleID.startsWith("combat_menu_") ) {

                return true;
            }

            if ( handleID.equals("punch") ) {

                SkillTreeWindow.this.Line.setVisible(false);
                SkillTreeWindow.this.LineActivated.setVisible(true);
                SkillTreeWindow.this.punchTree = true;

                return true;
            }
            if ( handleID.equals("punch1") ) {
                if ( SkillTreeWindow.this.punchTree ) {
                    SkillTreeWindow.this.Line2.setVisible(false);
                    SkillTreeWindow.this.Line2Activated.setVisible(true);
                }
                return true;
            }
            if ( handleID.equals("uppercut") ) {

                SkillTreeWindow.this.Line3.setVisible(false);
                SkillTreeWindow.this.Line3Activated.setVisible(true);

                return true;
            }
            if ( handleID.equals("steam") ) {

                SkillTreeWindow.this.Line4.setVisible(false);
                SkillTreeWindow.this.Line4Activated.setVisible(true);

                return true;
            }

            return false;
        }
    }

}
