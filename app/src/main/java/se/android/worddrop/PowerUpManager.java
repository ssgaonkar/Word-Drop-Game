package se.android.worddrop;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class PowerUpManager {

    private int userId;
    private int shrinkLetterCnt;
    private int deleteColorCnt;
    private int scrambleCnt;
    private int totalScore;
    private Random random=new Random();
    private List<Tile> tilesArr;
    private int validWordsCnt = 0;
    private DBHelper dbHelper;

    public PowerUpManager(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    public static enum PowerupType {

        SCRAMBLE("SCRAMBLES_NUM",0),
        DELETE_COLOR("DELETECOLORS_NUM",1),
        SHRINK_LETTER("SHRINKS_NUM",2);

        private String stringValue;
        private int intValue;
        private PowerupType(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    protected void finalize() throws Throwable{
        try{
            dbHelper.closeConnections();}
        catch (Throwable t)
        {
            Log.d("PowerUpManager", t.getMessage());
        }
        finally {
            super.finalize();
        }

    }

    public int getUserId(){
        return userId;
    }

    public void setUserId(int userId){
        this.userId=userId;
    }

    public int getShrinkLetterCnt(){
        shrinkLetterCnt=getPowerupCount(PowerupType.SHRINK_LETTER.toString());
        return shrinkLetterCnt;
    }

    public void setShrinkLetterCnt(int shrinkLetterCnt){
        updatePowerupCount(PowerupType.SHRINK_LETTER.toString(),shrinkLetterCnt);
        this.shrinkLetterCnt=shrinkLetterCnt;
    }

    public int getDeleteColorCnt(){
        deleteColorCnt=getPowerupCount(PowerupType.DELETE_COLOR.toString());
        return deleteColorCnt;
    }

    public void setDeleteColorCnt(int deleteColorCnt){
        updatePowerupCount(PowerupType.DELETE_COLOR.toString(),deleteColorCnt);
        this.deleteColorCnt=deleteColorCnt;
    }

    public int getScrambleCnt(){
        scrambleCnt=getPowerupCount(PowerupType.SCRAMBLE.toString());
        return scrambleCnt;
    }

    public void setScrambleCnt(int scrambleCnt){
        updatePowerupCount(PowerupType.SCRAMBLE.toString(),scrambleCnt);
        this.scrambleCnt=scrambleCnt;
    }

    public int getTotalScore(){
        totalScore = dbHelper.getTotalScore(userId);
        return totalScore;
    }

    public void setTotalScore(int totalScore){
        dbHelper.updateTotalScore(totalScore,userId);
        this.totalScore =totalScore;
    }

    private int getPowerupCount(String powerupType){
       return dbHelper.getPowerupCount(powerupType,userId);
    }

    private  void updatePowerupCount(String powerupType,int count) {
        dbHelper.updatePowerUpCount(count,powerupType,userId);
    }


    //Shuffles the grid such that some valid words are formed.
    public Tile[][] scramble(Grid grid, WordTrie trie) {
        Tile[][] tile = grid.getTile();
        tilesArr = convertGridToArray(tile);
        List<Integer> randInd = generateRandomIndArr(tile);
        validWordsCnt=0;
        List<Tile> wordTiles = findWords(trie);
        if(wordTiles!=null && wordTiles.isEmpty()){
            int size = wordTiles.size();
            int cnt = 0;
            int x=0,y=0;
            List<Integer> adjCells = new ArrayList<Integer>();

            //Places the tiles that form words in adjacent positions on the grid
            for (int i = 0; i < validWordsCnt; i++) {
                int randomNum = random.nextInt(randInd.size());
                for (int j = 0; j < size / validWordsCnt; j++) {
                    if (j == 0) {
                        int ranPos = randInd.get(randomNum);
                        x = ranPos / tile.length;
                        y = ranPos % tile.length;
                        randInd.remove(randomNum);
                    } else {
                        randomNum = random.nextInt(adjCells.size());
                        int ranPos = adjCells.get(randomNum);
                        x = ranPos / tile.length;
                        y = ranPos % tile.length;
                        adjCells.remove(randomNum);
                        int index = randInd.indexOf(ranPos);
                        randInd.remove(index);
                    }
                    tile[x][y] = wordTiles.get(cnt++);
                    adjCells = getAdjacentCells(x, y, tile.length,randInd);
                }
            }
        }
        //Places the remaining tiles in random positions
        for (int i = 0; i < tilesArr.size(); i++) {
            int randomNum = random.nextInt(randInd.size());
            int ranPos = randInd.get(randomNum);
            tile[ranPos / tile.length][ranPos % tile.length] = tilesArr.get(i);
            randInd.remove(randomNum);
        }
        grid.setTile(tile);
        return tile;
    }

    private List<Integer> getAdjacentCells(int gridx, int gridy, int gridSize,List<Integer> randInd) {
        Point neighbor[] = new Point[4];
        neighbor[0]=new Point((gridx-1),gridy);
        neighbor[1]=new Point((gridx+1),gridy);
        neighbor[2]=new Point(gridx,(gridy-1));
        neighbor[3]=new Point(gridx,(gridy+1));
        List<Integer> adjCell = new ArrayList<Integer>();
        for (int i = 0; i < neighbor.length; i++){
            int val=(neighbor[i].x*gridSize)+neighbor[i].y;
            if(neighbor[i].x>=0 && neighbor[i].y>=0 && neighbor[i].x<gridSize && neighbor[i].y<gridSize && randInd.contains(val)){
                adjCell.add(val);
            }
        }
        return adjCell;
    }

    private List<Tile> findWords(WordTrie trie){

        if(tilesArr!=null && trie!=null) {
            List<Tile> words = new ArrayList<Tile>();
            outer:
            for (int i = 0; i < tilesArr.size(); i++) {
                String tmp = "";
                for (int j = 0; j < tilesArr.size(); j++) {
                    if (j != i) {
                        tmp = "" + tilesArr.get(i).getLetter() + tilesArr.get(j).getLetter();
                        if (trie.findPossibleWords(tmp.toLowerCase()) == 1) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                    for (int k = 0; k < tilesArr.size(); k++) {
                        if (k != i && k != j) {
                            tmp = tmp + tilesArr.get(k).getLetter();
                            if (trie.searchWord(tmp.toLowerCase())) {
                                words.add(tilesArr.get(i));
                                words.add(tilesArr.get(j));
                                words.add(tilesArr.get(k));
                                removeUsedTiles(tilesArr, i, j, k);
                                validWordsCnt++;
                            }
                        }
                        if (validWordsCnt == 2) {
                            break outer;
                        }
                    }
                }
            }
            return words;
        }
        return null;
    }

    private List<Integer> generateRandomIndArr(Tile[][] tile){
        List<Integer> randInd=new ArrayList<Integer>();
        for(int i=0;i<(tile.length*tile[0].length);i++){
            randInd.add(i);
        }
        return randInd;
    }

    private List<Tile> removeUsedTiles(List<Tile> tilesArr,int i, int j, int k){
        tilesArr.remove(i);
        tilesArr.remove(j);
        tilesArr.remove(k);
        return tilesArr;
    }

    private List<Tile> convertGridToArray(Tile[][] tile){
        List<Tile> tilesArr=new ArrayList<Tile>();
        for(int i=0;i<tile.length;i++){
            for(int j=0;j<tile[i].length;j++){
                tilesArr.add(tile[i][j]);
            }
        }
        return tilesArr;
    }
	
	public Set deleteColor (Tile changeTile [][], String button) {
       try {
           Thread.sleep(400);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       Set<String> deleteColorButtonSet =new HashSet<String>();

       // get all the buttons with Tile colours of button
       int row = button.charAt(button.length()-2)-'0';
       int column = button.charAt(button.length()-1)-'0';
       int tileColorSelected = changeTile[row][column].getTileColor();
       for ( int i = 0 ; i<5 ; i++){
           for ( int j =0; j<5 ; j++)
           {
               if (changeTile[i][j].getTileColor() ==tileColorSelected) {
                   String addButton = "Button" + i + j;
                   deleteColorButtonSet.add(addButton);
               }
           }

       }

       return deleteColorButtonSet;
   }
}
