module oma.grafiikka.chessthing {
    requires javafx.controls;
    requires javafx.fxml;
    requires chesslib;
    requires java.desktop;


    opens oma.grafiikka.chessthing to javafx.fxml;
    exports oma.grafiikka.chessthing;
}