package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LocalLookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.tag.TagImpl;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Parameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ParameterSubstitution;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.CapturedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.InheritanceRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.SubtypeRelation;
import be.kuleuven.cs.distrinet.chameleon.util.Lists;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.NonLocalJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;

public class JavaDerivedType extends DerivedType implements JavaType {

	public <P extends Parameter> JavaDerivedType(Class<P> kind, List<P> parameters, Type baseType) {
		super(kind,parameters,baseType);
	}

	public JavaDerivedType(List<ParameterSubstitution> parameters, Type baseType) {
		super(parameters, baseType);
	}

	public JavaDerivedType(ParameterSubstitution substitution, Type baseType) {
		super(substitution, baseType);
	}

	public JavaDerivedType(Type baseType, List<ActualTypeArgument> typeArguments) throws LookupException {
		super(baseType, typeArguments);
	}

	@Override
	public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
		//  	//FIXME cache and put listener on inheritance relations to remove the default one if an explicit one is added.
		//    if(explicitNonMemberInheritanceRelations().isEmpty() && (! "Object".equals(name())) && (! getFullyQualifiedName().equals("java.lang.Object"))) {
		//    	InheritanceRelation relation = new SubtypeRelation(language(ObjectOrientedLanguage.class).createTypeReference(new NamedTarget("java.lang"),"Object"));
		//    	relation.setUniParent(this);
		//    	relation.setMetadata(new TagImpl(), IMPLICIT_CHILD);
		//    	List<InheritanceRelation> result = new ArrayList<InheritanceRelation>();
		//    	result.add(relation);
		//    	return result;
		//    } else {
		//    	return Collections.EMPTY_LIST;
		//    }

		//FIXME speed avoid creating collection
		String defaultSuperClassFQN = language(ObjectOrientedLanguage.class).getDefaultSuperClassFQN();
		if(explicitNonMemberInheritanceRelations().isEmpty() && 
				(! getFullyQualifiedName().equals(defaultSuperClassFQN)) //"java.lang.Object"
				) {
			InheritanceRelation relation = new SubtypeRelation(language(ObjectOrientedLanguage.class).createTypeReference(defaultSuperClassFQN));
			relation.setUniParent(this);
			relation.setMetadata(new TagImpl(), IMPLICIT_CHILD);
			List<InheritanceRelation> result = new ArrayList<InheritanceRelation>();
			result.add(relation);
			return result;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	public void newAccumulateAllSuperTypes(Set<Type> acc) throws LookupException {
		Type captureConversion = captureConversion();
		if(captureConversion != this) {
			captureConversion.newAccumulateAllSuperTypes(acc);
		} else {
			super.newAccumulateAllSuperTypes(acc);
		}
	}

	public void newAccumulateSelfAndAllSuperTypes(Set<Type> acc) throws LookupException {
		Type captureConversion = captureConversion();
		if(captureConversion != this) {
			captureConversion.newAccumulateSelfAndAllSuperTypes(acc);
		} else {
			super.newAccumulateSelfAndAllSuperTypes(acc);
		}
	}

	@Override
	public Type erasure() {
		return ((JavaType)origin()).erasure();
	}

	public Type captureConversion() throws LookupException {
		if(_captureConversion == null) {
			Type result = this;

			List<TypeParameter> typeParameters = Lists.create();
			if(! (parameter(TypeParameter.class,1) instanceof CapturedTypeParameter)) {
				Type base = baseType();
				List<TypeParameter> baseParameters = base.parameters(TypeParameter.class);
				Iterator<TypeParameter> formals = baseParameters.iterator();
				List<TypeParameter> actualParameters = parameters(TypeParameter.class);
				Iterator<TypeParameter> actuals = actualParameters.iterator();
				// substitute parameters by their capture bounds.
				// ITERATOR because we iterate over 'formals' and 'actuals' simultaneously.
				List<TypeConstraint> toBeSubstituted = Lists.create();
				while(actuals.hasNext()) {
					TypeParameter formalParam = formals.next();
					if(!(formalParam instanceof FormalTypeParameter)) {
						throw new LookupException("Type parameter of base type is not a formal parameter.");
					}
					TypeParameter actualParam = actuals.next();
					if(!(actualParam instanceof InstantiatedTypeParameter)) {
						throw new LookupException("Type parameter of type instantiation is not an instantiated parameter: "+actualParam.getClass().getName());
					}
					typeParameters.add(((InstantiatedTypeParameter) actualParam).capture((FormalTypeParameter) formalParam,toBeSubstituted));
				}
				// Everything works as well when we pass 'this' instead of 'base'.
				result = language(Java.class).createdCapturedType(new ParameterSubstitution(TypeParameter.class,typeParameters), base);
				result.setUniParent(parent());
				for(TypeParameter newParameter: typeParameters) {
					for(TypeParameter oldParameter: baseParameters) {
						//If we replace references to the old parameters with references to the captured type parameters, then
						// why is the capturing done with non-locals pointing to the formal?
						JavaTypeReference tref = new BasicJavaTypeReference(oldParameter.name());
						tref.setUniParent(newParameter);
						if(newParameter instanceof CapturedTypeParameter) {
							List<TypeConstraint> constraints = ((CapturedTypeParameter)newParameter).constraints();
							for(TypeConstraint constraint : constraints) {
								if(toBeSubstituted.contains(constraint)) {
									NonLocalJavaTypeReference.replace(tref, oldParameter, (JavaTypeReference) constraint.typeReference());
								}
							}
						} else {
							throw new ChameleonProgrammerException();
						}
					}
				}
			}
			_captureConversion = result;
		}
		return _captureConversion;
	}

	public JavaDerivedType clone() {
		List<ParameterSubstitution> args = clonedParameters();
		return new JavaDerivedType(args,baseType());
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(name());
		List<TypeParameter> parameters = parameters(TypeParameter.class);
		if(parameters.size() > 0) {
			result.append('<');
			Iterator<TypeParameter> iter = parameters.iterator();
			while(iter.hasNext()) {
				TypeParameter parameter = iter.next();
				//				TypeParameter clone = clone(parameter);
				//				for(BasicJavaTypeReference tref: clone.descendants(BasicJavaTypeReference.class)) {
				//					Type element = tref.getElement();
				//					if(parameters.contains(element)) {
				//						
				//					}
				//				}

				result.append(parameter.toString());
				if(iter.hasNext()) {
					result.append(",");
				}
			}
			result.append('>');
		}
		return result.toString();
	}

	@Override
	public LocalLookupContext<?> targetContext() throws LookupException {
		return captureConversion().targetContext();
	}

	private Type _captureConversion;

}
