package de.loc.item;

public class Stat {
    public enum Type {
        //ItemTypes
        DAMAGE,
        HEALTHRESTORE,
        ARMOUR,

        //SkillTypes
        MIN_DAMAGE,
        MAX_DAMAGE,
        ARMOR_REDUCTION,
        GROUP_DAMAGE,
        STEAM_COST,
    }

    public Type type;
    public int value;

    public Stat(Type type, int value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null || this.getClass() != obj.getClass() ) {
            return false;
        }

        Stat stat = (Stat) obj;

        return this.type == stat.type;

    }

    @Override
    public int hashCode() {
        return this.type != null ? this.type.hashCode() : 0;
    }
}

