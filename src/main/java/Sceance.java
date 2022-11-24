
import java.time.LocalDateTime;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michel
 */
public class Sceance {

    private Integer id;
    private List<Echantillon> echantillons;
    private String description;
    private LocalDateTime date;
    private String etat;  // marche, arret, pause
    private Boolean actif; // en cours, terminée

    public Sceance(Integer id, List<Echantillon> echantillons, String description, LocalDateTime date, String etat, Boolean actif) {
        this.id = id;
        this.echantillons = echantillons;
        this.description = description;
        this.date = date;
        this.etat = etat;
        this.actif = actif;
    }

    public Sceance() {

  
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Echantillon> getEchantillons() {
        return echantillons;
    }

    public void setEchantillons(List<Echantillon> echantillons) {
        this.echantillons = echantillons;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

}
