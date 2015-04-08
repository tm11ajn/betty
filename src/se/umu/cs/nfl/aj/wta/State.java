package se.umu.cs.nfl.aj.wta;

public class State {

	private String label;
	private boolean isFinal;

	public State(String label) {
		this.label = label;
		isFinal = false;
	}

	public String getLabel() {
		return label;
	}

	public void setFinal() {
		isFinal = true;
	}

	public boolean isFinal() {
		return isFinal;
	}

}
