package ba.unsa.etf.rpr.lv10;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class HelloController {
    @FXML
    public TableView<Grad> tableViewGradovi;
    @FXML
    public TableColumn<Grad, Integer> colGradId;
    @FXML
    public TableColumn<Grad, String> colGradNaziv;
    @FXML
    public TableColumn<Grad, Integer> colGradStanovnika;
    @FXML
    public TableColumn<Grad, String> colGradDrzava;
    public static GeografijaDAO dao;
    @FXML
    public void initialize() throws SQLException {
        dao = GeografijaDAO.getInstance();

        /*ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> {
            dao.vratiGradoveNaDefault();
            Thread.yield();
        });
        executor.execute(() -> {
            dao.vratiDrzaveNaDefault();
            Thread.yield();
        });
        executor.shutdown();

        executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> {
            dao.dodajDrzave();
            Thread.yield();
        });
        executor.execute(() -> {
            dao.dodajGradove();
            Thread.yield();
        });
        executor.shutdown();*/
        //tableViewGradovi = new TableView<Grad>();
        colGradId.setCellValueFactory(new PropertyValueFactory<Grad, Integer>("id"));
        colGradNaziv.setCellValueFactory(new PropertyValueFactory<Grad, String>("naziv"));
        colGradStanovnika.setCellValueFactory(new PropertyValueFactory<Grad, Integer>("broj_stanovnika"));
        colGradDrzava.setCellValueFactory(new PropertyValueFactory<Grad, String>("nazivDrzave"));

        tableViewGradovi.setItems(dao.sviGradovi());
    }

    public void openDrzavaForm(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("drzava.fxml"));
        Parent root = loader.load();
        DrzavaController noviprozor = loader.getController();
        //noviprozor.labela.setText(noviprozor.labela.getText() + fieldUsername.getText());

        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        stage.setTitle("Unos informacija za drzavu");
        stage.setScene(scene);
        stage.show();
    }

    public void openGradForm(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("grad.fxml"));
        Parent root = loader.load();
        GradController noviprozor = loader.getController();
        //noviprozor.labela.setText(noviprozor.labela.getText() + fieldUsername.getText());

        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        stage.setTitle("Unos informacija za grad");
        stage.setScene(scene);
        stage.show();
    }
}