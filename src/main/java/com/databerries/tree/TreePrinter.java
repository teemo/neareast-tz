package com.databerries.tree;

import java.util.ArrayList;
import java.util.List;

public class TreePrinter {

    public static <T extends XYZPoint> String getString(KdTree<T> tree) {
        if (tree.root == null)
            return "Tree has no nodes.";
        return getString(tree.root, "", true);
    }

    private static String getString(KdNode node, String prefix, boolean isTail) {
        StringBuilder builder = new StringBuilder();

        if (node.parent != null) {
            String side = "left";
            if (node.parent.greater != null && node.id.equals(node.parent.greater.id))
                side = "right";
            builder.append(prefix)
                    .append(isTail ? "└── " : "├── ")
                    .append("[").append(side).append("] ")
                    .append("depth=").append(node.depth).append(" id=").append(node.id).append("\n");
        } else {
            builder.append(prefix)
                    .append(isTail ? "└── " : "├── ")
                    .append("depth=").append(node.depth).append(" id=").append(node.id).append("\n");
        }
        List<KdNode> children = null;
        if (node.lesser != null || node.greater != null) {
            children = new ArrayList<>(2);
            if (node.lesser != null)
                children.add(node.lesser);
            if (node.greater != null)
                children.add(node.greater);
        }
        if (children != null) {
            for (int i = 0; i < children.size() - 1; i++) {
                builder.append(getString(children.get(i), prefix + (isTail ? "    " : "│   "), false));
            }
            if (children.size() >= 1) {
                builder.append(getString(children.get(children.size() - 1), prefix + (isTail ? "    " : "│   "),
                        true));
            }
        }
        return builder.toString();
    }
}