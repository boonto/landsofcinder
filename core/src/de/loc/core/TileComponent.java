package de.loc.core;

import com.badlogic.ashley.core.Component;

public class TileComponent implements Component {
    public final byte[][] tileMap;
    public final byte[][] heightMap;

    /**
     * @param tileMap   Contains the information wheter a field is walkable or obstructed.
     *                  The dimensions of the array (x and y) also define the total size of the tile.
     * @param heightMap contains the height-information for every tile-field
     */
    public TileComponent(byte[][] tileMap, byte[][] heightMap) {
        this.tileMap = tileMap;
        this.heightMap = heightMap;
    }

    public int getSizeX() {
        return this.tileMap.length;
    }

    public int getSizeY() {
        return (this.tileMap[0].length);
    }
}
