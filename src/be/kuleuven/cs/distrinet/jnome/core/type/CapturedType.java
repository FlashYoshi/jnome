package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LocalLookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ParameterSubstitution;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;

/**
 * A class that represents a captured type.
 * 
 * 
 * FIXME This should not be a subtype of {@link DerivedType}. Must
 *       introduce an intermediate class for code sharing. The current
 *       implementation of{@link #targetContext()} "violates" behavioral subtyping. 
 *       The contract is not written down in DerivedType, but it should be.
 * 
 * @author Marko van Dooren
 */
public class CapturedType extends JavaDerivedType {

	public CapturedType(ParameterSubstitution substitution, Type baseType) {
		super(substitution, baseType);
	}

//	public CapturedType(Type baseType, List<ActualTypeArgument> typeParameters) throws LookupException {
//		super(baseType, typeParameters);
//	}
	
	public CapturedType(List<ParameterSubstitution> parameters, Type baseType) {
		super(parameters, baseType);
	}

	@Override
	public CapturedType clone() {
		return new CapturedType(clonedParameters(),baseType());
	}
	
	@Override
	public LocalLookupContext<?> targetContext() throws LookupException {
		return localContext();
	}

//	@Override
//	public Type captureConversion() throws LookupException {
//		return this;
//	}
}
