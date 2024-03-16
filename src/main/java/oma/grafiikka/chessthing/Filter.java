package oma.grafiikka.chessthing;

import java.io.*;
import java.util.ArrayList;

public class Filter implements Serializable {
    String filterName;
    int eloLow;
    int eloHigh;
    String playerName;
    String opening;

    public Filter(String filterName, int eloHigh, int eloLow, String playerName, String opening){
        this.filterName = filterName;
        this.eloHigh = eloHigh;
        this.eloLow = eloLow;
        this.playerName = playerName;
        this.opening = opening;
    }
    public String getFilterName() {
        return filterName;
    }

    public int getEloLow() {
        return eloLow;
    }

    public int getEloHigh() {
        return eloHigh;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpening() {
        return opening;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public void setEloLow(int eloLow) {
        this.eloLow = eloLow;
    }

    public void setEloHigh(int eloHigh) {
        this.eloHigh = eloHigh;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public static ArrayList<Filter> getFiltersViaFile(String path){
        try{
            File f = new File(path);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            ArrayList<Filter> rArr = (ArrayList<Filter>) ois.readObject();
            ois.close();
            return rArr;
        }
        catch (IOException e){
            System.out.println("FNF");
        }
        catch (ClassNotFoundException e){
        }
        return null;
    }

    public static void saveFiltersToFile(File f, ArrayList<Filter> p){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(p);
            oos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
