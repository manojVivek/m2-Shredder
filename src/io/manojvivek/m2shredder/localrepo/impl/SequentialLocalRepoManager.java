package io.manojvivek.m2shredder.localrepo.impl;

import io.manojvivek.m2shredder.exception.M2PathException;
import io.manojvivek.m2shredder.localrepo.LocalRepoManager;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.plexus.util.StringUtils;

/**
 * @author manojvivek
 * 
 */
public class SequentialLocalRepoManager implements LocalRepoManager {

	private Logger logger = Logger.getLogger(SequentialLocalRepoManager.class.getName());

	public String m2RepoPath;

	public SequentialLocalRepoManager(String m2DirPath) throws M2PathException {
		super();
		if (!m2DirPath.endsWith("/")) {
			m2DirPath = m2DirPath.concat("/");
		}
		this.m2RepoPath = validateM2Path(m2DirPath);
	}

	public SequentialLocalRepoManager() throws M2PathException {
		super();
		String userHome = System.getProperty("user.home");
		if (StringUtils.isEmpty(userHome)) {
			throw new M2PathException();
		}
		if (!userHome.endsWith("/")) {
			userHome = userHome.concat("/");
		}
		String m2Path = userHome.concat(".m2/");

		this.m2RepoPath = validateM2Path(m2Path);
	}

	/**
	 * @param m2Path
	 */
	private String validateM2Path(String m2Path) throws M2PathException {
		if(StringUtils.isEmpty(m2Path)) {
			throw new M2PathException("Specified M2 Path is empty");
		}
		try {
			Path path = Paths.get(m2Path, _REPOSITORY_STRING);
			if (Files.exists(path)) {
				logger.info("M2Repo path:" + path.toString());
				return path.toString();
			} else {
				throw new M2PathException("repository directory in m2 dir doesn't exists");
			}
		} catch (InvalidPathException e) {
			logger.log(Level.SEVERE, "Invalid .m2 path" + e.getMessage(), e);
			throw new M2PathException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.manojvivek.m2shredder.localrepo.LocalRepoManager#initialize()
	 */
	@Override
	public void initialize() {
		// TODO Scan the user home /.m2/repository and build the details
		Path path = Paths.get(m2RepoPath);
		try {
			PomFinder finder = new PomFinder();
			Files.walkFileTree(path, finder);
			System.out.println(finder.getPomPaths().size() + " " + finder.getPomPaths());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class PomFinder implements FileVisitor<Path> {
		
		ArrayList<Path> pomPaths = new ArrayList<>();
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pom");

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
