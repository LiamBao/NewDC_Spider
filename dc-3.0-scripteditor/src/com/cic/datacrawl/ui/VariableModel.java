package com.cic.datacrawl.ui;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.mozilla.javascript.tools.debugger.treetable.TreeTableModel;

import com.cic.datacrawl.core.rhino.debugger.RhinoDim;

/**
 * Tree model for script object inspection.
 */
public class VariableModel implements TreeTableModel {

    /**
     * Serializable magic number.
     */
    private static final String[] cNames = { " Name", " Value" };

    /**
     * Tree column types.
     */
    private static final Class<?>[] cTypes =
        { TreeTableModel.class, String.class };

    /**
     * Empty {@link VariableNode} array.
     */
    private static final VariableNode[] CHILDLESS = new VariableNode[0];

    /**
     * The debugger.
     */
    private RhinoDim debugger;

    /**
     * The root node.
     */
    private VariableNode root;

    /**
     * Creates a new VariableModel.
     */
    public VariableModel() {
    }

    /**
     * Creates a new VariableModel.
     */
    public VariableModel(RhinoDim debugger, Object scope) {
        this.debugger = debugger;
        this.root = new VariableNode(scope, "this");
    }

    // TreeTableModel

    /**
     * Returns the root node of the tree.
     */
    public Object getRoot() {
        if (debugger == null) {
            return null;
        }
        return root;
    }

    /**
     * Returns the number of children of the given node.
     */
    public int getChildCount(Object nodeObj) {
        if (debugger == null) {
            return 0;
        }
        VariableNode node = (VariableNode) nodeObj;
        return children(node).length;
    }

    /**
     * Returns a child of the given node.
     */
    public Object getChild(Object nodeObj, int i) {
        if (debugger == null) {
            return null;
        }
        VariableNode node = (VariableNode) nodeObj;
        return children(node)[i];
    }

    /**
     * Returns whether the given node is a leaf node.
     */
    public boolean isLeaf(Object nodeObj) {
        if (debugger == null) {
            return true;
        }
        VariableNode node = (VariableNode) nodeObj;
        return children(node).length == 0;
    }

    /**
     * Returns the index of a node under its parent.
     */
    public int getIndexOfChild(Object parentObj, Object childObj) {
        if (debugger == null) {
            return -1;
        }
        VariableNode parent = (VariableNode) parentObj;
        VariableNode child = (VariableNode) childObj;
        VariableNode[] children = children(parent);
        for (int i = 0; i != children.length; i++) {
            if (children[i] == child) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns whether the given cell is editable.
     */
    public boolean isCellEditable(Object node, int column) {
        return column == 0;
    }

    /**
     * Sets the value at the given cell.
     */
    public void setValueAt(Object value, Object node, int column) { }

    /**
     * Adds a TreeModelListener to this tree.
     */
    public void addTreeModelListener(TreeModelListener l) { }

    /**
     * Removes a TreeModelListener from this tree.
     */
    public void removeTreeModelListener(TreeModelListener l) { }

    public void valueForPathChanged(TreePath path, Object newValue) { }

    // TreeTableNode

    /**
     * Returns the number of columns.
     */
    public int getColumnCount() {
        return cNames.length;
    }

    /**
     * Returns the name of the given column.
     */
    public String getColumnName(int column) {
        return cNames[column];
    }

    /**
     * Returns the type of value stored in the given column.
     */
    public Class<?> getColumnClass(int column) {
        return cTypes[column];
    }

    /**
     * Returns the value at the given cell.
     */
    public Object getValueAt(Object nodeObj, int column) {
        if (debugger == null) { return null; }
        VariableNode node = (VariableNode)nodeObj;
        switch (column) {
        case 0: // Name
            return node.toString();
        case 1: // Value
            String result;
            try {
                result = debugger.objectToString(getValue(node));
            } catch (RuntimeException exc) {
                result = exc.getMessage();
            }
            StringBuffer buf = new StringBuffer();
            int len = result.length();
            for (int i = 0; i < len; i++) {
                char ch = result.charAt(i);
                if (Character.isISOControl(ch)) {
                    ch = ' ';
                }
                buf.append(ch);
            }
            return buf.toString();
        }
        return null;
    }

    /**
     * Returns an array of the children of the given node.
     */
    private VariableNode[] children(VariableNode node) {
        if (node.children != null) {
            return node.children;
        }

        VariableNode[] children;

        Object value = getValue(node);
        Object[] ids = debugger.getObjectIds(value);
        if (ids == null || ids.length == 0) {
            children = CHILDLESS;
        } else {
            Arrays.sort(ids, new Comparator<Object>() {
                    public int compare(Object l, Object r)
                    {
                        if (l instanceof String) {
                            if (r instanceof Integer) {
                                return -1;
                            }
                            return ((String)l).compareToIgnoreCase((String)r);
                        } else {
                            if (r instanceof String) {
                                return 1;
                            }
                            int lint = ((Integer)l).intValue();
                            int rint = ((Integer)r).intValue();
                            return lint - rint;
                        }
                    }
            });
            children = new VariableNode[ids.length];
            for (int i = 0; i != ids.length; ++i) {
                children[i] = new VariableNode(value, ids[i]);
            }
        }
        node.children = children;
        return children;
    }

    /**
     * Returns the value of the given node.
     */
    public Object getValue(VariableNode node) {
        try {
            return debugger.getObjectProperty(node.object, node.id);
        } catch (Exception exc) {
            return "undefined";
        }
    }

    /**
     * A variable node in the tree.
     */
    private static class VariableNode {

        /**
         * The script object.
         */
        private Object object;

        /**
         * The object name.  Either a String or an Integer.
         */
        private Object id;

        /**
         * Array of child nodes.  This is filled with the properties of
         * the object.
         */
        private VariableNode[] children;

        /**
         * Creates a new VariableNode.
         */
        public VariableNode(Object object, Object id) {
            this.object = object;
            this.id = id;
        }

        /**
         * Returns a string representation of this node.
         */
        @Override
        public String toString() {
            return id instanceof String
                ? (String) id : "[" + ((Integer) id).intValue() + "]";
        }
    }
}

