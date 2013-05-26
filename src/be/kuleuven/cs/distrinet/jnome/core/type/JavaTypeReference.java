package be.kuleuven.cs.distrinet.jnome.core.type;

import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;

/**
 * A class for Java type references. They add support for array types and generic parameters.
 * 
 * @author Marko van Dooren
 */
public interface JavaTypeReference extends TypeReference {

//	public void setArrayDimension(int i);
	
//	public JavaTypeReference addArrayDimension(int i);
	
//	public int arrayDimension();

//	public Type erasure() throws LookupException;
	
//	public List<ActualTypeArgument> typeArguments();
	
	public JavaTypeReference toArray(int dimension);
	
	public JavaTypeReference erasedReference();
	
	public JavaTypeReference componentTypeReference();
}
