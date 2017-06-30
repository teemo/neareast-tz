package com.databerries.tree;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A k-d tree (short for k-dimensional tree) is a space-partitioning data
 * structure for organizing points in a k-dimensional space. k-d trees are a
 * useful data structure for several applications, such as searches involving a
 * multidimensional search key (e.g. range searches and nearest neighbor
 * searches). k-d trees are a special case of binary space partitioning trees.
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 * @see <a href="http://en.wikipedia.org/wiki/K-d_tree">K-d_tree (Wikipedia)</a>
 */
//TODO: remove
public class KdTree<T extends XYZPoint> implements Iterable<T>, Serializable {

    private int k = 3;
    KdNode root = null;

    static final Comparator<XYZPoint> X_COMPARATOR = (o1, o2) -> {
        if (o1.x < o2.x)
            return -1;
        if (o1.x > o2.x)
            return 1;
        return 0;
    };

    static final Comparator<XYZPoint> Y_COMPARATOR = (o1, o2) -> {
        if (o1.y < o2.y)
            return -1;
        if (o1.y > o2.y)
            return 1;
        return 0;
    };

    static final Comparator<XYZPoint> Z_COMPARATOR = (o1, o2) -> {
        if (o1.z < o2.z)
            return -1;
        if (o1.z > o2.z)
            return 1;
        return 0;
    };

    static final int X_AXIS = 0;
    static final int Y_AXIS = 1;
    static final int Z_AXIS = 2;

    /**
     * Default constructor.
     */
    public KdTree() { }

    /**
     * Constructor for creating a more balanced tree. It uses the
     * "median of points" algorithm.
     *
     * @param list
     *            of XYZPoints.
     */
    public KdTree(List<? extends XYZPoint> list) {
        super();
        root = createNode(list, k, 0);
    }

    /**
     * Constructor for creating a more balanced tree. It uses the
     * "median of points" algorithm.
     *
     * @param list
     *            of XYZPoints.
     * @param k
     *            of the tree.
     */
    public KdTree(List<? extends XYZPoint> list, int k) {
        super();
        root = createNode(list, k, 0);
    }

    /**
     * Creates node from list of XYZPoints.
     *
     * @param list
     *            of XYZPoints.
     * @param k
     *            of the tree.
     * @param depth
     *            depth of the node.
     * @return node created.
     */
    private static KdNode createNode(List<? extends XYZPoint> list, int k, int depth) {
        if (list == null || list.size() == 0)
            return null;

        int axis = depth % k;
        if (axis == X_AXIS)
            list.sort(X_COMPARATOR);
        else if (axis == Y_AXIS)
            list.sort(Y_COMPARATOR);
        else
            list.sort(Z_COMPARATOR);

        KdNode node = null;
        List<XYZPoint> less = new ArrayList<>(list.size());
        List<XYZPoint> more = new ArrayList<>(list.size());
        if (list.size() > 0) {
            int medianIndex = list.size() / 2;
            node = new KdNode(list.get(medianIndex), k, depth);
            // Process list to see where each non-median point lies
            for (int i = 0; i < list.size(); i++) {
                if (i == medianIndex)
                    continue;
                XYZPoint p = list.get(i);
                // Cannot assume points before the median are less since they could be equal
                if (KdNode.compareTo(depth, k, p, node.id) <= 0) {
                    less.add(p);
                } else {
                    more.add(p);
                }
            }

            if ((medianIndex-1 >= 0) && less.size() > 0) {
                node.lesser = createNode(less, k, depth + 1);
                node.lesser.parent = node;
            }

            if ((medianIndex <= list.size()-1) && more.size() > 0) {
                node.greater = createNode(more, k, depth + 1);
                node.greater.parent = node;
            }
        }

        return node;
    }

    /**
     * Adds value to the tree. Tree can contain multiple equal values.
     *
     * @param value
     *            T to add to the tree.
     * @return True if successfully added to tree.
     */
    public boolean add(T value) {
        if (value == null)
            return false;

        if (root == null) {
            root = new KdNode(value);
            return true;
        }

        KdNode node = root;
        while (true) {
            if (KdNode.compareTo(node.depth, node.k, value, node.id) <= 0) {
                // Lesser
                if (node.lesser == null) {
                    KdNode newNode = new KdNode(value, k, node.depth + 1);
                    newNode.parent = node;
                    node.lesser = newNode;
                    break;
                }
                node = node.lesser;
            } else {
                // Greater
                if (node.greater == null) {
                    KdNode newNode = new KdNode(value, k, node.depth + 1);
                    newNode.parent = node;
                    node.greater = newNode;
                    break;
                }
                node = node.greater;
            }
        }

        return true;
    }

    /**
     * Does the tree contain the value.
     *
     * @param value
     *            T to locate in the tree.
     * @return True if tree contains value.
     */
    public boolean contains(T value) {
        if (value == null || root == null)
            return false;

        KdNode node = getNode(this, value);
        return (node != null);
    }

    /**
     * Locates T in the tree.
     *
     * @param tree
     *            to search.
     * @param value
     *            to search for.
     * @return KdNode or NULL if not found
     */
    private static <T extends XYZPoint> KdNode getNode(KdTree<T> tree, T value) {
        if (tree == null || tree.root == null || value == null)
            return null;

        KdNode node = tree.root;
        while (true) {
            if (node.id.equals(value)) {
                return node;
            } else if (KdNode.compareTo(node.depth, node.k, value, node.id) <= 0) {
                // Lesser
                if (node.lesser == null) {
                    return null;
                }
                node = node.lesser;
            } else {
                // Greater
                if (node.greater == null) {
                    return null;
                }
                node = node.greater;
            }
        }
    }

    /**
     * Removes first occurrence of value in the tree.
     *
     * @param value
     *            T to remove from the tree.
     * @return True if value was removed from the tree.
     */
    public boolean remove(T value) {
        if (value == null || root == null)
            return false;

        KdNode node = getNode(this, value);
        if (node == null)
            return false;

        KdNode parent = node.parent;
        if (parent != null) {
            if (parent.lesser != null && node.equals(parent.lesser)) {
                List<XYZPoint> nodes = getTree(node);
                if (nodes.size() > 0) {
                    parent.lesser = createNode(nodes, node.k, node.depth);
                    if (parent.lesser != null) {
                        parent.lesser.parent = parent;
                    }
                } else {
                    parent.lesser = null;
                }
            } else {
                List<XYZPoint> nodes = getTree(node);
                if (nodes.size() > 0) {
                    parent.greater = createNode(nodes, node.k, node.depth);
                    if (parent.greater != null) {
                        parent.greater.parent = parent;
                    }
                } else {
                    parent.greater = null;
                }
            }
        } else {
            // root
            List<XYZPoint> nodes = getTree(node);
            if (nodes.size() > 0)
                root = createNode(nodes, node.k, node.depth);
            else
                root = null;
        }

        return true;
    }

    /**
     * Gets the (sub) tree rooted at root.
     *
     * @param root
     *            of tree to get nodes for.
     * @return points in (sub) tree, not including root.
     */
    private static List<XYZPoint> getTree(KdNode root) {
        List<XYZPoint> list = new ArrayList<>();
        if (root == null)
            return list;

        if (root.lesser != null) {
            list.add(root.lesser.id);
            list.addAll(getTree(root.lesser));
        }
        if (root.greater != null) {
            list.add(root.greater.id);
            list.addAll(getTree(root.greater));
        }

        return list;
    }

    /** 
     * Searches the K nearest neighbor.
     *
     * @param K
     *            Number of neighbors to retrieve. Can return more than K, if
     *            last nodes are equal distances.
     * @param value
     *            to find neighbors of.
     * @return Collection of T neighbors.
     */
    @SuppressWarnings("unchecked")
    public List<XYZDistancePoint> nearestNeighbourSearch(int K, T value) {
        if (value == null || root == null)
            return Collections.EMPTY_LIST;

        // Map used for XYZDistancePoints
        TreeSet<XYZDistancePoint> XYZDistancePoints = new TreeSet<>(new EuclideanComparator(value));

        // Find the closest leaf node
        KdNode prev = null;
        KdNode node = root;
        while (node != null) {
            if (KdNode.compareTo(node.depth, node.k, value, node.id) <= 0) {
                // Lesser
                prev = node;
                node = node.lesser;
            } else {
                // Greater
                prev = node;
                node = node.greater;
            }
        }
        KdNode leaf = prev;

        if (leaf != null) {
            // Used to not re-examine nodes
            Set<KdNode> examined = new HashSet<>();

            // Go up the tree, looking for better solutions
            node = leaf;
            while (node != null) {
                // Search node
                searchNode(value, node, K, XYZDistancePoints, examined);
                node = node.parent;
            }
        }
        return new ArrayList<>(XYZDistancePoints);
    }

    public static class XYZDistancePoint<T extends XYZPoint> {
        private final KdNode<T> kdNode;
        private final double distance;

        private XYZDistancePoint(KdNode<T> kdNode, double distance) {
            this.kdNode = kdNode;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            XYZDistancePoint XYZDistancePoint = (XYZDistancePoint) o;

            return kdNode != null ? kdNode.equals(XYZDistancePoint.kdNode) : XYZDistancePoint.kdNode == null;
        }

        @Override
        public int hashCode() {
            return kdNode != null ? kdNode.hashCode() : 0;
        }

        public double getDistance() {
            return distance;
        }

        public T getPoint() {
            return kdNode.id;
        }
    }

    private static <T extends XYZPoint> void searchNode(T value, KdNode<? extends XYZPoint> node, int K, TreeSet<XYZDistancePoint> XYZDistancePoints, Set<KdNode> examined) {
        examined.add(node);

        // Search node
        XYZDistancePoint lastNode = null;
        Double lastDistance = Double.MAX_VALUE;
        if (XYZDistancePoints.size() > 0) {
            lastNode = XYZDistancePoints.last();
            lastDistance = lastNode.kdNode.id.euclideanDistance(value);
        }
        Double nodeDistance = node.id.euclideanDistance(value);
        if (nodeDistance.compareTo(lastDistance) < 0) {
            if (XYZDistancePoints.size() == K && lastNode != null)
                XYZDistancePoints.remove(lastNode);
            XYZDistancePoints.add(new XYZDistancePoint<>(node, nodeDistance));
        } else if (nodeDistance.equals(lastDistance)) {
            XYZDistancePoints.add(new XYZDistancePoint<>(node, nodeDistance));
        } else if (XYZDistancePoints.size() < K) {
            XYZDistancePoints.add(new XYZDistancePoint<>(node, nodeDistance));
        }
        lastNode = XYZDistancePoints.last();
        lastDistance = lastNode.kdNode.id.euclideanDistance(value);

        int axis = node.depth % node.k;
        KdNode lesser = node.lesser;
        KdNode greater = node.greater;

        // Search children branches, if axis aligned distance is less than
        // current distance
        if (lesser != null && !examined.contains(lesser)) {
            examined.add(lesser);

            double nodePoint;
            double valuePlusDistance;
            if (axis == X_AXIS) {
                nodePoint = node.id.x;
                valuePlusDistance = value.x - lastDistance;
            } else if (axis == Y_AXIS) {
                nodePoint = node.id.y;
                valuePlusDistance = value.y - lastDistance;
            } else {
                nodePoint = node.id.z;
                valuePlusDistance = value.z - lastDistance;
            }
            boolean lineIntersectsCube = ((valuePlusDistance <= nodePoint) ? true : false);

            // Continue down lesser branch
            if (lineIntersectsCube)
                searchNode(value, lesser, K, XYZDistancePoints, examined);
        }
        if (greater != null && !examined.contains(greater)) {
            examined.add(greater);

            double nodePoint;
            double valuePlusDistance;
            if (axis == X_AXIS) {
                nodePoint = node.id.x;
                valuePlusDistance = value.x + lastDistance;
            } else if (axis == Y_AXIS) {
                nodePoint = node.id.y;
                valuePlusDistance = value.y + lastDistance;
            } else {
                nodePoint = node.id.z;
                valuePlusDistance = value.z + lastDistance;
            }
            boolean lineIntersectsCube = ((valuePlusDistance >= nodePoint) ? true : false);

            // Continue down greater branch
            if (lineIntersectsCube)
                searchNode(value, greater, K, XYZDistancePoints, examined);
        }
    }

    /** 
     * Adds, in a specified queue, a given node and its related nodes (lesser, greater).
     * 
     * @param node 
     *              Node to check. May be null.
     * 
     * @param results 
     *              Queue containing all found entries. Must not be null.
     */
    @SuppressWarnings("unchecked")
    private static <T extends XYZPoint> void search(final KdNode node, final Deque<T> results) {
        if (node != null) {
            results.add((T) node.id);
            search(node.greater, results);
            search(node.lesser, results);
        }
    }

    @Override
    public String toString() {
        return TreePrinter.getString(this);
    }

    protected static class EuclideanComparator implements Comparator<XYZDistancePoint> {

        private final XYZPoint point;

        EuclideanComparator(XYZPoint point) {
            this.point = point;
        }

        @Override
        public int compare(XYZDistancePoint o1, XYZDistancePoint o2) {
            Double d1 = point.euclideanDistance(o1.kdNode.id);
            Double d2 = point.euclideanDistance(o2.kdNode.id);
            if (d1.compareTo(d2) < 0)
                return -1;
            else if (d2.compareTo(d1) < 0)
                return 1;
            return o1.kdNode.id.compareTo(o2.kdNode.id);
        }
    }

    /** 
     * Searches all entries from the first to the last entry.
     * 
     * @return Iterator 
     *                  allowing to iterate through a collection containing all found entries.
     */
    public Iterator<T> iterator() {
        final Deque<T> results = new ArrayDeque<>();
        search(root, results);
        return results.iterator();
    }

    /** 
     * Searches all entries from the last to the first entry.
     * 
     * @return Iterator 
     *                  allowing to iterate through a collection containing all found entries.
     */
    public Iterator<T> reverse_iterator() {
        final Deque<T> results = new ArrayDeque<>();
        search(root, results);
        return results.descendingIterator();
    }
}