package oma.grafiikka.chessthing;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.MoveList;
import java.util.ArrayList;

public class GameMoves {

    /**
     * Etsii pelistä valkoisen siirrot
     * @param game
     * @return arraylist valkoisen siirroista
     */
    public ArrayList<String> getWhiteMoves(Game game){
        //Tämä on chesslibistä
        MoveList moves = game.getHalfMoves();
        ArrayList<String> whiteAL = new ArrayList<>();

        for(int i = 0; i < moves.size(); i++){
            if(i % 2 == 0){
                whiteAL.add(moves.get(i).getSan());
            }
        }
        return whiteAL;
    }

    /**
     * Etsii pelistä mustan siirrot
     * @param game
     * @return arraylist mustan siirroista
     */
    public ArrayList<String> getBlackMoves(Game game){
        MoveList moves = game.getHalfMoves();
        ArrayList<String> blackAL = new ArrayList<>();

        for(int i = 0; i < moves.size(); i++){
            if(i % 2 != 0){
                blackAL.add(moves.get(i).getSan());
            }
        }
        return  blackAL;
    }
}
