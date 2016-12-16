package com.cic.datacrawl.core.browser.listener;

import com.cic.datacrawl.core.browser.entity.DomNode;
import com.cic.datacrawl.core.browser.entity.IFrameName;
import com.cic.datacrawl.core.browser.entity.NodePath;

/**
 * 用于支持RobotEditor的浏览器事件监听器
 */
public interface RobotEditorListener {
	void nodeClick(DomNode rootNode, NodePath nodePath, IFrameName frameName);

	void locationChanged();
	void locationChanging();
}
