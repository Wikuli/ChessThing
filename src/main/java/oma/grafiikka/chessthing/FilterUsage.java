package oma.grafiikka.chessthing;

import com.github.bhlangonijr.chesslib.game.Game;
import java.util.*;

/**
 * Filttereiden luomiseen ja käyttämiseen tarkoitettuja metodeja
 */
public class FilterUsage {
    static ArrayList<ArrayList<Integer>> eloIds = new ArrayList<>();
    static HashMap<String, ArrayList<Integer>> nameToGameID = new HashMap<>();

    /**
     * Käy läpi pelilistan id:t ja filtteröi niistä pois ne, jotka eivät täytä ehtoja
     * @param eloLow filtteriin määritetty elo rangen alaraja
     * @param eloHigh filltteriin määritetty elo rangen yläraja
     * @param playerName filtteriin määritetty pelaajan nimi
     * @param opening filtteriin märritetty avaus
     * @return Arraylist id:istä, jotka täyttävät filterin ehdot
     */
    public static ArrayList<Integer> applyFilter(int eloLow, int eloHigh, String playerName, String opening){
        HashSet<Integer> tempElo = new HashSet<>();
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
            if(x >= eloIds.get(low).size()){
                low++;
                x = 0;
                continue;
            }
            while(x < eloIds.get(low).size() && eloIds.get(low).get(x) < eloLow){
                x++;
            }
            if(x < eloIds.get(low).size()) {
                tempElo.add(eloIds.get(low).get(x) + 1);
            }
            x++;
        }
        HashSet<Integer> openings = new HashSet<>();
        if (Objects.equals(opening, "")){
            openings = tempElo;
        }
        else {
            TrieNode cur = CSVT.trieOpening.findNode(opening);
            if (cur == null) {
                return rArray;
            }
            if (cur.isWord()) {
                openings.addAll(cur.getGameIDs());
            } else {
                for (Iterator<TrieNode> it = cur.getAllWordChildren(); it.hasNext(); ) {
                    TrieNode node = it.next();
                    openings.addAll(node.getGameIDs());
                }
            }
        }
        openings.retainAll(tempElo);
        openings.retainAll(tempName);
        rArray.addAll(openings);
        Collections.sort(rArray);
        return rArray;
    }

    /**
     * Lisää hashmappiin pelin id:n, jos nimi löytyy. Jos nimeä ei löydy luo uuden avaimen ja lisää id:n siihen
     * @param name pelaajan nimi
     * @param i pelin id
     */
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

    /**
     * Tyhjentää nameToGameID hashmapin
     */
    public static void clearNameToGameID(){
        nameToGameID.clear();
    }

    /**
     * Tyhjentää eloIds arraylistin
     */
    public static void clearEloIds(){
        eloIds.removeAll(eloIds);
    }

    /**
     * Lisää pelin id:n eloIds arraylistiin. Pelit lisätään elon mukaan nousevassa järjestyksessä
     * @param eloBracket arraylistin indeksi, johon pelin id lisätään
     * @param i pelin id
     * @param games lista peleistä
     */
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

    /**
     *
     * @param filters lista filttereistä
     * @return arraylistin filttereiden nimistä
     */
    public static ArrayList<String> getFilterNamesFromList(ArrayList<Filter> filters){
        ArrayList<String> rArray = new ArrayList<>();
        for(Filter f : filters){
            rArray.add(f.getFilterName());
        }
        return rArray;
    }

    /**
     * Puhdistaa stringin kaikesta muusta paitsi kirjaimista ja lisää sanan Trieen
     * @param word lisättävä sana
     * @param trie trie johon sana lisätään
     * @param id pelin id
     */
    public static void cleanAndPassToTrie(String word, Trie trie, int id){
        String s = "";
        for(int i = 0; i < word.length(); i++){
            s += Character.isLetter(word.charAt(i)) ? Character.toLowerCase(word.charAt(i)) : "";
        }
        trie.addWord(s, id);
    }
}
