/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Michel
 */
public class Rapport {

    private boolean[] actifs = {false, false, false};
    private boolean[] erreurs = {false, false, false};
    private long[] totaux = {0, 0, 0};
    private boolean sauvegarde = false;
    boolean marche = false;
    boolean pause = false;
    String log = "";

    public boolean[] getActifs() {
        return actifs;
    }

    public void setActifs(boolean[] actifs) {
        this.actifs = actifs;
    }

    public boolean[] getErreurs() {
        return erreurs;
    }

    public void setErreurs(boolean[] erreurs) {
        this.erreurs = erreurs;
    }

    public long[] getTotaux() {
        return totaux;
    }

    public void setTotaux(long[] totaux) {
        this.totaux = totaux;
    }

    public boolean isMarche() {
        return marche;
    }

    public void setMarche(boolean marche) {
        this.marche = marche;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setTotal1(long total1) {
        this.totaux[0] = total1;
    }

    public void setTotal2(long total2) {
        this.totaux[1] = total2;
    }

    public void setTotal3(long total3) {
        this.totaux[2] = total3;
    }

    public long getTotal1() {
        return totaux[0];
    }

    public long getTotal2() {
        return totaux[1];
    }

    public long getTotal3() {
        return totaux[2];
    }

    public boolean getActif1() {
        return actifs[0];
    }

    public boolean getActif2() {
        return actifs[1];
    }

    public boolean getActif3() {
        return actifs[2];
    }

    public void setActif1(boolean actifs1) {
        this.actifs[0] = actifs1;
    }

    public void setActif2(boolean actifs2) {
        this.actifs[1] = actifs2;
    }

    public void setActif3(boolean actifs3) {
        this.actifs[2] = actifs3;
    }

    public boolean getErreur1() {
        return erreurs[0];
    }

    public boolean getErreur2() {
        return erreurs[1];
    }

    public boolean getErreur3() {
        return erreurs[2];
    }

    public void setErreur1(boolean erreur1) {
        this.erreurs[0] = erreur1;
    }

    public void setErreur2(boolean erreur2) {
        this.erreurs[1] = erreur2;
    }
    
     public void setErreur3(boolean erreur3) {
        this.erreurs[2] = erreur3;
    }

    public boolean isSauvegarde() {
        return sauvegarde;
    }

    public void setSauvegarde(boolean sauvegarde) {
        this.sauvegarde = sauvegarde;
    }
     
     
}
