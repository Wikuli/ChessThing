package oma.grafiikka.chessthing;

import com.github.bhlangonijr.chesslib.game.Game;

import java.lang.reflect.Array;
import java.util.*;

public class FilterUsage {
    static ArrayList<ArrayList<Integer>> eloIds = new ArrayList<>();
    static HashMap<String, ArrayList<Integer>> nameToGameID = new HashMap<>();

    public FilterUsage(ArrayList<ArrayList<Integer>> al, TrieNode trieNode, HashMap<String, ArrayList<Integer>> map){
        eloIds = al;
        nameToGameID = map;
    }

    public static ArrayList<Integer> applyFilter(int eloLow, int eloHigh, String playerName, String opening){
        long start = System.nanoTime();
        HashSet<Integer> tempElo = new HashSet<Integer>();
        HashSet<Integer> tempName = new HashSet<>(playerName != null ? nameToGameID.get(playerName) :
                Main.glont);

        ArrayList<Integer> rArray = new ArrayList<>();
        int low = eloLow / 100;
        int high = Math.min(eloHigh / 100, eloIds.size() - 1);

        int x = 0;
        while(low <= high){
            if(eloIds.get(low).isEmpty()){
                low++;
                x = 0;
                continue;
            }
            if(x == eloIds.get(low).size()){
                low++;
                x = 0;
                continue;
            }
            while(eloIds.get(low).get(x) < eloLow){
                x++;
            }
            tempElo.add(eloIds.get(low).get(x) + 1);
            x++;
        }

        TrieNode cur = CSVT.trieOpening.findNode(opening);
        System.out.println(cur.getContent());
        if(cur == null){
            return rArray;
        }
        HashSet<Integer> openings = new HashSet<>();
        if (cur.isWord()){
            openings.addAll(cur.getGameIDs());
        }
        else {
            for (Iterator<TrieNode> it = cur.getAllWordChildren(); it.hasNext(); ) {
                TrieNode node = it.next();
                openings.addAll(node.getGameIDs());
            }
        }

        openings.retainAll(tempElo);
        openings.retainAll(tempName);
        rArray.addAll(openings);
        Collections.sort(rArray);
        long end = System.nanoTime();
        System.out.println((end - start) / 1e9);
        return rArray;
    }

    public static ArrayList<Integer> getContainedIDs(Filter filter, List<Game> games){
        ArrayList<Integer> elo = new ArrayList<>();
        int eloLow = filter.eloLow;
        int eloHigh = filter.eloHigh;
        for(int i = eloLow / 100; i < eloHigh / 100; i++){
            for(int x = 0; x < eloIds.get(i).size(); x++){
                if(games.get(x).getWhitePlayer().getElo() < eloLow){
                    continue;
                }
                else if(games.get(x).getWhitePlayer().getElo() > eloHigh){
                    break;
                }
                elo.add(eloIds.get(i).get(x));
            }
        }

        String pName = filter.playerName;
        ArrayList<Integer> name = new ArrayList<>();
        if(nameToGameID.containsKey(pName)){
            name.addAll(nameToGameID.get(pName));
        }
        ArrayList<Integer> coffinDance = new ArrayList<>();
        for(int i = 0; i < Math.max(name.size(), elo.size()); i++){
            if(name.contains(elo.get(i))){
                coffinDance.add(elo.get(i));
            }
        }
        return coffinDance;
    }

    public static void createNameToGameID(String name, int i){
        name = name.toLowerCase();
        if(nameToGameID.containsKey(name)){
            nameToGameID.get(name).add(i);
        }
        else{
            ArrayList<Integer> al = new ArrayList<>();
            al.add(i);
            nameToGameID.put(name, al);
        }
    }
    public static void clearNameToGameID(){
        nameToGameID = new HashMap<>();
    }
    public static void clearEloIds(){
        eloIds = new ArrayList<>();
    }

    public static void createEloIds(int eloBracket, int i, List<Game> games){
        while(eloIds.size() < eloBracket + 1){
            eloIds.add(new ArrayList<>());
        }
        if(eloIds.get(eloBracket).isEmpty()){
            eloIds.get(eloBracket).add(i);
        }
        else{
            int y = 0;
            int eloInArray = games.get(eloIds.get(eloBracket).get(y)).getWhitePlayer().getElo();
            int eloToAdd = games.get(i).getWhitePlayer().getElo();
            boolean added = false;
            while(eloInArray < eloToAdd){
                y++;
                if(y == eloIds.get(eloBracket).size()){
                    added = true;
                    eloIds.get(eloBracket).add(i);
                    break;
                }
                eloInArray = games.get(eloIds.get(eloBracket).get(y)).getWhitePlayer().getElo();
            }
            if(!added){
                eloIds.get(eloBracket).add(y, i);
            }
        }
    }

    public static ArrayList<ArrayList<Integer>> getEloIds(){
        return eloIds;
    }

    public static HashMap<String, ArrayList<Integer>> getNameToGameID(){
        return nameToGameID;
    }

    public static void cleanAndPassToTrie(String word, Trie trie, int id){
        String s = "";
        for(int i = 0; i < word.length(); i++){

            s += Character.isLetter(word.charAt(i)) ? Character.toLowerCase(word.charAt(i)) : "";
        }
        trie.addWord(s, id);

    }
}
