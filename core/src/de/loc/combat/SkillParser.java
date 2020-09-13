package de.loc.combat;

import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;
import java.util.Map;

import de.loc.item.Stat;
import de.loc.tools.Constants;
import de.loc.tools.ListItem;
import de.loc.tools.Pair;
import de.loc.tools.XmlHelper;

public final class SkillParser {

    private SkillParser() {
    }

    public static Pair<String, ArrayList<Stat>> parseSkill(String name) {
        Map<String, ListItem> skillList = XmlHelper.getHashMapXmlList(Constants.SKILL_LIST_PATH);

        XmlReader.Element skillNode = skillList.get(name).xmlElement;
        String gameName = skillNode.getChildByName("GameName").getText();
        Pair<String, ArrayList<Stat>> skillData = new Pair<>(gameName, new ArrayList<Stat>());

        if ( skillNode.getChildByName("MinDamage") != null ) {
            int value = Integer.parseInt(skillNode.getChildByName("MinDamage").getText());
            skillData.getRight().add(new Stat(Stat.Type.MIN_DAMAGE, value));
        }
        if ( skillNode.getChildByName("MaxDamage") != null ) {
            int value = Integer.parseInt(skillNode.getChildByName("MaxDamage").getText());
            skillData.getRight().add(new Stat(Stat.Type.MAX_DAMAGE, value));
        }
        if ( skillNode.getChildByName("ArmorReduction") != null ) {
            int value = Integer.parseInt(skillNode.getChildByName("ArmorReduction").getText());
            skillData.getRight().add(new Stat(Stat.Type.ARMOR_REDUCTION, value));
        }
        if ( skillNode.getChildByName("GroupDamage") != null ) {
            int value = Integer.parseInt(skillNode.getChildByName("GroupDamage").getText());
            skillData.getRight().add(new Stat(Stat.Type.GROUP_DAMAGE, value));
        }
        if ( skillNode.getChildByName("SteamCost") != null ) {
            int value = Integer.parseInt(skillNode.getChildByName("SteamCost").getText());
            skillData.getRight().add(new Stat(Stat.Type.STEAM_COST, value));
        }
        return skillData;
    }
}
