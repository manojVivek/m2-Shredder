/**
 * Copyright (c) 2011 by InfoArmy Inc.  All Rights Reserved.
 * This file contains proprietary information of InfoArmy Inc.
 * Copying, use, reverse engineering, modification or reproduction of
 * this file without prior written approval is prohibited.
 *
 */
package io.manojvivek.m2shredder.localrepo.impl;

import java.util.List;

/**
 * @author manojvivek
 *
 */
public class WorkspaceRepoPomXmlFinder extends PomFinder {
	private static final String pattern = "glob:**pom.xml";

	/**
	 * @param basePaths
	 * @param pattern
	 */
	public WorkspaceRepoPomXmlFinder(List<String> basePaths) {
		super(basePaths, pattern);
	}

	public WorkspaceRepoPomXmlFinder(String basePath) {
		super(basePath, pattern);
	}

}
