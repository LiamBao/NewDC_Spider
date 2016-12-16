package com.cic.datacrawl.ui.treecombo;

import java.util.Enumeration;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.tree.TreeModel;

public class TreeListModel extends AbstractListModel implements ComboBoxModel {
	private TreeModel treeModel;
	private Object selectedObject;


	public TreeListModel(TreeModel treeModel) {
		this.treeModel = treeModel;
	}

	 public int getSize(){ 
	        int count = 0; 
	        Enumeration enumer = new PreorderEnumeration(treeModel); 
	        while(enumer.hasMoreElements()){ 
	            enumer.nextElement(); 
	            count++; 
	        } 
	        return count; 
	    } 
	 
	    public Object getElementAt(int index){ 
	        Enumeration enumer = new PreorderEnumeration(treeModel); 
	        for(int i=0; i<index; i++) 
	            enumer.nextElement(); 
	        return enumer.nextElement(); 
	    } 

	public void setSelectedItem(Object anObject) {
		if ((selectedObject != null && !selectedObject.equals(anObject))
				|| selectedObject == null && anObject != null) {
			selectedObject = anObject;
			fireContentsChanged(this, -1, -1);
		}
	}

	public Object getSelectedItem() {
		return selectedObject;
	}
}