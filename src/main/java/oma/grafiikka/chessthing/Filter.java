package oma.grafiikka.chessthing;

import java.io.*;
import java.util.ArrayList;

/**
 * Filtteri, jonka avulla tiedostosta avattuja pelejä voi rajata tietyin ehdoin
 */
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

    /**
     *
     * @return filterin nimen
     */
    public String getFilterName() {
        return filterName;
    }

    /**
     *
     * @return filtteriin määritetys elo rangen alarajan
     */
    public int getEloLow() {
        return eloLow;
    }

    /**
     *
     * @return filtteriin määritetyn elo rangen ylärajan
     */
    public int getEloHigh() {
        return eloHigh;
    }

    /**
     *
     * @return filtteriin määritetys pelaajan nimen
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     *
     * @return filtteriin määritetyn avauksen
     */
    public String getOpening() {
        return opening;
    }

    /**
     * Avaa tiedostosijainnissa olevan tiedoston ja lukee sen
     * @param path
     * @return arralistin tiedostosta löytyneistä filttereistä
     */
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

    /**
     * Tallentaa filtterilistan annettuun tiedostoon
     * @param f tiedosto
     * @param p arraylist filttereistä
     */
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
