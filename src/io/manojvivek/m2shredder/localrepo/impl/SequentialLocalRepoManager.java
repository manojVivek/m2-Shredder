package io.manojvivek.m2shredder.localrepo.impl;

import io.manojvivek.m2shredder.exception.M2PathException;
import io.manojvivek.m2shredder.exception.PomFinderException;
import io.manojvivek.m2shredder.localrepo.LocalRepoManager;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author manojvivek
 * 
 */
public class SequentialLocalRepoManager implements LocalRepoManager {

	private static final char PATH_SEPARATOR = '/';

	private static final char ARTIFACT_SEPARATOR = '-';

	private Pattern VERSION_FILE_PATTERN = Pattern.compile("^(.*)-([0-9]{8}.[0-9]{6})-([0-9]+)$");

	private Logger logger = Logger.getLogger(SequentialLocalRepoManager.class.getName());

	public String m2RepoPath;

	private List<String> poms;

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
		if (StringUtils.isEmpty(m2Path)) {
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
			// logger.log(Level.SEVERE, "Invalid .m2 path" + e.getMessage(), e);
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

		LocalRepoPomFinder pomFinder = new LocalRepoPomFinder(m2RepoPath);
		try {
			poms = pomFinder.findPoms();
			System.out.println(".m2 poms:" + poms.size() + " files:" + poms);
		} catch (PomFinderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean hasArtifact(Dependency dependency) {
		String dependencyPomPath = getLocalRepoPomPath(dependency);
		return poms.contains(dependencyPomPath);
	}

	/**
	 * @param dependency
	 * @return
	 */
	public String getLocalRepoPomPath(Dependency dependency) {

		StringBuilder path = new StringBuilder(128);
		path.append(m2RepoPath).append(PATH_SEPARATOR);
		path.append(formatAsDirectory(dependency.getGroupId())).append(PATH_SEPARATOR);
		path.append(dependency.getArtifactId()).append(PATH_SEPARATOR);
		path.append(getBaseVersion(dependency)).append(PATH_SEPARATOR);
		path.append(dependency.getArtifactId()).append(ARTIFACT_SEPARATOR).append(dependency.getVersion());
		path.append(".pom");

		return path.toString();

	}

	/**
	 * @param dependency
	 * @return
	 */
	private Object getBaseVersion(Dependency dependency) {
		String SNAPSHOT_VERSION = "SNAPSHOT";
		if (dependency.getVersion() == null) {
			return "";
		}
		Matcher m = VERSION_FILE_PATTERN.matcher(dependency.getVersion());

		if (m.matches()) {
			return m.group(1) + "-" + SNAPSHOT_VERSION;
		} else {
			return dependency.getVersion();
		}
	}

	/**
	 * @param groupId
	 * @return
	 */
	private Object formatAsDirectory(String groupId) {
		return groupId.replaceAll("\\.", PATH_SEPARATOR + "");
	}

	/**
	 * @param workspacePomQueue
	 * @return
	 */
	public Collection<String> subtractUsedPoms(List<String> workspacePomQueue) {
		return CollectionUtils.subtract(poms, workspacePomQueue);
	}
}
