package de.loc.item;

import com.badlogic.gdx.math.RandomXS128;

import java.util.ArrayList;
import java.util.List;

import de.loc.combat.CombatComponent;

public final class StatManager {

    private static final RandomXS128 RANDOM = new RandomXS128();

    private StatManager() {
    }

    //modifies the existing CombatComponent
    public static CombatComponent modifyStat(CombatComponent combatComponent, StatComponent statComponent) {
        applyStat(combatComponent, statComponent);

        return combatComponent;
    }

    private static void applyStat(CombatComponent combatComponent, StatComponent statComponent) {
        for ( Stat stat : statComponent.stats ) {
            switch ( stat.type ) {
                case DAMAGE:
                    combatComponent.attack += stat.value;
                    break;
                case HEALTHRESTORE:
                    combatComponent.curHealth += stat.value;
                    break;
                case ARMOUR:
                    combatComponent.defense += stat.value;
                    break;
            }
        }
    }

    //TODO: besserer name, schöner programmieren, return rausnehmen
    public static int applyStatsToCombatComponents(
        List<Stat> statList, CombatComponent attackerCombatComponent, ArrayList<CombatComponent> defenderCombatComponents) {
        List<Stat.Type> stats = new ArrayList<>();
        for ( Stat stat : statList ) {
            stats.add(stat.type);
        }

        int damage = 0;

        for ( CombatComponent defender : defenderCombatComponents ) {
            int defense = defender.defense;
            int minDmg = attackerCombatComponent.attack;
            int maxDmg = attackerCombatComponent.attack;

            if ( stats.contains(Stat.Type.ARMOR_REDUCTION) ) {
                defense *= 1 - (statList.get(stats.indexOf(Stat.Type.ARMOR_REDUCTION)).value / 100);
            }

            if ( stats.contains(Stat.Type.MIN_DAMAGE) ) {
                minDmg += statList.get(stats.indexOf(Stat.Type.MIN_DAMAGE)).value;
            }

            if ( stats.contains(Stat.Type.MAX_DAMAGE) ) {
                maxDmg += statList.get(stats.indexOf(Stat.Type.MAX_DAMAGE)).value;
            }

            if ( stats.contains(Stat.Type.STEAM_COST) ) {
                attackerCombatComponent.curSteam -= statList.get(stats.indexOf(Stat.Type.STEAM_COST)).value;
            }

            damage = RANDOM.nextInt(maxDmg - minDmg + 1) + minDmg - defense;
            defender.curHealth -= damage;
        }

        return damage;
    }
}
