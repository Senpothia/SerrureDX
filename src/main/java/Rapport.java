
import java.awt.Color;

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
    private boolean[] arrets = {false, false, false};
    private boolean[] pauses = {false, false, false};
    private long[] totaux = {0, 0, 0};
    
    private boolean sauvegarde = false;
    private boolean marche = false;
    private boolean pause = false;
    private boolean acquittement = false;
    private boolean fermeture = false;
    private boolean fin = false;
    private FormSeance formSeance = new FormSeance();
    String log = "";
    Color color = Color.BLACK;
    private int message = 0;
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
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
    
    public boolean[] getArrets() {
        return arrets;
    }
    
    public void setArrets(boolean[] arrets) {
        this.arrets = arrets;
    }
    
    public boolean[] getPauses() {
        return pauses;
    }
    
    public void setPauses(boolean[] pauses) {
        this.pauses = pauses;
    }
    
    public boolean isAcquittement() {
        return acquittement;
    }
    
    public void setAcquittement(boolean acquittement) {
        this.acquittement = acquittement;
    }
    
    public boolean isFermeture() {
        return fermeture;
    }
    
    public void setFermeture(boolean fermeture) {
        this.fermeture = fermeture;
    }
    
    public FormSeance getFormSeance() {
        return formSeance;
    }
    
    public void setFormSeance(FormSeance formSeance) {
        this.formSeance = formSeance;
    }
    
    public int getMessage() {
        return message;
    }
    
    public void setMessage(int message) {
        this.message = message;
    }
    
    public boolean isFin() {
        return fin;
    }
    
    public void setFin(boolean fin) {
        this.fin = fin;
    }
    
    void resetSequenceVariables() {
        
        boolean[] tab = {false, false, false};
        setActifs(tab);
        setArrets(tab);
        setErreurs(tab);
        setPauses(tab);
    }
    
}
