package oma.grafiikka.chessthing;

import com.github.bhlangonijr.chesslib.game.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
import java.io.File;
import java.util.*;
import javafx.scene.shape.Rectangle;
import static oma.grafiikka.chessthing.CSVT.*;

public class Main extends Application {
    List<Game> gameList;
    int index = 0;
    ListView<Integer> gameSelectionList = new ListView<>(FXCollections.observableArrayList());
    ArrayList<Filter> fArray = new ArrayList<>();
    ListView<String> filterList = new ListView<>();
    public static ArrayList<Integer> glont;

    /**
     * Pääikkuna
     * @param stage ikkuna
     */
    public void start(Stage stage){
        Lauta lauta = new Lauta();
        lauta.createBoard();

        VBox vBox = new VBox();
        vBox.setMaxWidth(70);
        gameSelectionList.setMaxWidth(50);
        gameSelectionList.setMaxHeight(600);
        gameSelectionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        vBox.getChildren().add(gameSelectionList);
        vBox.setMaxWidth(80);
        vBox.setMaxHeight(600);

        GameMoves gameMoves = new GameMoves();
        HBox moveLVs = new HBox();
        ListView<String> whiteMoves = new ListView<>();
        ListView<String> blackMoves = new ListView<>();
        whiteMoves.setMaxWidth(80);
        blackMoves.setMaxWidth(80);
        whiteMoves.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        blackMoves.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        moveLVs.getChildren().addAll(whiteMoves, blackMoves);
        moveLVs.setMaxWidth(130);
        moveLVs.setMaxHeight(600);

        HBox hBox = new HBox();
        hBox.setMaxSize(800, 600);
        hBox.setPadding(new Insets(10));
        hBox.getChildren().addAll(gameSelectionList, lauta.gp, moveLVs);
        Rectangle rectangle = new Rectangle(10000, 10000, Color.MOCCASIN);

        Text filterText = new Text("Filter options");
        Button createFilter = new Button("Create");
        createFilter.setMinWidth(60);
        Button openFilterFromFile = new Button("Open");
        openFilterFromFile.setMinWidth(60);
        Button saveFilters = new Button("Save");
        saveFilters.setMinWidth(60);
        Button deleteFilter = new Button("Delete");
        deleteFilter.setMinWidth(60);
        Button unfilter = new Button("Unfilter");
        unfilter.setMinWidth(60);
        filterText.setFont(new Font(18));
        GridPane filterGp = new GridPane();
        filterGp.setVgap(2);
        filterGp.setHgap(2);
        filterGp.add(createFilter, 0 , 0);
        filterGp.add(openFilterFromFile, 1, 0);
        filterGp.add(saveFilters, 0, 1);
        filterGp.add(deleteFilter, 1, 1);
        filterGp.add(unfilter, 2, 0);

        VBox vBoxFilter = new VBox(filterText, filterGp);
        vBoxFilter.setMaxWidth(110);
        vBoxFilter.setSpacing(5);
        Text openFileTxt = new Text("File");
        Button openFile = new Button("Open");
        openFileTxt.setFont(new Font(18));
        VBox openFileVbox = new VBox(openFileTxt, openFile);
        openFileVbox.setSpacing(5);
        filterList.setOrientation(Orientation.HORIZONTAL);
        filterList.setMaxHeight(50);
        filterList.setMinWidth(500);
        filterList.setTranslateY(20);
        filterList.setStyle("-fx-background-color: moccasin;");
        HBox controlPanel = new HBox(openFileVbox, vBoxFilter, filterList);
        controlPanel.setMaxHeight(50);
        controlPanel.setSpacing(10);
        controlPanel.setPadding(new Insets(5));

        Text deleteFilterHelpTxt = new Text("Delete the selected filter");
        Rectangle deleteFilterHelpBg = new Rectangle( 155, 20);
        deleteFilterHelpBg.setFill(Color.WHITE);
        StackPane deleteFilterHelp = new StackPane(deleteFilterHelpBg, deleteFilterHelpTxt);
        deleteFilterHelp.setMaxSize(155, 20);
        deleteFilterHelp.setVisible(false);
        vBoxFilter.getChildren().add(deleteFilterHelp);

        StackPane sp = new StackPane(rectangle);
        sp.getChildren().add(hBox);
        Text text = new Text("Loading games...");
        text.setFont(new Font(42));
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMinWidth(300);
        VBox loadingVbox = new VBox(40);
        loadingVbox.setMaxSize(300, 200);
        loadingVbox.getChildren().addAll(text, progressBar);
        Rectangle rectangle1 = new Rectangle(10000, 10000);
        rectangle1.setFill(Color.BLACK);
        rectangle1.setOpacity(0.5);
        Pane pane = new Pane(rectangle1, loadingVbox);
        pane.setVisible(false);
        sp.getChildren().addAll(controlPanel, deleteFilterHelp, pane);
        StackPane.setAlignment(hBox, Pos.CENTER);
        StackPane.setAlignment(controlPanel, Pos.TOP_LEFT);

        Scene scene = new Scene(sp, 1500,1000);
        String css = this.getClass().getResource("app.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle("title");
        stage.show();

        gameSelectionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (gameSelectionList.getSelectionModel().getSelectedIndex() < 0){
                    return;
                }
                String fenPos = "";
                index = gameSelectionList.getSelectionModel().getSelectedItem() - 1;
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
                            blackMoves.scrollTo(blackMoves.getSelectionModel().getSelectedIndex());
                            whiteMoves.getSelectionModel().clearSelection();
                        }
                        else{
                            if(blackMoves.getSelectionModel().getSelectedIndex() < whiteMoves.getItems().size()) {
                                whiteMoves.requestFocus();
                                whiteMoves.getSelectionModel().select(blackMoves.getSelectionModel()
                                        .getSelectedIndex() + 1);
                                whiteMoves.scrollTo(whiteMoves.getSelectionModel().getSelectedIndex());
                                blackMoves.getSelectionModel().clearSelection();
                            }
                        }
                    }
                    if(event.getCode().equals(KeyCode.LEFT)){
                        if(blackMoves.getSelectionModel().isEmpty()) {
                            if(whiteMoves.getSelectionModel().getSelectedIndex() == 0){
                                whiteMoves.getSelectionModel().clearSelection();
                                lauta.updateBoard(CSVT.getCurrentBoard(gameList.get(index),0));
                            }
                            blackMoves.requestFocus();
                            blackMoves.getSelectionModel().select(whiteMoves.getSelectionModel()
                                    .getSelectedIndex() - 1);
                            blackMoves.scrollTo(blackMoves.getSelectionModel().getSelectedIndex());
                            whiteMoves.getSelectionModel().clearSelection();
                        }
                        else{
                            whiteMoves.requestFocus();
                            whiteMoves.getSelectionModel().select(blackMoves.getSelectionModel().getSelectedIndex());
                            whiteMoves.scrollTo(whiteMoves.getSelectionModel().getSelectedIndex());
                            blackMoves.getSelectionModel().clearSelection();
                        }
                    }
                }
            });


        openFile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String path = openFileExp(true);
                if(path == null){
                    return;
                }
                pane.setVisible(true);
                loadingVbox.setTranslateX(stage.getWidth() / 2 - 150);
                loadingVbox.setTranslateY(stage.getHeight() / 2 - 100);

                CSVT.asyncLoad(path);
                Timer timer = new Timer(true);
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
                                            glont = new ArrayList<>(games.size());
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
                if (gameList == null){
                    event.consume();
                    return;
                }
                popupFilterCreation().show();
            }
        });

        openFilterFromFile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (gameList == null){
                    return;
                }
                String path = openFileExp(false);
                if (path == null){
                    return;
                }
                fArray = Filter.getFiltersViaFile(path);
                if (fArray == null){
                    return;
                }
                ArrayList<String> temp = new ArrayList<>(fArray.size());
                for (Filter filter : fArray) {
                    temp.add(filter.getFilterName());
                }
                filterList.setItems(FXCollections.observableArrayList(temp));

            }
        });

        filterList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(filterList.getSelectionModel().getSelectedIndex() < 0){
                    return;
                }
                int eloLow = fArray.get(filterList.getSelectionModel().getSelectedIndex()).getEloLow();
                int eloHigh = fArray.get(filterList.getSelectionModel().getSelectedIndex()).getEloHigh();
                String playerName = fArray.get(filterList.getSelectionModel().getSelectedIndex()).getPlayerName();
                String opening = fArray.get(filterList.getSelectionModel().getSelectedIndex()).getOpening();
                gameSelectionList.setItems(FXCollections.observableArrayList(
                        FilterUsage.applyFilter(eloLow, eloHigh, playerName, opening)));
            }
        });

        deleteFilter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (fArray.isEmpty()){
                    return;
                }
                fArray.remove(filterList.getSelectionModel().getSelectedIndex());
                filterList.setItems(FXCollections.observableArrayList(FilterUsage.getFilterNamesFromList(fArray)));
                if (fArray.isEmpty()){
                    gameSelectionList.setItems(FXCollections.observableArrayList(glont));
                }
                filterList.getSelectionModel().clearSelection();
            }
        });

        deleteFilter.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                deleteFilterHelp.setTranslateX(-stage.getWidth() / 2 + 120);
                deleteFilterHelp.setTranslateY(-stage.getHeight() / 4 - 120);
                deleteFilterHelp.setVisible(true);
            }
        });

        deleteFilter.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                deleteFilterHelp.setVisible(false);
            }
        });

        saveFilters.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (fArray.isEmpty()){
                    return;
                }
                File file = getSaveLoc();
                if (file == null){
                    return;
                }
                Filter.saveFiltersToFile(file, fArray);
            }
        });

        unfilter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gameSelectionList.getSelectionModel().clearSelection();
                filterList.getSelectionModel().clearSelection();
                gameSelectionList.setItems(FXCollections.observableArrayList(glont));
            }
        });
    }

    public Stage popupFilterCreation(){
        Stage stage = new Stage();
        GridPane textPane = new GridPane();
        Text openinTxt = new Text("Opening");
        TextField openingTxtF = new TextField();
        textPane.add(openinTxt, 0, 0);
        textPane.add(openingTxtF, 0, 1);
        Text playerTxt = new Text("Player");
        TextField playerTxtF = new TextField();
        textPane.add(playerTxt, 1, 0);
        textPane.add(playerTxtF, 1, 1);
        Text eloLowerBoundTxt = new Text("Elo lower bound");
        TextField eloLowerBoundTxtF = new TextField();
        textPane.add(eloLowerBoundTxt, 2, 0);
        textPane.add(eloLowerBoundTxtF, 2, 1);
        Text eloHigherBoundTxt = new Text("Elo higher bound");
        TextField eloHigherBoundTxtF = new TextField();
        textPane.add(eloHigherBoundTxt, 3, 0);
        textPane.add(eloHigherBoundTxtF, 3, 1);
        Text fNameTxt = new Text("Filter name");
        TextField fNameTxtF = new TextField();
        textPane.add(fNameTxt, 4, 0);
        textPane.add(fNameTxtF, 4, 1);

        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        HBox hBox = new HBox(okButton, cancelButton);
        VBox vBox = new VBox(textPane, hBox);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);


        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.hide();
            }
        });

        okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int eloLow = 0;
                int eloHigh = 9999;
                filterList.getSelectionModel().clearSelection();
                try {
                    String pName = playerTxtF.getText().isBlank() ? null : playerTxtF.getText();
                    String opening = openingTxtF.getText();

                    String s = "";
                    for(int i = 0; i < opening.length(); i++){
                        s += Character.isLetter(opening.charAt(i)) ? Character.toLowerCase(opening.charAt(i)) : "";
                    }

                    String filterName = fNameTxtF.getText();
                    if (!eloLowerBoundTxtF.getText().isBlank()) {
                        eloLow = Integer.parseInt(eloLowerBoundTxtF.getText());
                    }
                    if (!eloHigherBoundTxtF.getText().isBlank()){
                        eloHigh = Integer.parseInt(eloHigherBoundTxtF.getText());
                    }
                    Filter f = new Filter(filterName, eloHigh, eloLow, pName, s);
                    fArray.add(f);
                    ArrayList<String> names = new ArrayList<>(fArray.size());
                    for (int i = 0; i < fArray.size(); i++){
                        names.add(fArray.get(i).getFilterName());
                    }
                    filterList.setItems(FXCollections.observableArrayList(names));
                }
                catch (Exception e){
                    System.out.println(e);
                }
                stage.hide();
            }
        });

        return stage;
    }


    public static void main(String[] args) {
        Application.launch();
    }

}
