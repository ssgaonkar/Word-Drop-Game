package se.android.worddrop;
import java.util.HashMap;

public class TriElement {

	char letter;
	HashMap<Character, TriElement> myHash = new HashMap<Character, TriElement>();
	boolean isLeaf;
	
	public TriElement() { }
	
	public TriElement(char c) {
		this.letter = c;
		}
		
}
