package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;

public class IndirectTypeAssignment extends TypeAssignment {

	private TypeParameter _source;

	public IndirectTypeAssignment(TypeParameter parameter, TypeParameter source) {
		super(parameter);
		_source = source;
	}

	@Override
	public Type type() {
		for(TypeAssignment assignment:parent().assignments()) {
			if(assignment.parameter().equals(source())) {
				return assignment.type();
			}
		}
		return null;
	}
	
	public TypeParameter source() {
		return _source;
	}

	@Override
	public void substitute(TypeParameter oldParameter, TypeParameter newParameter) {
		super.substitute(oldParameter, newParameter);
		if(source().equals(oldParameter)) {
			_source = newParameter;
		}
	}
}