package oma.grafiikka.chessthing;
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

public class CSVT {
    static Board currentPos = new Board();
    static int prevMove = 0;
    static ArrayBlockingQueue<CSVTStatus> msgQueue = new ArrayBlockingQueue<>(64);
    static List<Game> games;

    public static class CSVTStatus{
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
            e.printStackTrace();
            addToQueue(new CSVTStatus(CSVTStatus.Type.pgnFail, 0, e.getMessage()), 10);
        }
    }

    private static void addToQueue(CSVTStatus status, long timeout){
        try{
            msgQueue.offer(status, timeout, TimeUnit.SECONDS);
        }
        catch (InterruptedException e){
            System.out.println("Interrupted");
        }
    }
    protected static void asyncLoad(String path){
       new Thread(() ->{
           loadPgn(path);
           if(games != null) {
               createGameSelectionList();
           }
       }).start();

    }

    protected static void createGameSelectionList(){
        addToQueue(new CSVTStatus(CSVTStatus.Type.numFilterUpdate, 0, null), 1);
        int x = games.size();
        FilterUsage.clearEloIds();
        FilterUsage.clearNameToGameID();
        Trie trieOpening = new Trie();
        FilterUsage.setRootOpening(trieOpening.getRootNode());
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

    protected static String openFileExp(){
        String filePath = "";
        try{
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Files", "pgn", "txt");
            fileChooser.setFileFilter(filter);
            fileChooser.setCurrentDirectory(new File("."));
            int result = fileChooser.showOpenDialog(null);
            System.out.println(result);

            if(result == JFileChooser.APPROVE_OPTION){
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
            }
            else{
                filePath = null;
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return filePath;
    }
}
