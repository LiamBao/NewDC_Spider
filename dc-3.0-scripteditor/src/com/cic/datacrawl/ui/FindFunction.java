package com.cic.datacrawl.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import com.cic.datacrawl.core.util.StringUtil;

/**
 * Find function dialog.
 */
public class FindFunction extends JDialog implements ActionListener {

	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = 559491015232880916L;

	/**
	 * Last selected function.
	 */
	private String value;

	// /**
	// * List of functions.
	// */
	// private JList list;

	private JLabel lblScriptFilterInput;
	private JComboBox txtScriptFilterInput;
	private JLabel lblFunctionFilterInput;
	private JTextField txtFunctionFilterInput;
	/**
	 * List of functions.
	 */
	private JTable table;
	/**
	 * The debug GUI frame.
	 */
	private SwingGui debugGui;

	/**
	 * The "Select" button.
	 */
	private JButton setButton;

	/**
	 * The "Cancel" button.
	 */
	private JButton cancelButton;

	private FunctionTableModel model;

	private JButton btnFilter;
	private ActionListener filterActionListener;
	private IndexTableRowHeader indexTableRowHeader;

	/**
	 * Creates a new FindFunction.
	 */
	public FindFunction(SwingGui debugGui, String title) {
		super(debugGui, title, true);

		java.awt.GridBagConstraints gridBagConstraints;
		this.debugGui = debugGui;

		cancelButton = new JButton("Cancel");
		setButton = new JButton("Select");
		cancelButton.addActionListener(this);
		setButton.addActionListener(this);
		getRootPane().setDefaultButton(setButton);
		setLayout(new java.awt.GridBagLayout());

		JPanel filterPane = new javax.swing.JPanel();
		JLabel lblSearchString = new javax.swing.JLabel();
		lblScriptFilterInput = new javax.swing.JLabel();
		lblFunctionFilterInput = new javax.swing.JLabel();
		txtScriptFilterInput = new JComboBox();
		txtFunctionFilterInput = new javax.swing.JTextField();
		btnFilter = new javax.swing.JButton();

		filterPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter")); // NOI18N
		filterPane.setName("filterPane"); // NOI18N
		filterPane.setLayout(new java.awt.GridBagLayout());

		lblSearchString.setText("Filter String: (*=any string, ?=any character)"); // NOI18N
		lblSearchString.setName("lblScriptFilterInput"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
		filterPane.add(lblSearchString, gridBagConstraints);

		lblScriptFilterInput.setText("Script Name:"); // NOI18N
		lblScriptFilterInput.setName("lblScriptFilterInput"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
		filterPane.add(lblScriptFilterInput, gridBagConstraints);

		lblFunctionFilterInput.setText("Function Name:"); // NOI18N
		lblFunctionFilterInput.setName("lblFunctionFilterInput"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
		filterPane.add(lblFunctionFilterInput, gridBagConstraints);

		txtScriptFilterInput.setName("txtScriptFilterInput"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
		filterPane.add(txtScriptFilterInput, gridBagConstraints);

		txtFunctionFilterInput.setText(""); // NOI18N
		txtFunctionFilterInput.setName("txtFunctionFilterInput"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 5);
		filterPane.add(txtFunctionFilterInput, gridBagConstraints);

		btnFilter.setText("Filter"); // NOI18N
		btnFilter.setName("btnFilter"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
		filterPane.add(btnFilter, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 5);
		getContentPane().add(filterPane, gridBagConstraints);

		model = new FunctionTableModel();

		table = new JTable(model) {
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = rowAtPoint(p);
				int col = columnAtPoint(p);

				if (row == -1 || col == -1) {
					return "";
				}

				return (String) table.getValueAt(row, FunctionTableModel.FULL_PATH_COLUMN_INDEX);
			}

			public Point getToolTipLocation(MouseEvent event) {
				Point p = event.getPoint();
				int row = rowAtPoint(p);

				if (row == -1) {
					return null;
				}
				Rectangle cellRect = getCellRect(row, 0, true);
				return new Point(cellRect.x + cellRect.width / 4, cellRect.y + cellRect.height / 2);
			}
		};
		model.clear();

		String[] a = debugGui.dim.functionNames();
		java.util.Arrays.sort(a);
		for (int i = 0; i < a.length; i++) {
			model.addFunction(a[i]);
		}
		table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				int code = ke.getKeyCode();
				if (code == KeyEvent.VK_ESCAPE) {
					ke.consume();
					value = null;
					setVisible(false);
				} else if (code == KeyEvent.VK_ENTER) {
					setButton.doClick();
				}
			}
		});

		TableRowSorter<FunctionTableModel> rowSorter = new TableRowSorter<FunctionTableModel>(model);

		table.setRowSorter(rowSorter);
		filterActionListener = new ActionListener() {

			private String buildRegExString(String str) {
				str = StringUtil.replaceAll(str, "\\", "\\\\");
				str = StringUtil.replaceAll(str, ".", "\\.");
				str = StringUtil.replaceAll(str, "*", ".*");
				str = StringUtil.replaceAll(str, "+", ".+");
				str = StringUtil.replaceAll(str, "$", "\\$");
				str = StringUtil.replaceAll(str, "^", "\\^");
				str = StringUtil.replaceAll(str, "(", "\\(");
				str = StringUtil.replaceAll(str, ")", "\\)");
				str = StringUtil.replaceAll(str, "[", "\\[");
				str = StringUtil.replaceAll(str, "]", "\\]");
				str = StringUtil.replaceAll(str, "{", "\\{");
				str = StringUtil.replaceAll(str, "}", "\\}");
				str = StringUtil.replaceAll(str, "|", "\\|");
				str = StringUtil.replaceAll(str, "?", ".?");
				return "^(?i)" + str + ".*$";
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				TableRowSorter<FunctionTableModel> theRowSorter = (TableRowSorter<FunctionTableModel>) table
						.getRowSorter();
				String functionReg = txtFunctionFilterInput.getText();
				String scriptReg = (String) txtScriptFilterInput.getSelectedItem();

				boolean hasFunctionReg = functionReg != null && functionReg.length() > 0;

				boolean hasScriptReg = scriptReg != null && scriptReg.length() > 0;

				if (hasFunctionReg && hasScriptReg) {
					List<RowFilter<Object, Object>> list = new ArrayList<RowFilter<Object, Object>>();
					list.add(RowFilter.regexFilter(buildRegExString(functionReg),
													FunctionTableModel.FUNCTION_COLUMN_INDEX));

					list.add(RowFilter.regexFilter(buildRegExString(scriptReg),
													FunctionTableModel.SCRIPT_COLUMN_INDEX));

					RowFilter<Object, Object> andFilter = RowFilter.andFilter(list);

					theRowSorter.setRowFilter(andFilter);

				} else if (hasFunctionReg) {
					theRowSorter
							.setRowFilter(RowFilter.regexFilter(buildRegExString(functionReg),
																FunctionTableModel.FUNCTION_COLUMN_INDEX));

				} else if (hasScriptReg) {
					theRowSorter.setRowFilter(RowFilter.regexFilter(buildRegExString(scriptReg),
																	FunctionTableModel.SCRIPT_COLUMN_INDEX));

				} else {
					theRowSorter.setRowFilter(null);
				}
			}
		};
		btnFilter.addActionListener(filterActionListener);

		String[] scripts = model.getScriptNames();
		if (scripts != null)
			for (int i = 0; i < scripts.length; ++i)
				txtScriptFilterInput.addItem(scripts[i]);
		txtScriptFilterInput.setEditable(true);
		txtScriptFilterInput.setSelectedIndex(-1);

		txtScriptFilterInput.addKeyListener(new KeyAdapter() {
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
		txtFunctionFilterInput.addKeyListener(new KeyAdapter() {
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

		setButton.setEnabled(a.length > 0);
		table.addMouseListener(new MouseHandler());

		JScrollPane listScroller = new JScrollPane(table);
		listScroller.setPreferredSize(new Dimension(320, 240));
		listScroller.setMinimumSize(new Dimension(250, 80));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);
		// indexTableRowHeader = new IndexTableRowHeader(table);
		// listScroller.setRowHeaderView(indexTableRowHeader);
		// 
		// model.addTableModelListener(new TableModelListener() {
		//			
		// @Override
		// public void tableChanged(TableModelEvent tablemodelevent) {
		// indexTableRowHeader.setWidthByNumber(model.getRowCount());
		// }
		// });

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 7, 3, 7);
		add(listScroller, gridBagConstraints);

		// Lay out the buttons from left to right.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(setButton);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
		add(buttonPane, gridBagConstraints);
		setSize(400, 600);
		addEscKeyListener();
	}

	public void setScriptFilterString(String scriptFilterStr) {
		txtScriptFilterInput.getEditor().setItem(scriptFilterStr);
	}

	public void setFunctionFilterString(String functionFilterStr) {
		txtFunctionFilterInput.setText(functionFilterStr);
	}

	public void doFilter() {
		filterActionListener.actionPerformed(null);
	}

	private void addEscKeyListener() {
		addKeyListener(escKeyListener);
		txtScriptFilterInput.getEditor().getEditorComponent().addKeyListener(escKeyListener);
		table.addKeyListener(escKeyListener);
		btnFilter.addKeyListener(escKeyListener);
		setButton.addKeyListener(escKeyListener);

		cancelButton.addKeyListener(escKeyListener);
		txtFunctionFilterInput.addKeyListener(escKeyListener);
	}

	private KeyAdapter escKeyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent ke) {
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_ESCAPE) {
				ke.consume();
				value = null;
				setVisible(false);
			}
		}
	};

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
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			try {
				value = (String) table.getValueAt(selectedRow, FunctionTableModel.FULL_NAME_COLUMN_INDEX);
			} catch (ArrayIndexOutOfBoundsException exc) {
				return;
			}
			debugGui.locationToFunction(value);
			setVisible(false);
		}
	}

	/**
	 * MouseListener implementation for {@link #list}.
	 */
	class MouseHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				setButton.doClick();
			}
		}
	}
}
