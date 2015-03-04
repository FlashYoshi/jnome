package be.kuleuven.cs.distrinet.jnome.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.input.parser.JavaLexer;
import be.kuleuven.cs.distrinet.jnome.input.parser.JavaParser;

import org.aikodi.chameleon.core.document.Document;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.input.ParseException;
import org.aikodi.chameleon.oo.member.Member;
import org.aikodi.chameleon.support.input.ChameleonANTLR3Parser;
import org.aikodi.chameleon.support.input.ModelFactoryUsingANTLR;
import org.aikodi.chameleon.workspace.View;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 * @author Marko van Dooren
 */

public class JavaModelFactory extends ModelFactoryUsingANTLR {

//	/**
//	 * BE SURE TO CALL INIT() IF YOU USE THIS CONSTRUCTOR.
//	 * 
//	 * @throws IOException
//	 * @throws ParseException
//	 */
//	public JavaModelFactory() {
//		Java lang = new Java();
//		setLanguage(lang, ModelFactory.class);
//	}
	
	protected JavaModelFactory(boolean bogus) {
		super();
	}
	
	/**
	 */
	public JavaModelFactory() {
		super();
	}
	
  @Override
  protected ChameleonANTLR3Parser<? extends Java> getParser(InputStream inputStream,View view) throws IOException {
      ANTLRInputStream input = new ANTLRInputStream(inputStream);
      JavaLexer lexer = new JavaLexer(input);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      JavaParser parser = new JavaParser(tokens);
      parser.setView(view);
      return parser;
  }

  @Override
	protected <P extends Element> Element parse(Element element, String text) throws ParseException {
		try {
		  InputStream inputStream = new StringBufferInputStream(text);
		  Element result = null;
		  if(element instanceof Member) {
	  		result = ((JavaParser)getParser(inputStream, element.view())).memberDecl().element;
			}
			return result;
		} catch(RecognitionException exc) {
			throw new ParseException(element.nearestAncestor(Document.class));
		} catch(IOException exc) {
			throw new ChameleonProgrammerException("Parsing of a string caused an IOException",exc);
		}
	}


  @Override
	public ModelFactoryUsingANTLR clone() {
		try {
			JavaModelFactory javaModelFactory = new JavaModelFactory();
			javaModelFactory.setDebug(debug());
			return javaModelFactory;
		} catch (Exception e) {
			throw new RuntimeException("Exception while cloning a JavaModelFactory", e);
		}
	}
}
