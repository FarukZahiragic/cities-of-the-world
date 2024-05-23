package ba.unsa.etf.rpr.lv10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private Connection conn;
    private PreparedStatement pretragaUpit, noviIdGradUpit, noviIdDrzavaUpit, izmijenaUpit,
            brisanjeUpit, brisanjeGradovaUpit, glavniGradUpit, dodavanjeDrzaveUpit,
            pretragaDrzavaUpit, pretragaDrzavaPoIdUpit, dajGradUpit, dajGradNazivUpit, izmijenaDrzaveUpit,
            brisanjeGradaUpit, dajDrzaveUpit;

    private GeografijaDAO() throws SQLException {
        String url = "jdbc:sqlite:" + System.getProperty("user.home") + "/lv9/baza.db";
        conn = DriverManager.getConnection(url);
        try {
            pretragaUpit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, g.drzava FROM grad g ORDER BY broj_stanovnika DESC");
        } catch (SQLException e) {
            kreirajBazu();
            pretragaUpit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, g.drzava FROM grad g ORDER BY broj_stanovnika DESC");
        }
        noviIdGradUpit = conn.prepareStatement("SELECT MAX(id)+1 FROM grad");
        noviIdDrzavaUpit = conn.prepareStatement("SELECT MAX(id)+1 FROM drzava");
        izmijenaUpit = conn.prepareStatement("UPDATE grad SET naziv=?, broj_stanovnika=?, drzava=? WHERE id=?");
        brisanjeUpit = conn.prepareStatement("DELETE FROM drzava WHERE id=?");
        brisanjeGradovaUpit = conn.prepareStatement("DELETE FROM grad WHERE drzava=?");
        glavniGradUpit = conn.prepareStatement(
                "SELECT g.id, g.naziv, g.broj_stanovnika, g.drzava FROM grad g, drzava d WHERE g.drzava = d.id AND d.naziv=?");
        dodavanjeDrzaveUpit = conn.prepareStatement("INSERT INTO drzava VALUES(?, ?, ?)");
        pretragaDrzavaUpit = conn.prepareStatement("SELECT d.id, d.naziv, d.glavni_grad from drzava d WHERE d.naziv=?");
        pretragaDrzavaPoIdUpit = conn.prepareStatement("SELECT d.id, d.naziv, d.glavni_grad from drzava d WHERE d.id=?");
        dajGradUpit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, g.drzava from grad g WHERE g.id=?");
        dajGradNazivUpit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, g.drzava from grad g WHERE g.naziv=?");
        izmijenaDrzaveUpit = conn.prepareStatement("UPDATE drzava SET naziv=? WHERE glavni_grad=?");
        brisanjeGradaUpit = conn.prepareStatement("DELETE FROM grad WHERE naziv = ?");
        dajDrzaveUpit = conn.prepareStatement("SELECT d.id, d.naziv, d.glavni_grad from drzava d");
    }

    private void kreirajBazu() {
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("baza.sql"));
            String sqlUpit = "";
            while (ulaz.hasNext()) {
                sqlUpit += ulaz.nextLine();
                if ( sqlUpit.length() > 1 && sqlUpit.charAt( sqlUpit.length()-1 ) == ';') {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sqlUpit);
                        sqlUpit = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            ulaz.close();
        } catch (FileNotFoundException e) {
            System.out.println("Ne postoji SQL datoteka... nastavljam sa praznom bazom");
        }
    }

    // Metoda za potrebe testova, vraća bazu u polazno stanje
    public synchronized void vratiGradoveNaDefault() {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM grad");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void vratiDrzaveNaDefault() {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM drzava");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void dodajGradove() {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO grad VALUES (1,'Bec',1500000,1)");
            stmt.executeUpdate("INSERT INTO grad VALUES (2,'London',10000000,2)");
            stmt.executeUpdate("INSERT INTO grad VALUES (3,'Pariz',5000000,3)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void dodajDrzave() {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO drzava VALUES (1,'Austrija',1)");
            stmt.executeUpdate("INSERT INTO drzava VALUES (2,'Engleska',2)");
            stmt.executeUpdate("INSERT INTO drzava VALUES (3,'Francuska',3)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static GeografijaDAO getInstance() throws SQLException {
        if (instance == null) instance = new GeografijaDAO();
        return instance;
    }

    public static void removeInstance() throws SQLException {
        if (instance == null) return;
        instance.conn.close();
        instance=null;
    }

    ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradovi = new ArrayList<>();
        try {
            ResultSet rs = pretragaUpit.executeQuery();
            while (rs.next()) {
                gradovi.add(dajGradIzResultSeta(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return gradovi;
    }

    ArrayList<Drzava> drzave() {
        ArrayList<Drzava> drzave = new ArrayList<Drzava>();
        try {
            ResultSet rs = dajDrzaveUpit.executeQuery();
            while (rs.next()) {
                drzave.add(dajDrzavuIzResultSeta(rs, dajGrad(rs.getInt(3))));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return drzave;
    }

    private Grad dajGradIzResultSeta(ResultSet rs) throws SQLException {
        Grad grad = new Grad(rs.getInt(1), rs.getString(2), rs.getInt(3), null);
        grad.setDrzava(dajDrzavu(rs.getInt(4), grad));
        return grad;
    }

    public Drzava dajDrzavu(int id, Grad grad) {
        try {
            pretragaDrzavaPoIdUpit.setInt(1, id);
            ResultSet rs = pretragaDrzavaPoIdUpit.executeQuery();
            if(!rs.next()) return null;
            return dajDrzavuIzResultSeta(rs, grad);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Drzava dajDrzavuIzResultSeta(ResultSet rs, Grad grad) throws SQLException {
        return new Drzava(rs.getInt(1), rs.getString(2), grad);
    }

    public void dodajGrad(Grad grad) {
        try {
            ResultSet rs = noviIdGradUpit.executeQuery();
            if (rs.next())
                grad.setId(rs.getInt(1));
            else
                grad.setId(1);

            PreparedStatement dodavanjeUpit = conn.prepareStatement("INSERT INTO grad VALUES(?, ?, ?, ?)");
            dodavanjeUpit.setInt(1, grad.getId());
            dodavanjeUpit.setString(2, grad.getNaziv());
            dodavanjeUpit.setInt(3, grad.getBroj_stanovnika());
            dodavanjeUpit.setInt(4, grad.getDrzava().getId());
            dodavanjeUpit.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            ResultSet rs = noviIdDrzavaUpit.executeQuery();
            if (rs.next())
                drzava.setId(rs.getInt(1));
            else
                drzava.setId(1);

            dodavanjeDrzaveUpit.setInt(1, drzava.getId());
            dodavanjeDrzaveUpit.setString(2, drzava.getNaziv());
            dodavanjeDrzaveUpit.setInt(3, drzava.getGlavniGrad().getId());
            dodavanjeDrzaveUpit.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void izmijeniGrad(Grad grad) {
        try {
            izmijenaUpit.setInt(4, grad.getId());
            izmijenaUpit.setString(1, grad.getNaziv());
            izmijenaUpit.setInt(2, grad.getBroj_stanovnika());
            izmijenaUpit.setInt(3, grad.getDrzava().getId());
            izmijenaUpit.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void izmijeniDrzavu(String nazivDrzave, int idGlavnogGrada) {
        try {
            izmijenaDrzaveUpit.setString(1, nazivDrzave);
            izmijenaDrzaveUpit.setInt(2, idGlavnogGrada);
            izmijenaDrzaveUpit.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void obrisiDrzavu(String nazivDrzave) {
        try {
            pretragaDrzavaUpit.setString(1, nazivDrzave);
            ResultSet rs = pretragaDrzavaUpit.executeQuery();
            if(!rs.next()) return;
            Drzava drzava = dajDrzavuIzResultSeta(rs, null);

            brisanjeGradovaUpit.setInt(1, drzava.getId());
            brisanjeGradovaUpit.executeUpdate();


            brisanjeUpit.setInt(1, drzava.getId());
            brisanjeUpit.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void obrisiGrad(String nazivGrada) {
        try {
            brisanjeGradaUpit.setString(1, nazivGrada);
            brisanjeGradaUpit.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Grad glavniGrad(String drzava) {
        try {
            glavniGradUpit.setString(1, drzava);
            ResultSet rs = glavniGradUpit.executeQuery();
            if(!rs.next()) return null;
            return dajGradIzResultSeta(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Grad dajGrad(int id) {
        try {
            dajGradUpit.setInt(1, id);
            ResultSet rs = dajGradUpit.executeQuery();
            if(!rs.next()) return null;
            return dajGradIzResultSeta(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Drzava nadjiDrzavu(String drzava) {
        try {
            pretragaDrzavaUpit.setString(1, drzava);
            ResultSet rs = pretragaDrzavaUpit.executeQuery();
            if(!rs.next()) return null;
            return dajDrzavuIzResultSeta(rs, dajGrad(rs.getInt(3)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Grad nadjiGrad(String grad) {
        try {
            dajGradNazivUpit.setString(1, grad);
            ResultSet rs = dajGradNazivUpit.executeQuery();
            if(!rs.next()) return null;
            return dajGradIzResultSeta(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<Grad> sviGradovi() {
        return FXCollections.observableArrayList(gradovi());
    }

    public ObservableList<Drzava> sveDrzave() {
        return FXCollections.observableArrayList(drzave());
    }

}
