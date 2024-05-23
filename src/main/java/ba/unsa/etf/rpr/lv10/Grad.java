package ba.unsa.etf.rpr.lv10;

import javafx.beans.property.SimpleStringProperty;

public class Grad {
    private int id, broj_stanovnika;
    private SimpleStringProperty naziv;

    private Drzava drzava;

    private SimpleStringProperty nazivDrzave;

    public Grad() {
        naziv = new SimpleStringProperty();
        nazivDrzave = new SimpleStringProperty();
    }

    public Grad(int id, String naziv, int broj_stanovnika, Drzava drzava) {
        this.id = id;
        this.naziv = new SimpleStringProperty(naziv);
        this.broj_stanovnika = broj_stanovnika;
        this.drzava = drzava;
        if(drzava != null) this.nazivDrzave = new SimpleStringProperty(drzava.getNaziv());
        else this.nazivDrzave = new SimpleStringProperty();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBroj_stanovnika() {
        return broj_stanovnika;
    }

    public void setBroj_stanovnika(int broj_stanovnika) {
        this.broj_stanovnika = broj_stanovnika;
    }

    public String getNaziv() {
        return naziv.get();
    }

    public void setNaziv(String naziv) {
        this.naziv.set(naziv);
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
        if(drzava != null) this.nazivDrzave.set(drzava.getNaziv());
    }

    public String getNazivDrzave() {
        return nazivDrzave.get();
    }

    public void setNazivDrzave(String nazivDrzave) {
        this.nazivDrzave.set(nazivDrzave);
    }

    public SimpleStringProperty nazivProperty() {
        return naziv;
    }

    public SimpleStringProperty nazivDrzaveProperty() {
        return nazivDrzave;
    }

    public SimpleStringProperty broj_StanovnikaProperty() {
        return new SimpleStringProperty(((Integer)broj_stanovnika).toString());
    }
}
