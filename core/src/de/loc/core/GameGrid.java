package de.loc.core;

import com.badlogic.gdx.utils.StringBuilder;

import de.loc.tools.Constants;
import de.loc.tools.Position;

public class GameGrid {
    private byte[][] grid;
    private final Position gridSize;

    private final byte TYPE_MASK = (byte) 0xe0;
    private final byte HEIGHT_MASK = (byte) 0x1f;

    public GameGrid(int sizeX, int sizeY) {
        this.grid = new byte[sizeX][sizeY];
        this.gridSize = new Position(sizeX, sizeY);
        this.setupGameGrid();
    }

    private void setupGameGrid() {
        for ( int x = 0; x < this.gridSize.x; x++ ) {
            for ( int y = 0; y < this.gridSize.y; y++ ) {
                this.setType(x, y, Type.EMPTY);
                this.setHeight(x, y, (byte) 0);
            }
        }
    }

    public void softResetGrid() {
        for ( int x = 0; x < this.gridSize.x; x++ ) {
            for ( int y = 0; y < this.gridSize.y; y++ ) {
                if ( this.grid[x][y] == Type.OCCUPIED ) {
                    this.setType(x, y, Type.WALKABLE);
                }
            }
        }
    }

    public void setSize(int sizeX, int sizeY) {
        byte[][] tempGrid = new byte[sizeX][sizeY];

        for ( int a = 0; a < sizeX; a++ ) {
            for ( int b = 0; b < sizeY; b++ ) {
                tempGrid[a][b] = Type.WALKABLE;
            }
        }

        for ( int a = 0; a < sizeX && a < this.gridSize.x; a++ ) {
            for ( int b = 0; b < sizeY && b < this.gridSize.y; b++ ) {
                tempGrid[a][b] = this.grid[a][b];
            }
        }

        this.grid = tempGrid;
        this.gridSize.x = sizeX;
        this.gridSize.y = sizeY;
    }

    public Position getSize() {
        return this.gridSize;
    }

    // this method should only be used by the SceneLoader
    // as it makes no difference between field height and type!
    // no check if it is inside!
    public void setByteAt(int x, int y, byte value) {
        this.grid[x][y] = value;
    }

    // no check if it is inside!
    public byte getByteAt(int x, int y) {
        return this.grid[x][y];
    }

    public boolean setType(int x, int y, byte value) {
        if ( !this.isInside(x, y) ) {
            //System.out.println("WARNING: Parameter of setType(...) exceeds total grid-size!");
            return false;
        } else {
            // löschen des vorherigen Typwertes:
            this.grid[x][y] = (byte) (this.grid[x][y] & this.HEIGHT_MASK);
            // Oder verknüpfung des typs mit dem byte
            this.grid[x][y] = (byte) (this.grid[x][y] | value);

            return true;
        }
    }

    public boolean setType(Position position, byte value) {
        return this.setType(position.x, position.y, value);
    }

    public byte getType(int x, int y) {
        if ( !this.isInside(x, y) ) {
            return Type.OUTSIDE;
        }
        // byte an der position im grid mit der TYPE_MASK
        //maskieren (UND OPERATOR) um nur den Feld-Typ zu bekommen
        return (byte) (this.grid[x][y] & this.TYPE_MASK);
    }

    public byte getType(Position position) {
        return this.getType(position.x, position.y);
    }

    public boolean setHeight(int x, int y, byte value) {
        if ( !this.isInside(x, y) ) {
            //System.out.println("WARNING: Parameter of setHeight(...) exceeds total grid-size!");
            return false;
        } else {

            // löschen des vorherigen Höhenwerts:
            this.grid[x][y] = (byte) (this.grid[x][y] & this.TYPE_MASK);
            // Oder verknüpfung der höhe mit dem byte
            this.grid[x][y] = (byte) (this.grid[x][y] | value);

            return true;
        }
    }

    public boolean setHeight(Position position, byte value) {
        return this.setHeight(position.x, position.y, value);
    }

    public byte getHeight(int x, int y) {
        if ( !this.isInside(x, y) ) {
            //System.out.println("WARNING: Parameter of getHeight(...) exceeds total grid-size!");
            return Type.OUTSIDE;
        } else {
            // byte an der position im grid mit der HEIGHT_MASK
            //maskieren (UND OPERATOR) um nur die Höhen-Info zu bekommen
            return (byte) (this.grid[x][y] & this.HEIGHT_MASK);
        }
    }

    public byte getHeight(Position position) {
        return this.getHeight(position.x, position.y);
    }

    public float getHeightF(int x, int y) {
        return this.getHeight(x, y) * Constants.GRID_HEIGHT_STEP;
    }

    public float getHeightF(Position position) {
        return this.getHeightF(position.x, position.y);
    }

    public void printHeightGrid() {
        StringBuilder sb = new StringBuilder();
        for ( int y = 0; y < this.gridSize.y; y++ ) {
            for ( int x = 0; x < this.gridSize.x; x++ ) {

                sb.append(this.getHeightF(x, y)).append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    public boolean isInside(int x, int y) {
        return (x >= 0 && y >= 0 && x < this.getSize().x && y < this.getSize().y);
    }

    public boolean isInside(Position position) {
        return this.isInside(position.x, position.y);
    }

    public boolean isObstructed(int x, int y) {
        return this.getType(x, y) == Type.OBSTRUCTED;
    }

    public boolean isObstructed(Position position) {
        return this.isObstructed(position.x, position.y);
    }

    public boolean isOccupied(int x, int y) {
        return this.getType(x, y) == Type.OCCUPIED;
    }

    public boolean isOccupied(Position position) {
        return this.isOccupied(position.x, position.y);
    }

    public boolean isWalkable(int x, int y) {
        return this.getType(x, y) == Type.WALKABLE;
    }

    public boolean isWalkable(Position position) {
        return this.isWalkable(position.x, position.y);
    }

    public boolean checkWalkablePosition(Position position, Position size, float angle) {
        boolean result = true;

        //System.out.println("Angle: " + angle);
        for ( int x = 0; x < size.x; ++x ) {
            //System.out.println("x: " + x);
            for ( int y = 0; y < size.y; ++y ) {
                //System.out.println("y: " + y);
                if ( angle == 0 ) {
                    if ( result ) {
                        result = this.isWalkable(position.x + x, position.y + y);
                        //System.out.println("0: " + result);
                    }
                } else if ( angle == 90 ) {
                    if ( result ) {
                        result = this.isWalkable(position.x + y, position.y - x);
                        //System.out.println("90: " + result);
                    }
                } else if ( angle == 180 ) {
                    if ( result ) {
                        result = this.isWalkable(position.x - x, position.y - y);
                        //System.out.println("180: " + result);
                    }
                } else if ( angle == 270 ) {
                    if ( result ) {
                        result = this.isWalkable(position.x - y, position.y + x);
                        //System.out.println("270: " + result);
                    }
                }
            }
        }

        return result;
    }

    public boolean setToObstructed(Position position, Position size, float angle) {
        if ( this.checkWalkablePosition(position, size, angle) ) {
            for ( int x = 0; x < size.x; ++x ) {
                //System.out.println("x: " + x);
                for ( int y = 0; y < size.y; ++y ) {
                    //System.out.println("y: " + y);
                    if ( angle == 0 ) {
                        this.setType(position.x + x, position.y + y, Type.OBSTRUCTED);
                    } else if ( angle == 90 ) {
                        this.setType(position.x + y, position.y - x, Type.OBSTRUCTED);
                    } else if ( angle == 180 ) {
                        this.setType(position.x - x, position.y - y, Type.OBSTRUCTED);
                    } else if ( angle == 270 ) {
                        this.setType(position.x - y, position.y + x, Type.OBSTRUCTED);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void setToWalkable(Position position, Position size, float angle) {
        for ( int x = 0; x < size.x; ++x ) {
            //System.out.println("x: " + x);
            for ( int y = 0; y < size.y; ++y ) {
                //System.out.println("y: " + y);
                if ( angle == 0 ) {
                    this.setType(position.x + x, position.y + y, Type.WALKABLE);
                } else if ( angle == 90 ) {
                    this.setType(position.x + y, position.y - x, Type.WALKABLE);
                } else if ( angle == 180 ) {
                    this.setType(position.x - x, position.y - y, Type.WALKABLE);
                } else if ( angle == 270 ) {
                    this.setType(position.x - y, position.y + x, Type.WALKABLE);
                }
            }
        }
    }

    public boolean setToOccupied(Position position, Position size, float angle) {
        if ( this.checkWalkablePosition(position, size, angle) ) {
            for ( int x = 0; x < size.x; ++x ) {
                //System.out.println("x: " + x);
                for ( int y = 0; y < size.y; ++y ) {
                    //System.out.println("y: " + y);
                    if ( angle == 0 ) {
                        this.setType(position.x + x, position.y + y, Type.OCCUPIED);
                    } else if ( angle == 90 ) {
                        this.setType(position.x + y, position.y - x, Type.OCCUPIED);
                    } else if ( angle == 180 ) {
                        this.setType(position.x - x, position.y - y, Type.OCCUPIED);
                    } else if ( angle == 270 ) {
                        this.setType(position.x - y, position.y + x, Type.OCCUPIED);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isTilePlaceable(TileComponent t, int x, int y) {
        boolean result = true;

        for ( int i = x; i < x + t.getSizeX(); i++ ) {
            //System.out.println("x: " + x);
            for ( int j = y; j < y + t.getSizeY(); j++ ) {
                //System.out.println("y: " + y);
                if ( result ) {
                    result = (this.getType(i, y) == Type.EMPTY);
                }
            }
        }
        return result;
    }

    public void addTile(TileComponent t, int posX, int posY) {

        for ( int x = 0; x < t.getSizeX(); x++ ) {
            //System.out.println("x: " + x);
            for ( int y = 0; y < t.getSizeY(); y++ ) {
                this.setType(x + posX, y + posY, t.tileMap[x][y]);
            }
        }

        //        for (int x = 0; x < t.getSizeX(); x++)
        //        {
        //            //System.out.println("x: " + x);
        //            for (int y = 0; y < t.getSizeY(); y++)
        //            {
        //                setHeight(x + posX, y + posY, t.HEIGHT_MAP[x][y]);
        //            }
        //        }
    }

    public void clearTile(Position position, Position size, float angle) {
        for ( int x = 0; x < size.x; ++x ) {
            //System.out.println("x: " + x);
            for ( int y = 0; y < size.y; ++y ) {
                //System.out.println("y: " + y);
                if ( angle == 0 ) {
                    this.setType(position.x + x, position.y + y, Type.EMPTY);
                } else if ( angle == 90 ) {
                    this.setType(position.x + y, position.y - x, Type.EMPTY);
                } else if ( angle == 180 ) {
                    this.setType(position.x - x, position.y - y, Type.EMPTY);
                } else if ( angle == 270 ) {
                    this.setType(position.x - y, position.y + x, Type.EMPTY);
                }
            }
        }
    }

    public static class Type {
        // Decision nimmt die ersten drei Bits des Bytes ein (8 verschiedene Werte)
        public static final byte OUTSIDE = (byte) 0x00;
        public static final byte OBSTRUCTED = (byte) 0x20;
        public static final byte OCCUPIED = (byte) 0x40;
        public static final byte WALKABLE = (byte) 0x60;
        public static final byte EMPTY = (byte) 0x80;
        public static final byte FREE_SLOT2 = (byte) 0xa0;
        public static final byte FREE_SLOT3 = (byte) 0xc0;
        public static final byte FREE_SLOT4 = (byte) 0xe0;

        // In den restlichen Bits werden die HöhenInfos codiert. (32 verschiedene Werte)
        public static final byte TYPE_MASK = (byte) 0xe0;
        public static final byte HEIGHT_MASK = (byte) 0x1f;
    }
}
