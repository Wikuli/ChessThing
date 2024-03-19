package oma.grafiikka.chessthing;

//https://github.com/bhlangonijr/chesslib
import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Pgn tiedoston käsittelyyn tarkoitettuja metodeja ja kenttiä
 */
public class CSVT {
    static Board currentPos = new Board();
    static int prevMove = 0;
    static ArrayBlockingQueue<CSVTStatus> msgQueue = new ArrayBlockingQueue<>(64);
    static List<Game> games;
    public static Trie trieOpening = new Trie();

    /**
     * msgQueueen puskettava luokka, joka sisältää tarvittavat tiedot filttereiden alustamisen etenemisestä ja
     * pgn-tiedoston lukemisesta
     */
    public static class CSVTStatus{
        /**
         * Sisältää elementit statuksen tyypistä statukseen kuuluvasta viestistä ja intin kuinka monennessa askeleessa
         * filttereiden alustamisessa mennään
         */
        public enum Type{
            pgnStart,
            pgnDone,
            pgnFail,
            numFilterUpdate,
            filterDone
        }

        public CSVTStatus(Type type, int numFilters, String message) {
            this.type = type;
            this.numFilters = numFilters;
            this.message = message;
        }

        public Type type;
        public int numFilters;
        public String message;
    }

    /**
     * Lukee tiedostosijainnissa olevan tiedoston muistiin ja kutsuu addToQueue() statuksien lisäämiseen msgQueueen.
     * Tämä metodi käyttää suurimmaksi osaksi metodeja chesslibistä
     * @param path Tiedostosijainti
     */
    //Metodit ja kentät PgnHolder, loadPgn, getGames ovat chesslibistä
    protected static void loadPgn(String path){
        addToQueue(new CSVTStatus(CSVTStatus.Type.pgnStart, 0, null), 1);
        games = null;
        PgnHolder holder = new PgnHolder(path);
        try {
            holder.loadPgn();
            games = holder.getGames();
            addToQueue(new CSVTStatus(CSVTStatus.Type.pgnDone, 0, "Games loaded"), 1);
        }
        catch (Exception e){
            addToQueue(new CSVTStatus(CSVTStatus.Type.pgnFail, 0, e.getMessage()), 10);
        }
    }

    /**
     * Puskee statuksen msgQueueen
     * @param status Puskettava status
     * @param timeout Kuinka kauan statusta yritetään puskea queueen
     */
    private static void addToQueue(CSVTStatus status, long timeout){
        try{
            msgQueue.offer(status, timeout, TimeUnit.SECONDS);
        }
        catch (InterruptedException e){
            System.out.println("Interrupted");
        }
    }

    /**
     * Luo uuden threadin joka kutsuu metodeja loadPgn() ja createGameSelectionList()
     * @param path Tiedostosijainti
     */
    //Thread stays alive after main stops
    protected static void asyncLoad(String path){
       new Thread(() ->{
           loadPgn(path);
           if(games != null) {
               createGameSelectionList();
           }
       }).start();

    }

    /**
     * Luo metodikutsujen avulla gameSelectionListin ja alustaa filtterit.
     * Puskee msgQueueen statusviestejä, jotka kertovat filttereiden alustamisen vaiheen.
     *
     */
    //Metodit getBlackPlayer.* ja getWhitePlayer.get* sekä getOpening ovat chesslibistä
    protected static void createGameSelectionList(){
        addToQueue(new CSVTStatus(CSVTStatus.Type.numFilterUpdate, 0, null), 1);
        trieOpening = new Trie();
        int x = games.size();
        FilterUsage.clearEloIds();
        FilterUsage.clearNameToGameID();

        for(int i = 0; i < x; i++){
            String blackName = games.get(i).getBlackPlayer().getName();
            String whiteName = games.get(i).getWhitePlayer().getName();
            FilterUsage.createNameToGameID(blackName, i);
            FilterUsage.createNameToGameID(whiteName, i);
            String opening = games.get(i).getOpening();
            FilterUsage.cleanAndPassToTrie(opening, trieOpening, i);
            int eloBracket = games.get(i).getWhitePlayer().getElo() / 100;
            FilterUsage.createEloIds(eloBracket, i, games);

            if(msgQueue.isEmpty()){
                msgQueue.offer(new CSVTStatus(CSVTStatus.Type.numFilterUpdate, i, null));
            }
        }
        addToQueue(new CSVTStatus(CSVTStatus.Type.filterDone, x, "UwU"), 100);
    }

    /**
     * Etsii annettujen parametrien avulla oikean kohdan pelistä
     * @param game Peli, josta oikea kohta etsitään
     * @param moveNr Kuinka monennessa siirrossa ollaan meneillä
     * @return (Forsyths-Edwards Notation) FEN-stringin
     */
    //Metodit loadMoveText, getHalfMoves, doMove ja getFen ovat chesslibistä
    protected static String getCurrentBoard(Game game, int moveNr){
        try {
            game.loadMoveText();
        }
        catch (Exception e){
            System.out.println(e);
        }
        MoveList moves = game.getHalfMoves();
        if(moveNr > prevMove){
            while(prevMove < moveNr) {
                currentPos.doMove(moves.get(prevMove));
                prevMove++;
            }
        }
        else{
            while(prevMove > moveNr){
                prevMove--;
                currentPos.undoMove();
            }
        }
        return currentPos.getFen();
    }

    /**
     * Avaa JFileChooserin avulla file explorerin, josta käyttäjä voi valita avattavan tiedoston.
     * Parametrin avulla tiedostojen filtteröintiä muutetaan sen perusteella etsitäänkö pgn- vai txt-tiedostoja
     * @param pgn boolean
     * @return tiedostosijainnin tai null
     */
    protected static String openFileExp(boolean pgn){
        try{
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Txt files", "txt");
            if (pgn) {
                filter = new FileNameExtensionFilter("PGN files", "pgn");
            }
            fileChooser.setFileFilter(filter);
            fileChooser.setCurrentDirectory(new File("."));
            int result = fileChooser.showOpenDialog(null);

            if(result == JFileChooser.APPROVE_OPTION){
                return fileChooser.getSelectedFile().getAbsolutePath();
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    /**
     * Luo tiedoston JFileChooserin avulla
     * @return luodun tiedoston tai null
     */
    protected static File getSaveLoc(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int res = chooser.showSaveDialog(null);

        if (res == JFileChooser.APPROVE_OPTION){
            return chooser.getSelectedFile();
        }

        return null;
    }
}
