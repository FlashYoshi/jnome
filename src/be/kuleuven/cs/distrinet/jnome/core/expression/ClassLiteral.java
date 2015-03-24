package be.kuleuven.cs.distrinet.jnome.core.expression;


import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.util.association.Single;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaBasicTypeArgument;

/**
 * @author Marko van Dooren
 */
public class ClassLiteral extends Expression {

  public ClassLiteral(TypeReference tref) {
    setTarget(tref);
  }

  protected Type actualType() throws LookupException {
  	BasicJavaTypeReference tref = (BasicJavaTypeReference) language(ObjectOrientedLanguage.class).createTypeReferenceInNamespace("java.lang.Class", view().namespace());
  	tref.addArgument(language(Java7.class).createBasicTypeArgument(clone(target())));
  	tref.setUniParent(this);
  	return tref.getElement();
  }

  protected ClassLiteral cloneSelf() {
		return new ClassLiteral(null);
  }
  
	/**
	 * TARGET
	 */
	private Single<TypeReference> _typeReference = new Single<TypeReference>(this);

  
  public TypeReference target() {
    return _typeReference.getOtherEnd();
  }
  
  public void setTarget(TypeReference type) {
    set(_typeReference,type);
  }

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

//  public AccessibilityDomain getAccessibilityDomain() throws LookupException {
//    return getTypeReference().getType().getTypeAccessibilityDomain();
//  }
}
