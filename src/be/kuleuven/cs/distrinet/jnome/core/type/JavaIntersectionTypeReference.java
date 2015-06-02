package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.Collections;
import java.util.List;

import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.type.IntersectionTypeReference;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;

public class JavaIntersectionTypeReference extends IntersectionTypeReference implements JavaTypeReference {
	
	public JavaIntersectionTypeReference() {
		
	}

	public JavaIntersectionTypeReference(List<? extends TypeReference> refs) {
		addAll(refs);
	}
	
	@Override
	protected JavaIntersectionTypeReference cloneSelf() {
		return new JavaIntersectionTypeReference(Collections.EMPTY_LIST);
	}

	@Override
	public Verification verifySelf() {
    return Valid.create();
	}

	public void addAllArguments(List<TypeArgument> arguments) {
		throw new ChameleonProgrammerException("Cannot add arguments to an intersection type reference");
	}

	public void addArgument(TypeArgument argument) {
		throw new ChameleonProgrammerException("Cannot add an argument to an intersection type reference");
	}

	public void addArrayDimension(int i) {
		throw new ChameleonProgrammerException("Cannot change the dimension of an intersection type reference");
	}

	public int arrayDimension() {
		return 0;
	}

	public List<TypeArgument> typeArguments() {
		return Collections.EMPTY_LIST;
	}

	public JavaTypeReference erasedReference() {
		return ((JavaTypeReference)elementAt(0)).erasedReference();
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}
	

}
