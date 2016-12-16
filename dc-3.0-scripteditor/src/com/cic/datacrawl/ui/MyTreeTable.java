package com.cic.datacrawl.ui;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.mozilla.javascript.tools.debugger.treetable.JTreeTable;
import org.mozilla.javascript.tools.debugger.treetable.TreeTableModel;
import org.mozilla.javascript.tools.debugger.treetable.TreeTableModelAdapter;

/**
 * A tree table for browsing script objects.
 */
public class MyTreeTable extends JTreeTable {

    /**
     * Serializable magic number.
     */
    private static final long serialVersionUID = 3457265548184453049L;

    /**
     * Creates a new MyTreeTable.
     */
    public MyTreeTable(VariableModel model) {
        super(model);
    }

    /**
     * Initializes a tree for this tree table.
     */
    public JTree resetTree(TreeTableModel treeTableModel) {
        tree = new TreeTableCellRenderer(treeTableModel);

        // Install a tableModel representing the visible rows in the tree.
        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

        // Force the JTable and JTree to share their row selection models.
        ListToTreeSelectionModelWrapper selectionWrapper = new
            ListToTreeSelectionModelWrapper();
        tree.setSelectionModel(selectionWrapper);
        setSelectionModel(selectionWrapper.getListSelectionModel());

        // Make the tree and table row heights the same.
        if (tree.getRowHeight() < 1) {
            // Metal looks better like this.
            setRowHeight(18);
        }

        // Install the tree editor renderer and editor.
        setDefaultRenderer(TreeTableModel.class, tree);
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());
        setShowGrid(true);
        setIntercellSpacing(new Dimension(1,1));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        DefaultTreeCellRenderer r = (DefaultTreeCellRenderer)tree.getCellRenderer();
        r.setOpenIcon(null);
        r.setClosedIcon(null);
        r.setLeafIcon(null);
        return tree;
    }

    /**
     * Returns whether the cell under the coordinates of the mouse
     * in the {@link EventObject} is editable.
     */
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent)e;
            // If the modifiers are not 0 (or the left mouse button),
            // tree may try and toggle the selection, and table
            // will then try and toggle, resulting in the
            // selection remaining the same. To avoid this, we
            // only dispatch when the modifiers are 0 (or the left mouse
            // button).
            if (me.getModifiers() == 0 ||
                ((me.getModifiers() & (InputEvent.BUTTON1_MASK|1024)) != 0 &&
                 (me.getModifiers() &
                  (InputEvent.SHIFT_MASK |
                   InputEvent.CTRL_MASK |
                   InputEvent.ALT_MASK |
                   InputEvent.BUTTON2_MASK |
                   InputEvent.BUTTON3_MASK |
                   64   | //SHIFT_DOWN_MASK
                   128  | //CTRL_DOWN_MASK
                   512  | // ALT_DOWN_MASK
                   2048 | //BUTTON2_DOWN_MASK
                   4096   //BUTTON3_DOWN_MASK
                   )) == 0)) {
                int row = rowAtPoint(me.getPoint());
                for (int counter = getColumnCount() - 1; counter >= 0;
                     counter--) {
                    if (TreeTableModel.class == getColumnClass(counter)) {
                        MouseEvent newME = new MouseEvent
                            (MyTreeTable.this.tree, me.getID(),
                             me.getWhen(), me.getModifiers(),
                             me.getX() - getCellRect(row, counter, true).x,
                             me.getY(), me.getClickCount(),
                             me.isPopupTrigger());
                        MyTreeTable.this.tree.dispatchEvent(newME);
                        break;
                    }
                }
            }
            if (me.getClickCount() >= 3) {
                return true;
            }
            return false;
        }
        if (e == null) {
            return true;
        }
        return false;
    }

}


