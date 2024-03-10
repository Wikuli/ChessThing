package oma.grafiikka.chessthing;

import java.io.*;

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


    public Filter[] getFiltersViaFile(){
        String path = CSVT.openFileExp();
        try{
            File f = new File(path);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            Filter[] rArr = (Filter[]) ois.readObject();
            ois.close();
            return rArr;
        }
        catch (IOException e){
            System.out.println("FNF");
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    public void saveFilters(String path, Filter[] p){
        File f = new File(path);
        ObjectOutputStream oos = null;

        try{
            oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(p);
            oos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
