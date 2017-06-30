package com.databerries.tree;

import java.io.Serializable;

import static com.databerries.tree.KdTree.X_COMPARATOR;
import static com.databerries.tree.KdTree.Y_COMPARATOR;
import static com.databerries.tree.KdTree.Z_COMPARATOR;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

//TODO: remove
public class XYZPoint implements Comparable<XYZPoint>, Serializable {
    public static final int EARTH_RADIUS = 6371;
    final double x;
    final double y;
    final double z;

    /**
     * z is defaulted to zero.
     *
     * @param x
     * @param y
     */
    protected XYZPoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    /**
     * Default constructor
     *
     * @param x
     * @param y
     * @param z
     */
    protected XYZPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Does not use R to calculate x, y, and z. Where R is the approximate radius of earth (e.g. 6371KM).
     * @param latitude
     * @param longitude
     */
    protected XYZPoint(double r, Double latitude, Double longitude) {
        x = r * cos(Math.toRadians(latitude)) * cos(Math.toRadians(longitude));
        y = r * cos(Math.toRadians(latitude)) * sin(Math.toRadians(longitude));
        z = r * sin(Math.toRadians(latitude));
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }

    /**
     * Computes the Euclidean distance from this point to the other.
     *
     * @param o1
     *            other point.
     * @return euclidean distance.
     */
    public double euclideanDistance(XYZPoint o1) {
        return euclideanDistance(o1, this);
    }

    /**
     * Computes the Euclidean distance from one point to the other.
     *
     * @param o1
     *            first point.
     * @param o2
     *            second point.
     * @return euclidean distance.
     */
    private static double euclideanDistance(XYZPoint o1, XYZPoint o2) {
        return Math.sqrt(Math.pow((o1.x - o2.x), 2) + Math.pow((o1.y - o2.y), 2) + Math.pow((o1.z - o2.z), 2));
    }

    public static XYZPoint createXY(double x, double y) {
        return new XYZPoint(x, y);
    }

    public static XYZPoint createXYZ(double x, double y, double z) {
        return new XYZPoint(x, y, z);
    }

    public static XYZPoint createFromLatitudeLongitude(double latitude, double longitude) {
        return new XYZPoint(EARTH_RADIUS, Double.valueOf(latitude), Double.valueOf(longitude));
    }

    @Override
    public int hashCode() {
        return 31 * (int)(this.x + this.y + this.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof XYZPoint))
            return false;

        XYZPoint xyzPoint = (XYZPoint) obj;
        if (Double.compare(this.x, xyzPoint.x)!=0)
            return false;
        if (Double.compare(this.y, xyzPoint.y)!=0)
            return false;
        if (Double.compare(this.z, xyzPoint.z)!=0)
            return false;
        return true;
    }

    @Override
    public int compareTo(XYZPoint o) {
        int xComp = X_COMPARATOR.compare(this, o);
        if (xComp != 0)
            return xComp;
        int yComp = Y_COMPARATOR.compare(this, o);
        if (yComp != 0)
            return yComp;
        return Z_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return "(" +
                x + ", " +
                y + ", " +
                z +
                ")";
    }
}