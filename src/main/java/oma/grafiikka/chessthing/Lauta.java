package oma.grafiikka.chessthing;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;

public class Lauta{
    public GridPane gp = new GridPane();
    private HashMap<Character, Image> kPieces = new HashMap<Character, Image>(){{
        put('p', new Image("file:images/black_pawn.png"));
        put('P', new Image("file:images/white_pawn.png"));
        put('b', new Image("file:images/black_bishop.png"));
        put('B', new Image("file:images/white_bishop.png"));
        put('k', new Image("file:images/black_king.png"));
        put('K', new Image("file:images/white_king.png"));
        put('n', new Image("file:images/black_knight.png"));
        put('N', new Image("file:images/white_knight.png"));
        put('q', new Image("file:images/black_queen.png"));
        put('Q', new Image("file:images/white_queen.png"));
        put('r', new Image("file:images/black_rook.png"));
        put('R', new Image("file:images/white_rook.png"));
    }};

    public void createBoard(){
        for(int i = 0; i < 8; i++){
            for (int x = 0; x < 8; x++){
                if((x + i) % 2 == 0){
                    gp.add(new Rectangle(75,75, Color.BEIGE), x, i);
                }
                else {
                    gp.add(new Rectangle(75, 75, Color.BURLYWOOD), x, i);
                }
            }
        }
    }

    //This might need rethinking
    public void updateBoard(String fen){
        gp.getChildren().removeAll();
        createBoard();
        int x = 0;
        for(int i = 0; i < fen.length(); i++){
            if(fen.charAt(i) == ' '){
                break;
            }
            else if (fen.charAt(i) == '/') {
                continue;
            }
            else if(Character.isDigit(fen.charAt(i))){
                x += fen.charAt(i) - 48;
            }
            else{
                ImageView imageView = new ImageView(kPieces.get(fen.charAt(i)));
                imageView.setTranslateX(5);
                gp.add(imageView, x % 8, (x / 8));
                x++;
            }

        }
    }
}
