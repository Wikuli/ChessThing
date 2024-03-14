package oma.grafiikka.chessthing;

import com.github.bhlangonijr.chesslib.game.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

import javafx.scene.shape.Rectangle;

import static oma.grafiikka.chessthing.CSVT.*;

public class Main extends Application {
    List<Game> gameList;
    int index = 0;
    ListView<Integer> gameSelectionList = new ListView<>(FXCollections.observableArrayList());

    public void start(Stage stage){
        Lauta lauta = new Lauta();
        lauta.createBoard();

        VBox vBox = new VBox();
        vBox.setMaxWidth(70);
        gameSelectionList.setMaxWidth(50);
        gameSelectionList.setMaxHeight(600);
        gameSelectionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        vBox.getChildren().add(gameSelectionList);
        vBox.setMaxWidth(70);
        vBox.setMaxHeight(600);

        GameMoves gameMoves = new GameMoves();
        HBox moveLVs = new HBox();
        ListView<String> whiteMoves = new ListView<>();
        ListView<String> blackMoves = new ListView<>();
        whiteMoves.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        blackMoves.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        moveLVs.getChildren().addAll(whiteMoves, blackMoves);
        moveLVs.setMaxWidth(130);
        moveLVs.setMaxHeight(600);

        HBox hBox = new HBox();
        hBox.setMaxSize(1200, 600);
        hBox.getChildren().addAll(gameSelectionList, lauta.gp, moveLVs);
        Rectangle rectangle = new Rectangle(10000, 10000, Color.DARKGRAY);
        Button createFilter = new Button("Filter");
        //createFilter.setVisible(false);
        VBox vert = new VBox(hBox, createFilter);
        vert.setSpacing(20);
        StackPane sp = new StackPane(rectangle, vert);
        Button openFile = new Button("Open");
        Text text = new Text("Loading games...");
        text.setFont(new Font(42));
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMinWidth(300);
        VBox loadingVbox = new VBox(40);
        loadingVbox.getChildren().addAll(text, progressBar);
        Rectangle rectangle1 = new Rectangle(10000, 10000);
        rectangle1.setFill(Color.BLACK);
        rectangle1.setOpacity(0.5);
        Pane pane = new Pane(rectangle1, loadingVbox);
        loadingVbox.setTranslateX(200);
        loadingVbox.setTranslateY(200);
        pane.setVisible(false);
        sp.getChildren().addAll(pane, openFile, createFilter);
        StackPane.setAlignment(openFile, Pos.BOTTOM_LEFT);

        Scene scene = new Scene(sp, 900,750);
        String css = this.getClass().getResource("app.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle("title");
        stage.show();

        gameSelectionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {

                String fenPos = "";
                index = gameSelectionList.getSelectionModel().getSelectedIndex();
                try {
                    fenPos = getCurrentBoard(gameList.get(index), 0);
                    whiteMoves.setItems(FXCollections.observableArrayList(gameMoves.getWhiteMoves(gameList.get(index))));
                    blackMoves.setItems(FXCollections.observableArrayList(gameMoves.getBlackMoves(gameList.get(index))));
                    Node sbWhite = whiteMoves.lookup(".scroll-bar");
                    if(sbWhite instanceof ScrollBar){
                        final ScrollBar barWhite = (ScrollBar) sbWhite;
                        Node sbBlack = blackMoves.lookup(".scroll-bar");
                        if(sbBlack instanceof ScrollBar){
                            final ScrollBar barBlack = (ScrollBar) sbBlack;
                            barBlack.valueProperty().bindBidirectional(barWhite.valueProperty());
                        }
                    }
                    lauta.updateBoard(fenPos);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        whiteMoves.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
                if(whiteMoves.getSelectionModel().isEmpty()){
                    return;
                }
                try{
                    blackMoves.getSelectionModel().clearSelection();
                    String fenPos = getCurrentBoard(gameList.get(index),
                            2 * whiteMoves.getSelectionModel().getSelectedIndex() + 1);
                    lauta.updateBoard(fenPos);
                }
                catch (Exception e){
                    System.out.println("n");
                }
            }
        });

        blackMoves.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(blackMoves.getSelectionModel().isEmpty()){
                    return;
                }
                try{
                    whiteMoves.getSelectionModel().clearSelection();
                    String fenPos = getCurrentBoard(gameList.get(index),
                            2 * blackMoves.getSelectionModel().getSelectedIndex() + 2);
                    lauta.updateBoard(fenPos);
                }
                catch(Exception e){
                    System.out.println("n");
                }
            }
        });

        hBox.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                    event.consume();
                    if(event.getCode().equals(KeyCode.RIGHT)){
                        if(whiteMoves.getSelectionModel().getSelectedIndex() + 1 > blackMoves.getItems().size()
                        || blackMoves.getSelectionModel().getSelectedIndex() + 1 == whiteMoves.getItems().size()){
                            event.consume();
                        }
                        else if(blackMoves.getSelectionModel().isEmpty() && whiteMoves.getSelectionModel().isEmpty()){
                            whiteMoves.requestFocus();
                            whiteMoves.getSelectionModel().select(0);
                            whiteMoves.scrollTo(0);
                        }
                        else if(blackMoves.getSelectionModel().isEmpty()){
                            blackMoves.requestFocus();
                            blackMoves.getSelectionModel().select(whiteMoves.getSelectionModel().getSelectedIndex());
                            blackMoves.scrollTo(whiteMoves.getSelectionModel().getSelectedIndex());
                            whiteMoves.getSelectionModel().clearSelection();
                        }
                        else{
                            if(blackMoves.getSelectionModel().getSelectedIndex() < whiteMoves.getItems().size()) {
                                whiteMoves.requestFocus();
                                whiteMoves.getSelectionModel().select(blackMoves.getSelectionModel()
                                        .getSelectedIndex() + 1);
                                whiteMoves.scrollTo(blackMoves.getSelectionModel().getSelectedIndex() + 1);
                                blackMoves.getSelectionModel().clearSelection();
                            }
                        }
                    }
                    if(event.getCode().equals(KeyCode.LEFT)){
                        if(blackMoves.getSelectionModel().isEmpty()) {
                            if(whiteMoves.getSelectionModel().getSelectedIndex() == 0){
                                whiteMoves.getSelectionModel().clearSelection();
                                lauta.updateBoard(CSVT.getCurrentBoard(gameList.get(0),0));
                            }
                            blackMoves.requestFocus();
                            blackMoves.getSelectionModel().select(whiteMoves.getSelectionModel()
                                    .getSelectedIndex() - 1);
                            blackMoves.scrollTo(whiteMoves.getSelectionModel().getSelectedIndex() - 1);
                            whiteMoves.getSelectionModel().clearSelection();
                        }
                        else{
                            whiteMoves.requestFocus();
                            whiteMoves.getSelectionModel().select(blackMoves.getSelectionModel().getSelectedIndex());
                            whiteMoves.scrollTo(blackMoves.getSelectionModel().getSelectedIndex());
                            blackMoves.getSelectionModel().clearSelection();
                        }
                    }
                }
            });


        openFile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String path = openFileExp();
                pane.setVisible(true);

                CSVT.asyncLoad(path);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ArrayList<CSVTStatus> statuses = new ArrayList<>();
                        int numStuff = msgQueue.drainTo(statuses);
                        for(int i = 0; i < numStuff; i++){
                            CSVTStatus status = statuses.get(i);
                            switch (status.type){
                                case pgnDone -> {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            text.setText("Calculating filters...");
                                            progressBar.setProgress(0);
                                        }
                                    });
                                }
                                case pgnFail -> {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            text.setText(status.message);
                                        }
                                    });
                                }
                                case pgnStart -> {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                                            text.setText("Loading games...");
                                        }
                                    });
                                }
                                case filterDone -> {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            pane.setVisible(false);
                                            ArrayList<Integer> glont = new ArrayList<>(games.size());
                                            for(int i = 1; i < games.size() + 1; i++){
                                                glont.add(i);
                                            }
                                            gameList = games;
                                            gameSelectionList.setItems(FXCollections.observableArrayList(glont));
                                            timer.cancel();
                                        }
                                    });
                                }
                                case numFilterUpdate -> {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress((double) status.numFilters / (double) games.size());
                                        }
                                    });
                                }
                            }
                        }

                    }
                }, 0, 100);
            }
        });

        createFilter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                HBox hBox1 = new HBox();
                Scene filterStage = new Scene(hBox1, 600,600);


            }
        });
    }

    public static void main(String[] args) {
        Application.launch();
    }
}
