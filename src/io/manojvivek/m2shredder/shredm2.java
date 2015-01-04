package io.manojvivek.m2shredder;

import io.manojvivek.m2shredder.exception.M2PathException;
import io.manojvivek.m2shredder.exception.PomFinderException;
import io.manojvivek.m2shredder.localrepo.impl.PomFinder;
import io.manojvivek.m2shredder.localrepo.impl.SequentialLocalRepoManager;
import io.manojvivek.m2shredder.localrepo.impl.WorkspaceRepoPomXmlFinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author manojvivek
 *
 */
public class shredm2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File irisPom = new File("/Users/manojvivek/workspace/ia-core/ia-core/ia-core-web/pom.xml");
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			SequentialLocalRepoManager repoManager = new SequentialLocalRepoManager();
			repoManager.initialize();

			PomFinder workspacePomFinder = new WorkspaceRepoPomXmlFinder("/Users/manojvivek/workspace/ia-core");
			try {
				List<String> poms = workspacePomFinder.findPoms();
				System.out.println("Worspace poms:" + poms.size() + " files:" + poms);

				LinkedHashSet<String> workspacePomQueue = new LinkedHashSet<String>(poms);
				Iterator<String> iterator = workspacePomQueue.iterator();
				while (iterator.hasNext()) {
					String pomFile = iterator.next();
					try {
						Model model = reader.read(new FileReader(pomFile));
						List<Dependency> dependencies = model.getDependencies();
						for (Dependency dependency : dependencies) {
							System.out.println("Index:" + dependencies.indexOf(dependency));
							if (repoManager.hasArtifact(dependency)) {
								String dependencyPomPath = repoManager.getLocalRepoPomPath(dependency);
								System.out.println("Contains it:" + dependencyPomPath);
								workspacePomQueue.add(dependencyPomPath);
								System.out.println("Size after adding:" + workspacePomQueue.size());
							}

						}

					} catch (IOException | XmlPullParserException e) {
						// TODO Auto-generated catch blocks
						e.printStackTrace();
					}

				}
			} catch (PomFinderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				Model irisModel = reader.read(new FileReader(irisPom));
				irisModel.getDependencies();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// System.out.println(repoManager.m2DirPath);
			// SequentialLocalRepoManager repoManager1 = new
			// SequentialLocalRepoManager("/Users/manojvivek/");
			// System.out.println(repoManager1.m2DirPath);
			// Model irisModel = reader.read(new FileReader(irisPom));
			// System.out.println(irisModel.getParent().getRelativePath());
			// for (Dependency dependency : irisModel.getDependencies()) {
			// System.out.println(dependency.getGroupId() + " " +
			// dependency.getArtifactId());
			// }
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (XmlPullParserException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (M2PathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
