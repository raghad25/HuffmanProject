package application;

public class Tree implements Comparable<Tree> {

	char c;//character
	int freq;//Frequency
	Tree left;//left
	Tree right;//right

	public Tree(char c, int freq, Tree left, Tree right) {

		this.c = c;
		this.freq = freq;
		this.left = left;
		this.right = right;

	}

	public Tree(char c, int freq) {

		this.c = c;
		this.freq = freq;

	}

	public Tree(char c) {

		this.c = c;

	}

	public boolean isLeaf() {
		return (left == null && right == null);
	}

	// compare the freqs of diff chars ;
	@Override
	public int compareTo(Tree x) {
		return (this.freq - x.freq);
	}

	public char getC() {
		return c;
	}

	public void setC(char c) {
		this.c = c;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public Tree getLeft() {
		return left;
	}

	public void setLeft(Tree left) {
		this.left = left;
	}

	public Tree getRight() {
		return right;
	}

	public void setRight(Tree right) {
		this.right = right;
	}

}