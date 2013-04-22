package be.kuleuven.cs.distrinet.jnome.tool.design;

import be.kuleuven.cs.distrinet.chameleon.core.validation.BasicProblem;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.VariableDeclaration;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;

public class PublicFieldViolation extends Analysis<VariableDeclaration, Verification> {

	public PublicFieldViolation() {
		super(VariableDeclaration.class);
	}

	@Override
	public Verification analyse(VariableDeclaration declaration) {
		Verification result = Valid.create();
			Java language = declaration.language(Java.class);
			Variable variable = declaration.variable();
			if(variable.isTrue(language.PUBLIC) && variable.isTrue(language.INSTANCE) && (variable.isFalse(language.FINAL))) {
				String message = "Error: encapsulation: non-final member variable "+variable.name() +
						" in class "+variable.nearestAncestor(Type.class).getFullyQualifiedName()+" is public.";
				result = new BasicProblem(declaration, message);
			}
		return result;
	}

}