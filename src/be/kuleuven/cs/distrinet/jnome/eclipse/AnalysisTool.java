package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.tool.Tool;
import be.kuleuven.cs.distrinet.jnome.tool.design.DesignAnalyzer;
import be.kuleuven.cs.distrinet.rejuse.io.FileUtils;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

public abstract class AnalysisTool extends Tool {

	protected AnalysisTool(String name) {
		super(name);
	}
	
	public void execute(String[] args) {
		try
	  {
			DesignCheckerOptions result = CliFactory.parseArguments(DesignCheckerOptions.class, args);
			if(result.isHelp()) {
				printHelp();
				System.exit(0);
			}
	    File root = new File(result.getRoot());
	    Map containerConfiguration = getContainerConfiguration(result);
			Project project = new JavaEclipseProjectConfig(root, containerConfiguration).project();
			OutputStream stream;
			if(result.isOut()) {
				File output = new File(result.getOut());
				stream = new FileOutputStream(output);
			} else {
				stream = System.out;
			}
	    OutputStreamWriter writer = new OutputStreamWriter(stream);
			writer.write("Analyzing project in "+root.getAbsolutePath()+"\n");
			writer.flush();
			check(project, writer);
			writer.close();
			stream.close();
	  }
	  catch(ArgumentValidationException e) {
			printHelp();
			System.exit(0);
	  }
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void check(Project project, OutputStreamWriter writer) throws LookupException, InputException, IOException;

	private void printHelp() {
		Cli<DesignCheckerOptions> cli = CliFactory.createCli(DesignCheckerOptions.class);
		System.out.println(cli.getHelpMessage());
	}

	private Map getContainerConfiguration(DesignCheckerOptions result) {
		Map containerConfiguration = new HashMap<String,String>();
		if(result.getContainers() != null) {
		  File containerConfigFile = new File(result.getContainers());
		  Properties properties = new Properties();
		  try {
				properties.load(new FileInputStream(containerConfigFile));
				containerConfiguration = properties;
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("The given container configuration file is not found.");
			} catch (IOException e) {
				throw new IllegalArgumentException("The given container configuration file was found, but could not be read.");
			}
		  File parentFile = containerConfigFile.getParentFile();
		  if(parentFile == null) {
		  	parentFile = new File(".");
		  }
			makeRelativePathsAbsoluteRelativeToConfigFile(containerConfiguration, parentFile);
		}
		return containerConfiguration;
	}
	
	private void makeRelativePathsAbsoluteRelativeToConfigFile(Map<Object,Object> map, File rootForRelativePaths) {
		for(Map.Entry entry: map.entrySet()) {
			String path = (String) entry.getValue();
			String key = (String) entry.getKey();
			String newPath = FileUtils.absolutePath(path, rootForRelativePaths);
			map.put(key, newPath);
		}
	}
	
	@CommandLineInterface(application="DesignChecker")
	public static interface DesignCheckerOptions {
		
		@Option(defaultValue="./", description="The directory that contains the Eclipse .project and .classpath files. Note that the Eclipse project should have no compile errors.") 
		String getRoot();
		
		@Option(description="Libraries that are built into Eclipse, such as Junit, are marked in the classpath as containers. To function propertly, the tool must be able to find the jar files that correspond to these containers. The given file should be a property file that contains the mapping of Eclipse container names to jar files. Relative paths are resolved relative to the given file.") 
		String getContainers();
		boolean isContainers();
		
		@Option(description="The name of the output file. If no file is given, the output is written to the standard output stream.") 
		String getOut();
		boolean isOut();
		
		@Option(description="Display this help and exit.")
		boolean isHelp();
	}
}
