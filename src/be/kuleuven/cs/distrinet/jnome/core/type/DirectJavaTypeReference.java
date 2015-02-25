package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.IntersectionTypeReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;

public class DirectJavaTypeReference extends ElementImpl implements JavaTypeReference {

	public DirectJavaTypeReference(Type type) {
		_type = type;
	}
	
	private Type _type;
	
	@Override
	public Type getType() {
		return _type;
	}

	@Override
	public Type getElement() {
		return _type;
	}

	@Override
	public TypeReference intersection(TypeReference other) {
		return other.intersectionDoubleDispatch(this);
	}

	@Override
	public TypeReference intersectionDoubleDispatch(TypeReference other) {
		return language(ObjectOrientedLanguage.class).createIntersectionReference(clone(this), clone(other));
	}

	@Override
	public TypeReference intersectionDoubleDispatch(IntersectionTypeReference other) {
		IntersectionTypeReference result = clone(other);
		result.add(clone(this));
		return result;
	}

	@Override
	public Declaration getDeclarator() throws LookupException {
		return _type;
	}

	@Override
	public LookupContext targetContext() throws LookupException {
		return getType().targetContext();
	}

	@Override
	public JavaTypeReference toArray(int dimension) {
		return new ArrayTypeReference(this, dimension);
	}

	@Override
	public JavaTypeReference erasedReference() {
		Java java = language(Java.class);
		return java.reference(java.erasure(_type));
	}

	@Override
	public JavaTypeReference componentTypeReference() {
		if(_type instanceof ArrayType) {
			return language(Java.class).reference(_type).componentTypeReference();
		} else {
			return this;
		}
	}

	@Override
	protected Element cloneSelf() {
		return new DirectJavaTypeReference(_type);
	}

}