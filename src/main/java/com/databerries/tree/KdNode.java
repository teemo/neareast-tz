package com.databerries.tree;

import java.io.Serializable;

import static com.databerries.tree.KdTree.X_AXIS;
import static com.databerries.tree.KdTree.X_COMPARATOR;
import static com.databerries.tree.KdTree.Y_AXIS;
import static com.databerries.tree.KdTree.Y_COMPARATOR;
import static com.databerries.tree.KdTree.Z_COMPARATOR;

//todo: remove serializable
class KdNode<T extends XYZPoint> implements Comparable<KdNode>, Serializable {

    final T id;
    final int k;
    final int depth;

    KdNode parent = null;
    KdNode lesser = null;
    KdNode greater = null;

    KdNode(T id) {
        this.id = id;
        this.k = 3;
        this.depth = 0;
    }

    KdNode(T id, int k, int depth) {
        this.id = id;
        this.k = k;
        this.depth = depth;
    }

    static int compareTo(int depth, int k, XYZPoint o1, XYZPoint o2) {
        int axis = depth % k;
        if (axis == X_AXIS)
            return X_COMPARATOR.compare(o1, o2);
        if (axis == Y_AXIS)
            return Y_COMPARATOR.compare(o1, o2);
        return Z_COMPARATOR.compare(o1, o2);
    }

    @Override
    public int hashCode() {
        return 31 * (this.k + this.depth + this.id.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof KdNode))
            return false;

        KdNode kdNode = (KdNode) obj;
        if (this.compareTo(kdNode) == 0)
            return true;
        return false;
    }

    @Override
    public int compareTo(KdNode o) {
        return compareTo(depth, k, this.id, o.id);
    }

    @Override
    public String toString() {
        return "k=" + k +
                " depth=" + depth +
                " id=" + id.toString();
    }
}