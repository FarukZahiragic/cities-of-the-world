package ba.unsa.etf.rpr.lv10;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

public class GradController {

    public Button btnOk;
    public Button btnCancel;
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public ChoiceBox<String> choiceDrzava;

    @FXML
    public void initialize() throws SQLException {
        fieldNaziv.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String o, String n) {
                if (fieldNaziv.getText().trim().isEmpty()) {
                    fieldNaziv.getStyleClass().removeAll("poljeJeIspravno");
                    fieldNaziv.getStyleClass().add("poljeNijeIspravno");
                }
                else {
                    fieldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
                    fieldNaziv.getStyleClass().add("poljeJeIspravno");
                }
            }
        });

         /*HelloController.tableViewGradovi.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            Grad oldGrad = (Grad) oldItem;
            Grad newGrad = (Grad) newItem;
            if (oldItem != null) {
                fieldNaziv.textProperty().unbindBidirectional(oldGrad.nazivProperty());
                fieldBrojStanovnika.textProperty().unbindBidirectional(oldGrad.broj_StanovnikaProperty());
                choiceDrzava.valueProperty().unbindBidirectional(oldGrad.nazivDrzaveProperty());
            }
            if(newItem != null) {
                fieldNaziv.textProperty().bindBidirectional(newGrad.nazivProperty());
                fieldBrojStanovnika.textProperty().bindBidirectional(newGrad.broj_StanovnikaProperty());
                choiceDrzava.valueProperty().bindBidirectional(newGrad.nazivDrzaveProperty());
            } else {
                fieldNaziv.setText("");
                fieldBrojStanovnika.setText("");
                choiceDrzava.setValue("");
            }
        });*/

        choiceDrzava.getItems().addAll(dajNazivDrzave());
    }

    private ArrayList<String> dajNazivDrzave() {
        ArrayList<String> naziviDrzava = new ArrayList<String>();
        ArrayList<Drzava> drzave = HelloController.dao.drzave();
        for (Drzava drzava : drzave) {
            naziviDrzava.add(drzava.getNaziv());
        }
        return naziviDrzava;
    }

    public void prihvatiUnos(ActionEvent actionEvent) {
        System.out.println("Kliknuto na grad");
        String nazivGrada = fieldNaziv.getText();
        int brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
        String nazivDrzave = choiceDrzava.getValue();
        Drzava drzava = HelloController.dao.nadjiDrzavu(nazivDrzave);
        Grad grad = HelloController.dao.nadjiGrad(nazivGrada);
        if(grad == null) {
            HelloController.dao.dodajGrad(new Grad(1, nazivGrada, brojStanovnika, drzava));
        }
        else if(brojStanovnika == grad.getBroj_stanovnika() && grad.getNazivDrzave().equals(nazivDrzave)){
            HelloController.dao.obrisiGrad(nazivGrada);
        }
        else {
            grad.setBroj_stanovnika(brojStanovnika);
            grad.setDrzava(drzava);
            HelloController.dao.izmijeniGrad(grad);
        }
    }

    public void cancelUnos(ActionEvent actionEvent) {
        Stage stage = (Stage)fieldNaziv.getScene().getWindow();
        stage.close();
    }
}
