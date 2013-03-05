package jnome.core.namespacedeclaration;

import java.util.Collections;
import java.util.List;

import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.NamespaceReference;
import chameleon.core.namespace.RootNamespaceReference;
import chameleon.core.namespacedeclaration.DemandImport;
import chameleon.core.namespacedeclaration.Import;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.SimpleReference;
import chameleon.util.association.Single;

public class JavaNamespaceDeclaration extends NamespaceDeclaration {

  static {
    excludeFieldName(JavaNamespaceDeclaration.class,"_defaultImport");
  }
  
  public JavaNamespaceDeclaration() {
  	this(new RootNamespaceReference());
  }

  public JavaNamespaceDeclaration(String fqn) {
  	this(check(fqn));
  }
  
  private static CrossReference<Namespace> check(String fqn) {
  	if("".equals(fqn)) {
  		throw new IllegalArgumentException("If you want a namespace declaration for the root namespace, use a RootNamespaceReference, or use the default constructor.");
  	}
  	return new SimpleReference<Namespace>(fqn, Namespace.class);
  }
  
	public JavaNamespaceDeclaration(CrossReference<Namespace> ref) {
		super(ref);
		if(ref instanceof SimpleReference && ((SimpleReference)ref).name().equals("")) {
			throw new IllegalArgumentException();
		}
		set(_defaultImport,new DemandImport(new NamespaceReference("java.lang")));
	}
	
	@Override
	public NamespaceDeclaration cloneThis() {
		return new JavaNamespaceDeclaration(namespaceReference().clone());
	}
	
	@Override
	public List<? extends Import> implicitImports() {
		return Collections.singletonList(_defaultImport.getOtherEnd());
	}

	private Single<Import> _defaultImport = new Single<Import>(this);

}
