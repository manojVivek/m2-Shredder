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
public class LocalRepoPomFinder extends PomFinder {
	private static final String pattern = "glob:**.pom";

	public LocalRepoPomFinder(List<String> basePaths) {
		super(basePaths, pattern);

	}

	public LocalRepoPomFinder(String basePath) {
		super(basePath, pattern);

	}
}
