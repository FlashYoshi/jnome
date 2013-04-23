package be.kuleuven.cs.distrinet.jnome.workspace;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import be.kuleuven.cs.distrinet.chameleon.workspace.BootstrapProjectConfig.BaseLibraryConfiguration;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.FileInputSourceFactory;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfiguration;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.input.BaseJavaProjectLoader;

public class JavaProjectConfig extends ProjectConfiguration {

	public JavaProjectConfig(String projectName, File root, View view, Workspace workspace, FileInputSourceFactory inputSourceFactory, JarFile baseJarPath, BaseLibraryConfiguration baseLibraryConfiguration) throws ConfigException {
		super(projectName,root,view, workspace, inputSourceFactory);
		if(baseLibraryConfiguration.mustLoad("Java")) {
			try {
				//Add the base loader.
				view.addBinary(new BaseJavaProjectLoader(baseJarPath,(Java)language("java")));
			} catch (ProjectException e) {
				throw new ConfigException(e);
			}
		}
	}
	
	@Override
	protected void binaryNonBaseLoaderAdded(DocumentLoader loader) throws ConfigException {
		if(loader instanceof BaseJavaProjectLoader) {
			return;
		}
		if(loader instanceof JarLoader) {
			BinaryPath p = createOrGetChild(BinaryPath.class);
			p.createOrUpdateChild(BinaryPath.Jar.class,loader);
		} else {
			super.binaryLoaderAdded(loader);
		}
	}
	
	public class BinaryPath extends ProjectConfiguration.BinaryPath {
		
		public class Jar extends Archive {
	  	
	  	protected void pathChanged() throws ConfigException {
	  		try {
	  			JarFile path = new JarFile(project().absoluteFile(_path));
					view().addBinary(new JarLoader(path, language("java").plugin(ProjectConfigurator.class).binaryFileFilter()));
	  		} catch (ProjectException | IOException e) {
	  			throw new ConfigException(e);
	  		}
	  	}
	  	
	  }
	}
	
}
