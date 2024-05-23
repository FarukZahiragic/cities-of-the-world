package ba.unsa.etf.rpr.lv10;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

public class DrzavaController {
    public TextField fieldNaziv;
    public Button btnOk;
    public Button btnCancel;
    public ChoiceBox<String> choiceGrad;


    @FXML
    public void initialize() throws SQLException {
        //HelloController.dao = GeografijaDAO.getInstance();
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

        choiceGrad.getItems().addAll(dajNaziveGradova());
    }

    public void prihvatiUnos(ActionEvent actionEvent) {
        System.out.println("Kliknuto");
        String nazivDrzave = fieldNaziv.getText();
        String nazivGrada = choiceGrad.getValue();
        Drzava drzava = HelloController.dao.nadjiDrzavu(nazivDrzave);
        Grad grad = HelloController.dao.nadjiGrad(nazivGrada);
        if(drzava == null) {
            HelloController.dao.dodajDrzavu(new Drzava(1, nazivDrzave, grad));
            Drzava drzava2 = HelloController.dao.nadjiDrzavu(nazivDrzave);
            System.out.println(drzava2.getId() + " " + drzava2.getNaziv() + " " + drzava2.getGlavniGrad());
        }
        else {
            HelloController.dao.izmijeniDrzavu(nazivDrzave, grad.getId());
        }
    }

    public void cancelUnos(ActionEvent actionEvent) {
        Stage stage = (Stage)fieldNaziv.getScene().getWindow();
        stage.close();
    }

    private ArrayList<String> dajNaziveGradova() {
        ArrayList<String> naziviGradova = new ArrayList<String>();
        ArrayList<Grad> gradovi = HelloController.dao.gradovi();
        for (Grad grad : gradovi) {
            naziviGradova.add(grad.getNaziv());
        }
        return naziviGradova;
    }
}
