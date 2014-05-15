package io.manojvivek.m2shredder.localrepo.impl;

import io.manojvivek.m2shredder.exception.M2PathException;
import io.manojvivek.m2shredder.localrepo.LocalRepoManager;

import org.codehaus.plexus.util.StringUtils;

/**
 * @author manojvivek
 *
 */
public class SequentialLocalRepoManager implements LocalRepoManager {

    String m2DirPath;

    public SequentialLocalRepoManager(String m2DirPath) throws M2PathException {
	super();
	validateM2Path(m2DirPath);
	this.m2DirPath = m2DirPath;
    }

    public SequentialLocalRepoManager() throws M2PathException {
	super();
	String userHome = System.getProperty("user.home");
	if(StringUtils.isEmpty(userHome)){
	    throw new M2PathException();
	}
	if(!userHome.endsWith("/")){
	    userHome = userHome.concat("/");
	}
	String m2Path = userHome.concat(".m2");
	validateM2Path(m2Path);
	this.m2DirPath = m2Path;
    }

    /**
     * @param m2Path
     */
    private void validateM2Path(String m2Path) throws M2PathException {
	// TODO validate the m2DirPath(by checking repositories subdir atleast)
	// and throw ex if not.

    }

    /* (non-Javadoc)
     * @see io.manojvivek.m2shredder.localrepo.LocalRepoManager#initialize()
     */
    public void initialize() {
	// TODO Scan the user home /.m2/repository and build the details

    }

}
