package io.manojvivek.m2shredder.localrepo.impl;

import io.manojvivek.m2shredder.exception.M2PathException;
import io.manojvivek.m2shredder.localrepo.LocalRepoManager;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	public void initialize() {
		// TODO Scan the user home /.m2/repository and build the details

	}

}
