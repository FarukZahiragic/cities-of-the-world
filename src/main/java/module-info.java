module ba.unsa.etf.rpr.lv10 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ba.unsa.etf.rpr.lv10 to javafx.fxml;
    exports ba.unsa.etf.rpr.lv10;
}