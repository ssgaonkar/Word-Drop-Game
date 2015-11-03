package se.android.worddrop;

import android.graphics.Color;

import java.util.Random;

public class Tile {
    private char letter;
    private int tileColor;
    private Random rand=new Random();
    private static final String[] allowedTileColors={"#0002FD","#F28740","#68F441","#F14E41","#9D4F76","#FFCD00"};
    private static int letterFreq[]=new int[26];
    private static int total=0;
    public Tile(){
        initializeLetterFreq();
    }
    public Tile(char letter,int tileColor){
        this.letter=letter;
        this.tileColor=tileColor;
    }
    public void initializeLetterFreq(){
        total =0;
        for(int i=0;i<letterFreq.length;i++){
            if(i==0 || i==4 || i==8 || i==14 ){
                letterFreq[i]=70;
            }else if(i>=21 || i==16 || i==9|| i==20){
                letterFreq[i]=5;
            }else{
                letterFreq[i]=30;
            }
            total+=letterFreq[i];
        }
    }
    public char getRandomLetter(){
        int randNum = rand.nextInt(total+1)+1;
        int it=0,count=0;
        while(count<randNum && it<letterFreq.length){
            count+=letterFreq[it++];
        }
        char letter=(char)((it-1)+65);
        return letter;
    }
    public int getRandomColor(){
        int randNum = rand.nextInt(allowedTileColors.length);
        tileColor=Color.parseColor(allowedTileColors[randNum]);
        return tileColor;
    }
    public void setLetter(char letter){
        this.letter=letter;
    }
    public char getLetter(){
        return letter;
    }
    public void setTileColor(int tileColor){
        this.tileColor=tileColor;
    }
    public int getTileColor(){
        return tileColor;
    }
}
