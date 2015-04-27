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

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof State) {
			State s = (State) obj;

			if (s.getLabel().equals(this.label)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return label.hashCode();
	}

}
