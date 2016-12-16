package com.cic.datacrawl.manager;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.EntitySaveManager;
import com.cic.datacrawl.ui.SwingGui;

public class ShowSaveDataToUIImpl extends EntitySaveManager {

	@Override
	public void save(BaseEntity entity) {
		SwingGui.getInstance().getOutputPanel().addItem(entity);
	}

	@Override
	public void save(BaseEntity[] entities) {
		SwingGui.getInstance().getOutputPanel().addItem(entities);
	}

	@Override
	public void commit() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clean() {
		SwingGui.getInstance().getOutputPanel().removeAll();
		
	}


}
