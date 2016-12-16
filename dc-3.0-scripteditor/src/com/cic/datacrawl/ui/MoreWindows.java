package com.cic.datacrawl.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


/**
 * Dialog to list the available windows.
 */
public class MoreWindows extends JDialog implements ActionListener {

    /**
     * Serializable magic number.
     */
    private static final long serialVersionUID = 5177066296457377546L;

    /**
     * Last selected value.
     */
    private String value;

    /**
     * The list component.
     */
    private JList list;

    /**
     * Our parent frame.
     */
    private SwingGui swingGui;

    /**
     * The "Select" button.
     */
    private JButton setButton;

    /**
     * The "Cancel" button.
     */
    private JButton cancelButton;

    /**
     * Creates a new MoreWindows.
     */
    public MoreWindows(SwingGui frame, Map<String,FileWindow> fileWindows, String title,
                String labelText) {
        super(frame, title, true);
        this.swingGui = frame;
        //buttons
        cancelButton = new JButton("Cancel");
        setButton = new JButton("Select");
        cancelButton.addActionListener(this);
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);

        //dim part of the dialog
        list = new JList(new DefaultListModel());
        DefaultListModel model = (DefaultListModel)list.getModel();
        model.clear();
        //model.fireIntervalRemoved(model, 0, size);
        for (String data: fileWindows.keySet()) {
            model.addElement(data);
        }
        list.setSelectedIndex(0);
        //model.fireIntervalAdded(model, 0, data.length);
        setButton.setEnabled(true);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.addMouseListener(new MouseHandler());
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(320, 240));
        //XXX: Must do the following, too, or else the scroller thinks
        //XXX: it's taller than it is:
        listScroller.setMinimumSize(new Dimension(250, 80));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the
        //scroll pane because its background would be white.
        //Lay out the label and scroll pane from top to button.
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setLabelFor (list);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(setButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);
        //pack();
        setSize(900, 500);
        addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent ke) {
                    int code = ke.getKeyCode();
                    if (code == KeyEvent.VK_ESCAPE) {
                        ke.consume();
                        value = null;
                        setVisible(false);
                    }
                }
            });
    }

    /**
     * Shows the dialog.
     */
    public String showDialog(Component comp) {
        value = null;
        setLocationRelativeTo(comp);
        setVisible(true);
        return value;
    }

    // ActionListener

    /**
     * Performs an action.
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Cancel")) {
            setVisible(false);
            value = null;
        } else if (cmd.equals("Select")) {
            value = (String)list.getSelectedValue();
            setVisible(false);
            swingGui.showFileWindow(value, -1);
        }
    }

    /**
     * MouseListener implementation for {@link #list}.
     */
    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                setButton.doClick();
            }
        }
    }
}

