package be.kuleuven.cs.distrinet.jnome.analysis.dependency;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.SubtypeRelation;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class NoSubtypeReferences extends UniversalPredicate<Element,Nothing> {

	public NoSubtypeReferences() {
		super(Element.class);
	}

	@Override
	public boolean uncheckedEval(Element t) throws Nothing {
		return t.nearestAncestor(SubtypeRelation.class) == null;
	}

}
