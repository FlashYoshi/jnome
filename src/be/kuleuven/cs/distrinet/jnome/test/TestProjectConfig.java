package be.kuleuven.cs.distrinet.jnome.test;

import static junit.framework.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import be.kuleuven.cs.distrinet.chameleon.workspace.BaseLibraryConfiguration;
import be.kuleuven.cs.distrinet.chameleon.workspace.BootstrapProjectConfig;
import be.kuleuven.cs.distrinet.chameleon.workspace.DirectoryLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.LanguageRepository;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
import be.kuleuven.cs.distrinet.jnome.input.BaseJavaProjectLoader;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaProjectConfiguration;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;


public class TestProjectConfig {
	
	public final static File PROJECT_CONFIGURATIONS = new File(JavaTest.TEST_DATA,"project_configurations");
	
	@Before
	public void setup() {
		File file = new File("tmp");
		if(! file.exists()) {
			file.mkdir();
		}
		File test = new File(file, "test");
		if(test.exists()) {
			test.delete();
		}
		test.mkdir();
		_testDir = test;
		_workspace = createWorkspace();
	}
	
	private File _testDir;
	
	private Workspace _workspace;

	private Workspace createWorkspace() {
		LanguageRepository repository = new LanguageRepository();
		repository.add(new JavaLanguageFactory().create());
		return new Workspace(repository);
	}
	
	public JavaProjectConfiguration createProjectConfig(String name) {
		Workspace workspace = _workspace;
		ProjectConfigurator configurator = workspace.languageRepository().get(Java.NAME).plugin(ProjectConfigurator.class);
		File root = new File(".");
		BaseLibraryConfiguration baseConfiguration = new BaseLibraryConfiguration(workspace);
		return (JavaProjectConfiguration) configurator.createConfigElement(name, root, workspace, null, baseConfiguration);
	}
	
	@Test
	public void testEmptyProject() {
		// 1. Create a basic project configuration.
		Project project = createProject("testBasicWrite.xml", "test");
		View view = getAndTestView(project);
		assertTrue(view.sourceLoaders().isEmpty());
		assertTrue(view.binaryLoaders().size() == 1);
	}

	private View getAndTestView(Project project) {
		assertTrue(project.views().size() == 1);
		View view = project.views().get(0);
		assertTrue(view.language() instanceof Java);
		assertTrue(view instanceof JavaView);
		boolean found = false;
		for(DocumentLoader loader: view.binaryLoaders()) {
			if(loader instanceof BaseJavaProjectLoader) {
				// There should not be more that one base java project 
				// loader in a java project.
				assertTrue(! found);
				found = true;
			}
		}
		assertTrue(found);
		return view;
	}
	
	@Test
	public void testOneSourceDirectory() {
		Project project = readProject(configFile("one_source_directory.xml"));
		assertTrue(project.getName().equals("one source directory project"));
		View view = getAndTestView(project);
		assertTrue(view.binaryLoaders().size() == 1);
		assertTrue(view.sourceLoaders().size() == 1);
		DocumentLoader loader = view.sourceLoaders().get(0);
		assertTrue(loader instanceof DirectoryLoader);
		assertTrue(loader.project() == project);
		DirectoryLoader directoryLoader = (DirectoryLoader) loader;
		assertTrue(directoryLoader.path().equals("src"));
	}	
	
	private File configFile(String name) {
		return new File(PROJECT_CONFIGURATIONS,name);
	}

	private Project createProject(String fileName, String projectName) {
		JavaProjectConfiguration projectConfig = createProjectConfig(projectName);
		
		// 2. Write the configuration to a file.
		projectConfig.writeToXML(new File(_testDir,fileName));
		
		// 3. Test whether the file exists.
		File configFile = new File(_testDir,fileName);
		assertTrue(configFile.exists());
		
		Project project = readProject(configFile);
		assertTrue(project.getName().equals(projectName));
		return project;
	}

	private Project readProject(File configFile) {
		BootstrapProjectConfig bootstrapper = new BootstrapProjectConfig(_workspace);
		Project project = bootstrapper.project(configFile, null);
		return project;
	}
	

}