/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;


import org.aikodi.chameleon.core.declaration.TargetDeclaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.expression.NamedTarget;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeVariable;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;

public class EqualTypeConstraint extends SecondPhaseConstraint {

	public EqualTypeConstraint(TypeParameter param, JavaTypeReference type) {
		super(param,type);
	}
	
	public void process() throws LookupException {
		Type Utype = U();
		if(Utype instanceof TypeVariable && parent().typeParameters().contains(((TypeVariable)Utype).parameter())) {
			TypeVariable U = (TypeVariable) Utype;
			FormalTypeParameter parameter = U.parameter();
			if(parameter.sameAs(typeParameter())) {
				// Otherwise, if U is Tj, then this constraint carries no information and may be discarded.
			} else {
				JavaTypeReference tref = typeParameter().language(Java7.class).createTypeReference(parameter.signature().name());
				tref.setUniParent(parameter);
				substituteRHS(tref);
				substitute(U.parameter());
				parent().add(new IndirectTypeAssignment(typeParameter(), U.parameter()));
			}
		} else {
			substituteRHS(URef());
			parent().add(new ActualTypeAssignment(typeParameter(), Utype));
		}
		parent().remove(this);
	}
	
	/**
	 * Replace the typeParameter() of this constraint with a clone of the given type reference in the other constraints.
	 * The clone will direct its lookup to the parent of the given type reference to avoid name capture.
	 */
	private void substituteRHS(JavaTypeReference tref) throws LookupException {
		for(SecondPhaseConstraint constraint: parent().constraints()) {
			if(constraint != this) {
				if(constraint.typeParameter().sameAs(typeParameter())) {
					parent().remove(constraint);
				} else {
					final TypeParameter tp = typeParameter();
					JavaTypeReference uRef = constraint.URef();
					NonLocalJavaTypeReference.replace(tref, tp, uRef);
				}
			}
		}
	}

	private void substitute(TypeParameter param) throws LookupException {
		for(SecondPhaseConstraint constraint: parent().constraints()) {
			if(constraint != this) {
				if(constraint.typeParameter().sameAs(typeParameter())) {
					constraint.setTypeParameter(param);
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return this.typeParameter().name() + " = " + this.URef().toString();
	}

}
