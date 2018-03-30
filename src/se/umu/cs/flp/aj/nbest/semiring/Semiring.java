package se.umu.cs.flp.aj.nbest.semiring;

public abstract class Semiring implements Comparable<Semiring> {

	protected double value;

	public abstract Semiring add(Semiring s);
	public abstract Semiring mult(Semiring s);
	public abstract Semiring zero();
	public abstract Semiring one();
	public abstract boolean isOne();
	public abstract boolean isZero();
	public abstract Semiring div(Semiring s);

}
