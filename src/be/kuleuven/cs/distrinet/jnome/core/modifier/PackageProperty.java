package be.kuleuven.cs.distrinet.jnome.core.modifier;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.NamespaceScope;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.core.scope.Scope;
import org.aikodi.chameleon.core.scope.ScopeProperty;
import org.aikodi.chameleon.oo.type.Type;

import be.kuleuven.cs.distrinet.rejuse.property.PropertyMutex;
import be.kuleuven.cs.distrinet.rejuse.property.PropertyUniverse;

public class PackageProperty extends ScopeProperty {
	
	public final static String ID = "accessibility.package";
	
	public PackageProperty(PropertyMutex<ChameleonProperty> family) {
		super(ID, family);
	}

	public PackageProperty(String name, PropertyMutex<ChameleonProperty> family) {
		super(name, family);
	}

	public Scope scope(Element element) throws LookupException {
		try {
			return new NamespaceScope(((Type)element).namespace());
		} catch (ClassCastException exc) {
			throw new LookupException("The given element is not a type");
		}
	}
}
