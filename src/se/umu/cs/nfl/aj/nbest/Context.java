package se.umu.cs.nfl.aj.nbest;

public class Context {

	private Node rootNode;
	private Node specialLeafNode;

	public Context() {
		rootNode = null;
		specialLeafNode = null;
	}

	public void setSpecialLeafNode() {
		if (specialLeafNode != null) {
			throw new IllegalArgumentException("The context already has " +
					"a special leaf node.");
		}
	}

	public void removeSpecialLeafNode() {
		specialLeafNode = null;
	}

}
