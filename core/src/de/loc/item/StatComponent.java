package de.loc.item;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class StatComponent implements Component {
    public ArrayList<Stat> stats;

    public StatComponent() {
        this.stats = new ArrayList<>(1);
    }

    public void add(Stat stat) {
        this.stats.add(stat);
    }
}
