/**
 * Copyright (c) 2011 by InfoArmy Inc.  All Rights Reserved.
 * This file contains proprietary information of InfoArmy Inc.
 * Copying, use, reverse engineering, modification or reproduction of
 * this file without prior written approval is prohibited.
 *
 */
package io.manojvivek.m2shredder.localrepo.impl;

import io.manojvivek.m2shredder.exception.PomFinderException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author manojvivek
 *
 */
public class PomFinder {

	List<String> basePaths;
	String pattern;

	/**
	 * @param basePaths
	 */
	public PomFinder(List<String> basePaths, String pattern) {
		super();
		this.basePaths = basePaths;
		this.pattern = pattern;
	}

	public PomFinder(String basePath, String pattern) {
		super();
		this.basePaths = new ArrayList<String>();
		this.basePaths.add(basePath);
		this.pattern = pattern;
	}

	public List<String> findPoms() throws PomFinderException {
		_PomFinder finder = new _PomFinder(pattern);
		for (String pathString : basePaths) {
			Path path = Paths.get(pathString);
			try {
				Files.walkFileTree(path, finder);
			} catch (IOException e) {
				throw new PomFinderException(e);
			}
		}
		List<String> pomPathsString = new ArrayList<String>();
		for (Path path : finder.getPomPaths()) {
			pomPathsString.add(path.toString());
		}
		return pomPathsString;
	}

	class _PomFinder implements FileVisitor<Path> {
		String pattern;

		/**
		 * @param pattern
		 */
		public _PomFinder(String pattern) {
			super();
			this.pattern = pattern;
			this.pathMatcher = FileSystems.getDefault().getPathMatcher(pattern);
		}

		ArrayList<Path> pomPaths = new ArrayList<>();
		PathMatcher pathMatcher;

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			// System.out.println(file.toString());
			if (pathMatcher.matches(file)) {
				pomPaths.add(file);
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		public ArrayList<Path> getPomPaths() {
			return pomPaths;
		}

	}

}
