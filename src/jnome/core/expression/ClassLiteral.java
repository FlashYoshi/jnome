package jnome.core.expression;


import java.util.List;

import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.type.TypeReference;
import chameleon.support.expression.LiteralWithTypeReference;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class ClassLiteral extends LiteralWithTypeReference {

  public ClassLiteral(TypeReference tref) {
    super("class");
    setTarget(tref);
    //FIXME a class literal should not have a type reference to store its type, it is not present in the source code.
    setTypeReference(new BasicJavaTypeReference("java.lang.Class"));
  }

  public ClassLiteral clone() {
    TypeReference target = target();
		TypeReference clone = (target == null ? null : target.clone());
		ClassLiteral result = new ClassLiteral(clone);
    result.setTypeReference((JavaTypeReference)getTypeReference().clone());
    return result;
  }
  
  public List<Element> children() {
  	List<Element> result = super.children();
  	Util.addNonNull(target(), result);
  	return result;
  }
 
	/**
	 * TARGET
	 */
	private SingleAssociation<LiteralWithTypeReference,TypeReference> _typeReference = new SingleAssociation<LiteralWithTypeReference,TypeReference>(this);

  
  public TypeReference target() {
    return _typeReference.getOtherEnd();
  }
  
  public void setTarget(TypeReference type) {
    _typeReference.connectTo(type.parentLink());
  }

//  public AccessibilityDomain getAccessibilityDomain() throws LookupException {
//    return getTypeReference().getType().getTypeAccessibilityDomain();
//  }
}
