package io.manojvivek.m2shredder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import io.manojvivek.m2shredder.exception.M2PathException;
import io.manojvivek.m2shredder.exception.PomFinderException;
import io.manojvivek.m2shredder.localrepo.impl.PomFinder;
import io.manojvivek.m2shredder.localrepo.impl.SequentialLocalRepoManager;
import io.manojvivek.m2shredder.localrepo.impl.WorkspaceRepoPomXmlFinder;

/**
 * @author manojvivek
 *
 */
public class shredm2 {

	static Logger logger = Logger.getLogger(shredm2.class.getName());

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			SequentialLocalRepoManager repoManager = new SequentialLocalRepoManager();
			repoManager.initialize();
			long sizeBefore = repoManager.calculateStorageSize()/(1024*1024);
			

			PomFinder workspacePomFinder = new WorkspaceRepoPomXmlFinder("/Users/manojvivek/workspace/");
			try {
				List<String> poms = workspacePomFinder.findPoms();
				System.out.println("Worspace poms:" + poms.size() + " files:" + poms);

				List<String> workspacePomQueue = new ArrayList<String>(poms);
				for (int i = 0; i < workspacePomQueue.size(); i++) {
					String pomFile = workspacePomQueue.get(i);
					try {
						Model model = reader.read(new FileReader(pomFile));
						List<Dependency> dependencies = model.getDependencies();
						for (Dependency dependency : dependencies) {
							if (StringUtils.isEmpty(dependency.getArtifactId())
									|| StringUtils.isEmpty(dependency.getGroupId())) {
								// skipping the invalid dependencies
								continue;
							}
							if (repoManager.hasArtifact(dependency)) {
								String dependencyPomPath = repoManager.getLocalRepoPomPath(dependency);
								if (!workspacePomQueue.contains(dependencyPomPath)) {
									workspacePomQueue.add(dependencyPomPath);
								}

							}

						}

					} catch (IOException | XmlPullParserException e) {
						logger.log(Level.WARNING, "Error while parsing the pom:" + pomFile, e);
					}

				}
				Collection<String> unusedPoms = repoManager.subtractUsedPoms(workspacePomQueue);
				long sizeToDelete = calculateDirectoySize(unusedPoms);

				System.out.println("Total poms after iterating workspace poms:" + workspacePomQueue.size());
				System.out.println("To delete size:" + repoManager.subtractUsedPoms(workspacePomQueue).size());
				System.out.println("To delete:" + unusedPoms);
				System.out.println("Size before:"+sizeBefore+"MB");
				System.out.println("Size to delete:"+sizeToDelete+"MB");
				System.out.println("Size after:"+(sizeBefore-sizeToDelete)+"MB");
			} catch (PomFinderException e) {
				logger.log(Level.SEVERE, "Error while finding poms in workspace", e);
			}

		} catch (M2PathException e) {
			logger.log(Level.SEVERE, "Cannot find the m2 repo", e);
		}

	}

	/**
	 * @param unusedPoms
	 * @return
	 */
	private static long calculateDirectoySize(Collection<String> unusedPoms) {
		long size = 0;
		Collections.sort((List<String>)unusedPoms);
		unusedPoms = new HashSet<>(unusedPoms);
		for(String pom : unusedPoms){
			File pomFile = new File(pom);
			File[] files = pomFile.getParentFile().listFiles();
			for(File file : files){
			size+= file.length();	
			}
			
		}
		return size;
	}
}
