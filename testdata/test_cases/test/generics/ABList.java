package test.generics;

public class ABList<X extends ABList<X,Y,B>, Y extends ABList<Y,X,B>, F> extends List<Y> {
	
	public F f() {
		return null;
	}
	
	public void m() {
		get(0).f().b().c();
	}
}