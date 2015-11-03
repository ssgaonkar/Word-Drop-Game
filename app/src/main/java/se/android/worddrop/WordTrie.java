package se.android.worddrop;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class WordTrie {

	private TriElement root;
	private TriElement t;
	public void buildTrie(Context context,WordTrie myTree){

		try  {
			AssetManager assetmgr = context.getAssets();
			InputStream is = assetmgr.open("words.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
		    String line;
		    while ((line = br.readLine()) != null) {
		       myTree.insertElement(line.toLowerCase());
		    }
		} catch (Exception e) {
			System.out.println("Exception!"+e.getMessage());
		}

	}
	
	public WordTrie() {
		root = new TriElement();
	}
	
	
	public void insertElement(String word) {
		HashMap<Character,TriElement> hashRange = root.myHash;
		
		for(int i=0; i<word.length(); i++){
	            char c = word.charAt(i);
	 
	            TriElement t;
	            if(hashRange.containsKey(c)){
	                    t = hashRange.get(c);
	            }else{
	                t = new TriElement(c);
	                hashRange.put(c, t);
	            }
	 
	            hashRange = t.myHash;
	 
	            //set leaf node
	            if(i==word.length()-1)
	                t.isLeaf = true;    
	        }
	    } 
	
	public boolean searchWord(String word){
		int flag=findPossibleWords(word);
		if(t!=null){
			if (flag ==0 & t.isLeaf==true ) {
				return true;
			}
		}
		return false;
	}
	public int findPossibleWords(String word){
		int flag=0;
		HashMap<Character, TriElement> myRange = root.myHash;
		t = root;
		
		for (int i=0; i<word.length() ; i++){
			if(myRange.containsKey(word.charAt(i))){
				t = myRange.get(word.charAt(i));
				myRange = t.myHash;
			}else {
				flag = 1;
			}
			
		}
		return flag;

		
	}
	

}
