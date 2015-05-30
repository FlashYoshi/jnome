package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.generics.FormalParameterType;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.LazyFormalAlias;

public class JavaLazyFormalAlias extends LazyFormalAlias implements JavaType {

  public JavaLazyFormalAlias(String name, FormalTypeParameter param) {
    super(name, param);
  }

  @Override
  public Type erasure() {
    return this;
  }
  
  @Override
  public FormalParameterType cloneSelf() {
    return new JavaLazyFormalAlias(name(), parameter());
  }
  
  @Override
  public boolean upperBoundNotHigherThan(Type other, TypeFixer trace) throws LookupException {
    if(trace.contains(other, parameter())) {
      return true;
    } 
    if(sameAs(other)) {
      return true;
    }
    trace.add(other, parameter());
    boolean result = aliasedType().upperBoundNotHigherThan(other, trace);
    return result;
//    return JavaType.super.upperBoundNotHigherThan(other, trace);
  }
  
  @Override
  public boolean lowerBoundAtLeastAsHighAs(Type other, TypeFixer trace) throws LookupException {
    return other.upperBoundNotHigherThan(aliasedType(), trace);
  }

}
