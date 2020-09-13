package de.loc.tools;

import com.badlogic.gdx.math.Vector2;

public class Position {

    public int x;
    public int y;

    public Position() {
        this.x = 0;
        this.y = 0;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    @Override
    public boolean equals(Object obj) {

        if ( obj instanceof Position ) {
            Position p = (Position) obj;
            return (this.x == p.x) && (this.y == p.y);
        }
        return false;
    }

    public boolean compareTo(Position comp) {
        return (this.x == comp.x) && (this.y == comp.y);
    }

    //abfrage ob die Position anliegend ist
    public boolean adjacentTo(Position adj) {
        int dX = this.x - adj.x;
        int dY = this.y - adj.y;

        return ((dX >= -1) && (dX <= 1)) && ((dY >= -1) && (dY <= 1));
    }

    public Position add(Position position) {
        int x = this.x + position.x;
        int y = this.y + position.y;

        return new Position(x, y);
    }

    public Position subtract(Position position) {
        int x = this.x - position.x;
        int y = this.y - position.y;

        return new Position(x, y);
    }

    //wandelt die position in eine "richtung" um
    public void direct() {
        this.x = (this.x != 0) ? (this.x / Math.abs(this.x)) : 0;
        this.y = (this.y != 0) ? (this.y / Math.abs(this.y)) : 0;
    }

    //liefert die anderen anliegenden positionen, ausgehend von einer richtung
    public Position[] getOtherAdjacents(Position direction) {
        Position[] array = new Position[7];

        //irgendwie komisch pseudohardcoded
        if ( direction.x == 0 ) {
            array[0] = this.add(new Position(-1, direction.y));
            array[1] = this.add(new Position(1, direction.y));
            array[2] = this.add(new Position(-1, 0));
            array[3] = this.add(new Position(1, 0));
            array[4] = this.add(new Position(-1, -direction.y));
            array[5] = this.add(new Position(1, -direction.y));
            array[6] = this.add(new Position(0, -direction.y));
        } else if ( direction.y == 0 ) {
            array[0] = this.add(new Position(direction.x, -1));
            array[1] = this.add(new Position(direction.x, 1));
            array[2] = this.add(new Position(0, -1));
            array[3] = this.add(new Position(0, 1));
            array[4] = this.add(new Position(-direction.x, -1));
            array[5] = this.add(new Position(-direction.x, 1));
            array[6] = this.add(new Position(-direction.x, 0));
        } else {
            array[0] = this.add(new Position(0, direction.y));
            array[1] = this.add(new Position(direction.x, 0));
            array[2] = this.add(new Position(-direction.x, direction.y));
            array[3] = this.add(new Position(direction.x, -direction.y));
            array[4] = this.add(new Position(-direction.x, 0));
            array[5] = this.add(new Position(0, -direction.y));
            array[6] = this.add(new Position(-direction.x, -direction.y));
        }

        return array;
    }

    public Position[] getAdjacents() {
        Position[] array = new Position[8];

        array[0] = this.add(new Position(-1, -1));
        array[1] = this.add(new Position(0, -1));
        array[2] = this.add(new Position(1, -1));
        array[3] = this.add(new Position(-1, 0));
        array[4] = this.add(new Position(1, 0));
        array[5] = this.add(new Position(-1, 1));
        array[6] = this.add(new Position(0, 1));
        array[7] = this.add(new Position(1, 1));

        return array;
    }

    public boolean inVicinity(Position pos, int range) {
        Position p = this.subtract(pos);

        return p.length() <= (float) range;
    }

    public float length() {
        return (float) Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

    public Vector2 toVector2() {
        return new Vector2((float) this.x, (float) this.y);
    }
}
