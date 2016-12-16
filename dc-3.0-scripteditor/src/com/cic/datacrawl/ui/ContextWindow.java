package com.cic.datacrawl.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.ResizeableToolbarUI;

import com.cic.datacrawl.core.rhino.debugger.RhinoDim;


/**
 * Panel that shows information about the context.
 */
public class ContextWindow extends JPanel implements ActionListener {

    /**
     * Serializable magic number.
     */
    private static final long serialVersionUID = 2306040975490228051L;

    /**
     * The debugger GUI.
     */
    private SwingGui debugGui;

    /**
     * The combo box that holds the stack frames.
     */
    public JComboBox context;

    /**
     * Tool tips for the stack frames.
     */
    public List<String> toolTips;

    /**
     * Tabbed pane for "this" and "locals".
     */
    private JTabbedPane variablesTab;

    /**
     * Tabbed pane for "watch" and "evaluate".
     */
    private JTabbedPane evaluateTabs;

    /**
     * The table showing the "this" object.
     */
    private MyTreeTable thisTable;

    /**
     * The table showing the stack local variables.
     */
    private MyTreeTable localsTable;

    /**
     * The {@link #evaluator}'s table model.
     */
	private MyTableModel watchTableModel;

    /**
     * The script evaluator table.
     */
    private Evaluator evaluator;

    /**
     * The script evaluation text area.
     */
    private EvalTextArea cmdLine;

    /**
     * The split pane.
     */
    public JSplitPane hSplit;


    /**
     * Whether the ContextWindow is enabled.
     */
    private boolean enabled;




	/**
     * Creates a new ContextWindow.
     */
    public ContextWindow(final SwingGui debugGui) {
        this.debugGui = debugGui;
        enabled = false;
        JPanel left = new JPanel();
        JToolBar variablesToolbar = new JToolBar();
        variablesToolbar.setUI(new ResizeableToolbarUI());
        variablesToolbar.setName("Variables");
        variablesToolbar.setLayout(new GridLayout());
        variablesToolbar.add(left);
        JPanel variablesPanel = new JPanel();
        variablesPanel.setLayout(new GridLayout());
        JPanel evaluatePanel = new JPanel();
        evaluatePanel.setLayout(new GridLayout());
        variablesPanel.add(variablesToolbar);
        JLabel label = new JLabel("Context:");
        context = new JComboBox();
        context.setLightWeightPopupEnabled(false);
        toolTips = Collections.synchronizedList(new java.util.ArrayList<String>());
        label.setBorder(context.getBorder());
        context.addActionListener(this);
        context.setActionCommand("ContextSwitch");
        GridBagLayout layout = new GridBagLayout();
        left.setLayout(layout);
        GridBagConstraints lc = new GridBagConstraints();
        lc.insets.left = 5;
        lc.anchor = GridBagConstraints.WEST;
        lc.ipadx = 5;
        layout.setConstraints(label, lc);
        left.add(label);
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(context, c);
        left.add(context);
        variablesTab = new JTabbedPane(SwingConstants.BOTTOM);
        variablesTab.setPreferredSize(new Dimension(300,150));
        thisTable = new MyTreeTable(new VariableModel());
//        watchPanel.getViewport().setViewSize(new Dimension(5,2));
        variablesTab.add("this", new JScrollPane(thisTable));
        localsTable = new MyTreeTable(new VariableModel());
        localsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        localsTable.setPreferredSize(null);
//        watchPanel = new JScrollPane(localsTable);
        variablesTab.add("Locals", new JScrollPane(localsTable));
        c.weightx  = c.weighty = 1;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(variablesTab, c);
        left.add(variablesTab);
        watchTableModel = new MyTableModel(debugGui);
        evaluator = new Evaluator(watchTableModel);
        cmdLine = new EvalTextArea(debugGui);
        //cmdLine.requestFocus();
        JToolBar evaluateToolbar = new JToolBar();
        evaluateToolbar.setUI(new ResizeableToolbarUI());
        evaluateToolbar.setName("Evaluate");
        evaluateTabs = new JTabbedPane(SwingConstants.BOTTOM);
        evaluateTabs.add("Watch", new JScrollPane(evaluator));
        evaluateTabs.add("Evaluate", new JScrollPane(cmdLine));
        evaluateTabs.setPreferredSize(new Dimension(300,150));
        evaluateToolbar.setLayout(new GridLayout());
        evaluateToolbar.add(evaluateTabs);
        evaluatePanel.add(evaluateToolbar);
        evaluator.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                               variablesPanel, evaluatePanel);
		
        hSplit.setOneTouchExpandable(true);
        SwingGui.setResizeWeight(hSplit, 0.5);
        
        setLayout(new BorderLayout());
        
        add(hSplit, BorderLayout.CENTER);

        //FIXME change Toolbar can undock. 
//        variablesToolbar.setFloatable(false);
//        evaluateToolbar.setFloatable(false);
        
        final JToolBar finalVariablesToolbar = variablesToolbar;
        final JToolBar finalEvaluateToolbar = evaluateToolbar;
        final JPanel finalVariablesPanel = variablesPanel;
        final JPanel finalEvaluatePanel = evaluatePanel;
        final JSplitPane finalSplit = hSplit;
        final JPanel finalThis = this;

      
        ComponentListener clistener = new ComponentListener() {
                boolean t2Docked = true;
                void check(Component comp) {
                    Component thisParent = finalThis.getParent();
                    if (thisParent == null) {
                        return;
                    }
                    Component parent = finalVariablesToolbar.getParent();
                    boolean leftDocked = true;
                    boolean rightDocked = true;
                    boolean adjustVerticalSplit = false;
                    if (parent != null) {
                        if (parent != finalVariablesPanel) {
                            while (!(parent instanceof JFrame)) {
                                parent = parent.getParent();
                            }
                            JFrame frame = (JFrame)parent;
                            debugGui.addTopLevel("Variables", frame);

                            // We need the following hacks because:
                            // - We want an undocked toolbar to be
                            //   resizable.
                            // - We are using JToolbar as a container of a
                            //   JComboBox. Without this JComboBox's popup
                            //   can get left floating when the toolbar is
                            //   re-docked.
                            //
                            // We make the frame resizable and then
                            // remove JToolbar's window listener
                            // and insert one of our own that first ensures
                            // the JComboBox's popup window is closed
                            // and then calls JToolbar's window listener.
                            if (!frame.isResizable()) {
                                frame.setResizable(true);
                                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                                final EventListener[] l =
                                    frame.getListeners(WindowListener.class);
                                frame.removeWindowListener((WindowListener)l[0]);
                                frame.addWindowListener(new WindowAdapter() {
                                        @Override
                                        public void windowClosing(WindowEvent e) {
                                            context.hidePopup();
                                            ((WindowListener)l[0]).windowClosing(e);
                                        }
                                    });
                                //adjustVerticalSplit = true;
                            }
                            leftDocked = false;
                        } else {
                            leftDocked = true;
                        }
                    }
                    parent = finalEvaluateToolbar.getParent();
                    if (parent != null) {
                        if (parent != finalEvaluatePanel) {
                            while (!(parent instanceof JFrame)) {
                                parent = parent.getParent();
                            }
                            JFrame frame = (JFrame)parent;
                            debugGui.addTopLevel("Evaluate", frame);
                            frame.setResizable(true);
                            rightDocked = false;
                        } else {
                            rightDocked = true;
                        }
                    }
                    if (leftDocked && t2Docked && rightDocked && t2Docked) {
                        // no change
                        return;
                    }
                    t2Docked = rightDocked;
                    JSplitPane split = (JSplitPane)thisParent;
                    if (leftDocked) {
                        if (rightDocked) {
                            finalSplit.setDividerLocation(0.5);
                        } else {
                            finalSplit.setDividerLocation(1.0);
                        }
                        if (adjustVerticalSplit) {
                            split.setDividerLocation(0.66);
                        }

                    } else if (rightDocked) {
                            finalSplit.setDividerLocation(0.0);
                            split.setDividerLocation(0.66);
                    } else {
                        // both undocked
                        split.setDividerLocation(1.0);
                    }
                }
                public void componentHidden(ComponentEvent e) {
                    check(e.getComponent());
                }
                public void componentMoved(ComponentEvent e) {
                    check(e.getComponent());
                }
                public void componentResized(ComponentEvent e) {
                    check(e.getComponent());
                }
                public void componentShown(ComponentEvent e) {
                    check(e.getComponent());
                }
            };
        variablesPanel.addContainerListener(new ContainerListener() {
            public void componentAdded(ContainerEvent e) {
                Component thisParent = finalThis.getParent();
                JSplitPane split = (JSplitPane)thisParent;
                if (e.getChild() == finalVariablesToolbar) {
                    if (finalEvaluateToolbar.getParent() == finalEvaluatePanel) {
                        // both docked
                        finalSplit.setDividerLocation(0.5);
                    } else {
                        // left docked only
                        finalSplit.setDividerLocation(1.0);
                    }
                    split.setDividerLocation(0.66);
                }
            }
            public void componentRemoved(ContainerEvent e) {
                Component thisParent = finalThis.getParent();
                JSplitPane split = (JSplitPane)thisParent;
                if (e.getChild() == finalVariablesToolbar) {
                    if (finalEvaluateToolbar.getParent() == finalEvaluatePanel) {
                        // right docked only
                        finalSplit.setDividerLocation(0.0);
                        split.setDividerLocation(0.66);
                    } else {
                        // both undocked
                        split.setDividerLocation(1.0);
                    }
                }
            }
            });
        variablesToolbar.addComponentListener(clistener);
        evaluateToolbar.addComponentListener(clistener);
        disable();
    }

    /**
     * Disables the component.
     */
    @Override
    public void disable() {
        context.setEnabled(false);
        thisTable.setEnabled(false);
        localsTable.setEnabled(false);
        evaluator.setEnabled(false);
        cmdLine.setEnabled(false);       
    }

    /**
     * Enables the component.
     */
    @Override
    public void enable() {
        context.setEnabled(true);
        thisTable.setEnabled(true);
        localsTable.setEnabled(true);
        evaluator.setEnabled(true);
        cmdLine.setEnabled(true);     
    }

    /**
     * Disables updating of the component.
     */
    public void disableUpdate() {
        enabled = false;
    }

    /**
     * Enables updating of the component.
     */
    public void enableUpdate() {
        enabled = true;
    }

    // ActionListener

    /**
     * Performs an action.
     */
    public void actionPerformed(ActionEvent e) {
        if (!enabled) return;
        if (e.getActionCommand().equals("ContextSwitch")) {
        	changeContextScope();
        }
    }

    private void changeContextScope(){
    	RhinoDim.ContextData contextData = debugGui.dim.currentContextData();
        if (contextData == null) { return; }
        int frameIndex = context.getSelectedIndex();
        context.setToolTipText(toolTips.get(frameIndex));
        int frameCount = contextData.frameCount();
        if (frameIndex >= frameCount) {
            return;
        }
        RhinoDim.StackFrame frame = contextData.getFrame(frameIndex);
        Object scope = frame.scope();
        Object thisObj = frame.thisObj();
        thisTable.resetTree(new VariableModel(debugGui.dim, thisObj));
        VariableModel scopeModel;
        if (scope != thisObj) {
            scopeModel = new VariableModel(debugGui.dim, scope);
        } else {
            scopeModel = new VariableModel();
        }            
        localsTable.resetTree(scopeModel);
        watchTableModel.changeScope(frame);
        debugGui.dim.contextSwitch(frameIndex);
        debugGui.showStopLine(frame);
       // watchTableModel.updateModel();
        
    }
    
	public void addWatch(String watchString) {
		if(evaluator.isEnabled())
			evaluator.addWatch(watchString);		
	}

}
