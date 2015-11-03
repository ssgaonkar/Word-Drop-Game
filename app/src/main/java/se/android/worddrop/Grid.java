package se.android.worddrop;

import java.util.Iterator;
import java.util.Set;

public class Grid {
    public Tile[][] tile;
    private Tile t=new Tile();

    public Grid(int rows,int columns){
        tile=new Tile[rows][columns];
    }

    public void setTile(Tile[][] tile)
    {
        this.tile = tile;
    }
    public Tile[][] getTile(){
        return tile;
    }
    public Tile[][] populateGrid(){
        for(int i=0;i<tile.length;i++){
            for(int j=0;j<tile[i].length;j++){
                tile[i][j]=new Tile(t.getRandomLetter(),t.getRandomColor());
            }
        }
        return tile;
    }

    public Tile[][] refillGrid(Set<String> btnIdsSwipped){
        if(tile!=null){
            Iterator<String> it=btnIdsSwipped.iterator();

            while(it. hasNext()){
                String btnId=it.next();
                int len=btnId.length();
                int x=Character.getNumericValue(btnId.charAt(len-2));
                int y=Character.getNumericValue(btnId.charAt(len-1));
                int i;
                for(i=x;i>0;i--){
                    tile[i][y]=tile[i-1][y];
                    String id="button"+i+y;
                    System.out.println("Button id in grid: "+id);
                }
                tile[i][y]=new Tile(t.getRandomLetter(),t.getRandomColor());
            }
        }
        return tile;
    }
}
