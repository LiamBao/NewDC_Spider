package com.cic.datacrawl.manager;

import java.util.ArrayList;
import java.util.List;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.EntitySaveManager;

public class CombinSaveDataImpl extends EntitySaveManager {

	List<EntitySaveManager> saveImplList = new ArrayList<EntitySaveManager>();

	/**
	 * @return the saveImplList
	 */
	public List<EntitySaveManager> getSaveImplList() {
		return saveImplList;
	}

	/**
	 * @param saveImplList
	 *            the saveImplList to set
	 */
	public void setSaveImplList(List<EntitySaveManager> saveImplList) {
		this.saveImplList = saveImplList;
	}

	/**
	 * @param bufferSize
	 *            the bufferSize to set
	 */
	@Override
	public void setBufferSize(int bufferSize) {
		super.setBufferSize(bufferSize);

		for (int i = 0; i < saveImplList.size(); ++i) {
			saveImplList.get(i).setBufferSize(bufferSize);
		}
	}

	@Override
	public void save(final BaseEntity entity) {
		for (int i = 0; i < saveImplList.size(); ++i) {
			final EntitySaveManager saver = saveImplList.get(i);
			new Thread(new Runnable() {

				@Override
				public void run() {
					saver.save(entity);
				}
			}).start();
		}
	}

	@Override
	public void save(final BaseEntity[] entities) {

		for (int i = 0; i < saveImplList.size(); ++i) {
			final EntitySaveManager saver = saveImplList.get(i);
			new Thread(new Runnable() {

				@Override
				public void run() {
					saver.save(entities);
				}
			}).start();
		}
	}

	@Override
	public void commit() throws Exception {
		for (int i = 0; i < saveImplList.size(); ++i) {
			final EntitySaveManager saver = saveImplList.get(i);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						saver.commit();
					} catch (Exception e) {
						if (e instanceof RuntimeException)
							throw (RuntimeException) e;
						throw new RuntimeException(e);
					}
				}
			}).start();
		}
	}

	@Override
	public void clean() {
		for (int i = 0; i < saveImplList.size(); ++i) {
			final EntitySaveManager saver = saveImplList.get(i);
			new Thread(new Runnable() {

				@Override
				public void run() {
					saver.clean();

				}
			}).start();
		}
	}

}
