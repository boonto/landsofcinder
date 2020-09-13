package de.loc.tools;

public class DimensionHelper {

    //public final static Matrix4 GRID_ROTATION = new Matrix4().rotate(new Vector3(0,1,0), -45.f);

    public static float getCameraHeight(float angle) {
        // rechtwinkeliges Dreieck: tan(alpha) = Gegenkathete / Ankathete
        // wobei ich davon ausgehe dass die Kamera auf x = 1 und z = 1 liegt
        // (45�)
        return (float) (Math.tan(Math.toRadians(angle)) * Math.sqrt(2));
    }

    public static float getGridHeight(float angle, float gridWidth) {
        return (float) Math.sin(Math.toRadians(angle)) * gridWidth;
    }

    public static Position ind2sub(int ind, int width) {
        return new Position(ind % width, ind / width);
    }

    public static int sub2ind(Position sub, int width) {
        return width * sub.y + sub.x;
    }
}