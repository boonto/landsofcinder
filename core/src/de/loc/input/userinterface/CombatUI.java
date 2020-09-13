package de.loc.input.userinterface;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.SnapshotArray;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import de.loc.combat.CombatComponent;
import de.loc.combat.Skill;
import de.loc.combat.SkillParser;
import de.loc.item.Stat;
import de.loc.tools.Constants;
import de.loc.tools.Pair;

public class CombatUI {

    private final UserInterface ui;
    private final CombatInterface.CombatInputHandler combatInputHandler;

    private final MenuWindow healthBar;
    private final Image red;
    private final MenuWindow steamBar;
    private final Image needle;
    private final MenuWindow actionWindow;
    private final Map<SkillType, MenuWindow> skillWindows;
    private final Map<SkillType, Button> skillTypeButtons;

    public CombatUI(UserInterface ui, CombatInterface.CombatInputHandler combatInputHandler, CombatComponent playerCC) {
        this.ui = ui;
        this.combatInputHandler = combatInputHandler;

        this.healthBar = new MenuWindow(ui, MenuWindow.Type.HEALTHBAR);
        this.healthBar.setBackground(new NinePatchDrawable(ui.getNinePatch(Constants.UI_NINEPATCHES_PATH + "black.9.png")));
        this.red = new Image(new NinePatchDrawable(ui.getNinePatch(Constants.UI_NINEPATCHES_PATH + "red.9.png")));

        this.steamBar = new MenuWindow(ui, MenuWindow.Type.STEAMMETER);
        TextureRegion steam = new TextureRegion(ui.getTextureWithMipmap(Constants.UI_NINEPATCHES_PATH + "uhr_512.png"));
        steam.getTexture().setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.steamBar.setBackground(new TextureRegionDrawable(steam));

        TextureRegion needleTexRegion = new TextureRegion(ui.getTextureWithMipmap(Constants.UI_NINEPATCHES_PATH + "needle.png"));
        needleTexRegion.getTexture().setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.needle = new Image(needleTexRegion);
        this.needle.setPosition(
            (this.steamBar.getX() + (this.steamBar.getWidth() / 2.0f)) - (this.needle.getWidth() / 2.0f),
            this.steamBar.getY() + (this.steamBar.getHeight() / 2.0f));
        this.needle.setScale(1.0f, 0.7f);
        this.needle.setRotation(135.0f);

        this.actionWindow = new MenuWindow(ui, MenuWindow.Type.COMBATBASE);
        this.actionWindow.setBackground(new NinePatchDrawable(ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));

        this.skillWindows = new EnumMap<SkillType, MenuWindow>(SkillType.class);
        this.skillTypeButtons = new EnumMap<SkillType, Button>(SkillType.class);
        this.setupSkillWindows(playerCC.skillList);

        this.fillActionWindow(playerCC.curActions);

        this.updatePlayerHealth(playerCC.maxHealth, playerCC.curHealth);
        this.updatePlayerSteam(playerCC.curSteam);
    }

    private void setupSkillWindows(Map<Skill.Type, String> skillList) {
        this.skillWindows.put(SkillType.ATTACK, new MenuWindow(this.ui, MenuWindow.Type.COMBATSKILLS));
        this.skillWindows.put(SkillType.DEFEND, new MenuWindow(this.ui, MenuWindow.Type.COMBATSKILLS));
        this.skillWindows.put(SkillType.ITEM, new MenuWindow(this.ui, MenuWindow.Type.COMBATSKILLS));
        for ( MenuWindow skillWindow : this.skillWindows.values() ) {
            skillWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCHES_PATH + "woodrahmen500.9.png")));
        }

        float skillWindowWidth = this.skillWindows.get(SkillType.ATTACK).getWidth();

        this.skillTypeButtons.put(SkillType.ATTACK, this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "swordframedisabled.9.png",
                                                                                          Constants.UI_NINEPATCHES_PATH + "swordframe.9.png",
                                                                                          skillWindowWidth - (float) Constants.SKILL_IMAGE_SPACE,
                                                                                          (float) Constants.SKILL_IMAGE_TOP,
                                                                                          (float) Constants.SKILL_IMAGE_SIZE,
                                                                                          (float) Constants.SKILL_IMAGE_SIZE));
        this.skillTypeButtons.get(SkillType.ATTACK).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CombatUI.this.combatInputHandler.handle(CombatInterface.CombatInputHandler.OFFENSIVE);
            }
        });
        this.ui.remove(this.skillTypeButtons.get(SkillType.ATTACK));//TODO

        this.skillTypeButtons.put(SkillType.DEFEND, this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "shieldframedisabled.9.png",
                                                                                          Constants.UI_NINEPATCHES_PATH + "shieldframe.9.png",
                                                                                          skillWindowWidth - (float) Constants.SKILL_IMAGE_SPACE,
                                                                                          (float) Constants.SKILL_IMAGE_TOP
                                                                                          - (float) Constants.SKILL_IMAGE_SIZE,
                                                                                          (float) Constants.SKILL_IMAGE_SIZE,
                                                                                          (float) Constants.SKILL_IMAGE_SIZE));
        this.skillTypeButtons.get(SkillType.DEFEND).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CombatUI.this.combatInputHandler.handle(CombatInterface.CombatInputHandler.DEFENSIVE);
            }
        });
        this.ui.remove(this.skillTypeButtons.get(SkillType.DEFEND));//TODO

        this.skillTypeButtons.put(SkillType.ITEM, this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "trankframedisabled.9.png",
                                                                                        Constants.UI_NINEPATCHES_PATH + "trankframe.9.png",
                                                                                        skillWindowWidth - (float) Constants.SKILL_IMAGE_SPACE,
                                                                                        (float) Constants.SKILL_IMAGE_TOP - (float) (2
                                                                                                                                     * Constants.SKILL_IMAGE_SIZE),
                                                                                        (float) Constants.SKILL_IMAGE_SIZE,
                                                                                        (float) Constants.SKILL_IMAGE_SIZE));
        this.skillTypeButtons.get(SkillType.ITEM).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CombatUI.this.combatInputHandler.handle(CombatInterface.CombatInputHandler.ITEMS);
            }
        });
        this.ui.remove(this.skillTypeButtons.get(SkillType.ITEM));//TODO

        this.fillSkillWindow(skillList);
    }

    public void hide() {
        this.ui.remove(this.healthBar);
        this.ui.remove(this.red);
        this.ui.remove(this.steamBar);
        this.ui.remove(this.needle);
        this.ui.remove(this.actionWindow);
        for ( MenuWindow skillWindow : this.skillWindows.values() ) {
            this.ui.remove(skillWindow);
        }
        for ( Button button : this.skillTypeButtons.values() ) {
            this.ui.remove(button);
        }
    }

    public void show() {
        this.ui.add(this.healthBar);
        this.ui.add(this.red);
        this.ui.add(this.steamBar);
        this.ui.add(this.needle);
        this.ui.add(this.actionWindow);
        for ( MenuWindow skillWindow : this.skillWindows.values() ) {
            this.ui.add(skillWindow);
            skillWindow.setVisible(false);
        }
        for ( Button button : this.skillTypeButtons.values() ) {
            this.ui.add(button);
            button.setVisible(false);
        }
    }

    public void hideSkillWindows() {
        for ( MenuWindow skillWindow : this.skillWindows.values() ) {
            skillWindow.setVisible(false);
        }
        for ( Button button : this.skillTypeButtons.values() ) {
            button.setVisible(false);
        }
    }

    public void showSkillWindow(SkillType type) {
        this.hideSkillWindows();
        this.skillWindows.get(type).setVisible(true);
        for ( Button button : this.skillTypeButtons.values() ) {
            button.setVisible(true);
            button.setChecked(false);
        }
        this.skillTypeButtons.get(type).setChecked(true);
    }

    public void updatePlayerHealth(int maxHealth, int curHealth) {
        float width = ((this.healthBar.getWidth() - this.healthBar.getPadLeft() - this.healthBar.getPadRight()) / (float) maxHealth) * (float) curHealth;

        this.healthBar.clear();
        this.healthBar.add(this.red).width(width);
    }

    public void updatePlayerSteam(int curSteam) {
        //TODO dafuq
        float rotation = (float) (-curSteam + 135);

        this.needle.setRotation(rotation);
    }

    private void fillSkillWindow(Map<Skill.Type, String> skillList) {

        for ( final Map.Entry<Skill.Type, String> entry : skillList.entrySet() ) {
            Pair<String, ArrayList<Stat>> skill = SkillParser.parseSkill(entry.getValue()); //TODO ist das etwas im kreis herum?

            Table skillButton = (Table) this.skillWindows.get(SkillType.ATTACK).addTextButtonItemWithoutListener(entry.getValue());
            //schauen ob der skill GROUP_DAMAGE macht
            if ( skill.getRight().contains(new Stat(Stat.Type.GROUP_DAMAGE, 0)) ) {
                skillButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        CombatUI.this.combatInputHandler.handle(CombatInterface.CombatInputHandler.GROUP_SKILL + entry.getKey());
                    }
                });
            } else {
                skillButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        CombatUI.this.combatInputHandler.handle(CombatInterface.CombatInputHandler.SKILL + entry.getKey());
                    }
                });
            }
        }

        this.skillWindows.get(SkillType.DEFEND).addTextButtonItemWithoutListener("Block");
        this.skillWindows.get(SkillType.DEFEND).addTextButtonItemWithoutListener("Block");
        this.skillWindows.get(SkillType.DEFEND).addTextButtonItemWithoutListener("Block");

        this.skillWindows.get(SkillType.ITEM).addTextButtonItemWithoutListener("HP +  50%");
        this.skillWindows.get(SkillType.ITEM).addTextButtonItemWithoutListener("HP + 100%");
        this.skillWindows.get(SkillType.ITEM).addTextButtonItemWithoutListener("Dampf + 100%");
    }

    private void fillActionWindow(int curActions) {
        for ( int i = 1; i <= curActions; i++ ) {
            final int action = i;
            TextButton actionButton = (TextButton) ((Group) (this.actionWindow.addTextButtonItemWithoutListener("Aktion " + i))).getChildren().get(0);

            actionButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    CombatUI.this.combatInputHandler.handle(CombatInterface.CombatInputHandler.ACTION + (action - 1));
                }
            });
        }

        this.actionWindow.addTextButtonItemWithoutListener("Go!").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CombatUI.this.combatInputHandler.handle(CombatInterface.CombatInputHandler.GO);
            }
        });
    }

    public void resetActionButtons() {
        SnapshotArray<Actor> buttons = ((Group) ((Group) this.actionWindow.getChildren().get(0)).getChildren().get(0)).getChildren();

        for ( int i = 0; i < (buttons.size - 1); ++i ) {
            TextButton button = (TextButton) ((Group) buttons.get(i)).getChildren().get(0);
            button.setText("Aktion " + (i + 1));
        }
    }

    public void setSkill(int action, Skill.Type skill) {
        TextButton
            button =
            (TextButton) ((Group) ((Group) ((Group) this.actionWindow.getChildren().get(0)).getChildren().get(0)).getChildren().get(action)).getChildren()
                                                                                                                                            .get(0);
        button.setText(skill.toString());
    }

    public void setTarget(int action, String target) {
        TextButton
            button =
            (TextButton) ((Group) ((Group) ((Group) this.actionWindow.getChildren().get(0)).getChildren().get(0)).getChildren().get(action)).getChildren()
                                                                                                                                            .get(0);
        String[] strings = button.getText().toString().split("\n");
        button.setText(strings[0] + '\n' + target);
    }

    public enum SkillType {
        ATTACK,
        DEFEND,
        ITEM
    }
}
