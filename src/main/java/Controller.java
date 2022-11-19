
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
public class Controller {
    
    private boolean isCompteur;
    private boolean isActifs;
    private boolean isArret;
    private boolean isPause;
    private boolean isErreur;
    private boolean isSequence;
    private boolean isAcquittement;
    private boolean isFichier;
    private boolean isFin;
    
    private Rapport rapport = new Rapport();
    private Enregistreur enregistreur = new Enregistreur();
    
    public Rapport parser(String inputLine) {
        
        inputLine = inputLine.trim();
        isCompteur = inputLine.startsWith(Constants.TOTAL);
        isActifs = inputLine.startsWith(Constants.ACTIFS);
        isArret = inputLine.startsWith(Constants.ARRETS);
        isPause = inputLine.startsWith(Constants.PAUSES);
        isErreur = inputLine.startsWith(Constants.ERREUR);
        isSequence = inputLine.startsWith(Constants.SEQUENCE);
        isAcquittement = inputLine.startsWith(Constants.ACQUITTEMENT);
        isFichier = inputLine.startsWith(Constants.FICHIER);
        isFin = inputLine.startsWith(Constants.FIN);
        
        System.out.println("isCompteur: " + isCompteur);
        System.out.println("isActif: " + isActifs);
        System.out.println("isArret: " + isArret);
        System.out.println("isArret: " + isPause);
        System.out.println("isErreur: " + isErreur);
        System.out.println("isSequence: " + isSequence);
        System.out.println("isFin: " + isFin);
        
        if (isCompteur) {
            
            System.out.println("inputLine: " + inputLine);
            gestionCompteurs(inputLine);
            
        }
        
        if (isSequence) {
            
            gestionSequence(inputLine);
            
        }
        
        if (isActifs) {
            
            gestionActifs(inputLine);
            
        }
        if (isArret) {
            
            gestionArrets(inputLine);
            
        }
        
        if (isPause) {
            
            gestionPauses(inputLine);
            
        }
        
        if (isErreur) {
            
            gestionErreurs(inputLine);
            
        }
        
        if (isAcquittement) {
            
            gestionAcquittement(inputLine);
            rapport.setAcquittement(false);
            
        }
        
        if (isFichier) {
            
        }
        
        if (isFin) {
            
            gestionFin(inputLine);
        }
        
        return rapport;
        
    }
    
    private void gestionCompteurs(String inputLine) {

        // @TOTAL:#0:111:222:333 remonte la valeur des compteurs. 111, valeur compteur 1, ...Transmis en fin de séquence pour l'enregsitrement
        // @TOTAL:#1:1233:na:na indique que le total pour l'échantillon 1 est 1233 cycles.
        String[] extraction = extraire(inputLine);
        
        String ech = extraction[1];
        String compteur1 = extraction[2];
        String compteur2 = extraction[3];
        String compteur3 = extraction[4];
        
        if (ech.equals("#1")) {
            
            System.out.println("Réception total pour ech 1: " + compteur1);
            rapport.setTotal1(Long.parseLong(compteur1));
            return;
        }
        
        if (ech.equals("#2")) {
            
            System.out.println("Réception total pour ech 2: " + compteur1);
            rapport.setTotal2(Long.parseLong(compteur1));
            return;
        }
        
        if (ech.equals("#3")) {
            
            System.out.println("Réception total pour ech 3: " + compteur1);
            rapport.setTotal3(Long.parseLong(compteur1));
            return;
            
        }
        
        if (ech.equals("#0")) {
            
            System.out.println("Réception total des 3 échantillons: " + compteur1 + ";" + compteur2 + ";" + compteur3);
            rapport.setTotal1(Long.parseLong(compteur1));
            rapport.setTotal2(Long.parseLong(compteur2));
            rapport.setTotal3(Long.parseLong(compteur3));
            return;
            
        }
        
        rapport.setLog("Test en cours");
        rapport.setColor(Color.BLUE);
        
    }
    
    private void gestionSequence(String inputLine) {
        
        rapport.setLog("FIN DE SEQUENCE");
        rapport.setColor(Color.RED);
        rapport.setSauvegarde(true);
        enregistreur.sauvegarder(rapport);
        
    }
    
    private void gestionActifs(String inputLine) {

        // @ACTIFS:1:0:1 remonte l'état d'activation des échantillons 1=actif, 0=inactif
        String[] extraction = extraire(inputLine);
        
        System.out.println("traitement actifs");
        for (int i = 0; i < 4; i++) {
            
            System.out.println("recept num: " + i + " " + extraction[i]);
            
        }
        
        for (int i = 1; i < 4; i++) {
            
            rapport.getActifs()[i - 1] = extraction[i].equals("0") ? false : true;
            System.out.println("actif n°: " + i + " = " + rapport.getActifs()[i - 1]);
        }
        
        String log = "RAPPORT ACTIFS: Ech1: ";
        String s = rapport.getActif1() ? "actif - " : "inactif - ";
        log = log + s;
        s = rapport.getActif2() ? "actif - " : "inactif - ";
        log = log + "Ech2: " + s;
        s = rapport.getActif3() ? "actif" : "inactif";
        log = log + "Ech3: " + s;
        
        rapport.setLog(log);
        rapport.setColor(Color.BLUE);
        
    }
    
    private void gestionArrets(String inputLine) {

        // @ARRETS:1:0:1 notification de l'arrêt de la scéance de test sur tous les échantillons
        String[] extraction = extraire(inputLine);
        
        System.out.println("traitement arrêts");
        for (int i = 0; i < 4; i++) {
            
            System.out.println("recept num: " + i + " " + extraction[i]);
            
        }
        
        for (int i = 1; i < 4; i++) {
            
            rapport.getArrets()[i - 1] = extraction[i].equals("0") ? false : true;
            System.out.println("arrêt n°: " + i + " = " + rapport.getArrets()[i - 1]);
        }
        
        String log = "RAPPORT ARRETS: Ech1: ";
        String s = rapport.getArrets()[0] ? "actif - " : "arrêté - ";
        log = log + s;
        s = rapport.getArrets()[1] ? "actif - " : "arrêté - ";
        log = log + "Ech2: " + s;
        s = rapport.getArrets()[2] ? "actif" : "arrêté";
        log = log + "Ech3: " + s;
        
        rapport.setLog(log);
        rapport.setColor(Color.RED);
        
    }
    
    private void gestionPauses(String inputLine) {

        // @PAUSES:1:0:1 notification de l'arrêt de la scéance de test sur tous les échantillons
        String[] extraction = extraire(inputLine);
        
        System.out.println("traitement pauses");
        for (int i = 0; i < 4; i++) {
            
            System.out.println("recept num: " + i + " " + extraction[i]);
            
        }
        
        for (int i = 1; i < 4; i++) {
            
            rapport.getPauses()[i - 1] = extraction[i].equals("0") ? false : true;
            System.out.println("actif n°: " + i + " = " + rapport.getPauses()[i - 1]);
        }
        
        String log = "RAPPORT PAUSES: Ech1: ";
        String s = rapport.getPauses()[0] ? "actif - " : "en pause - ";
        log = log + s;
        s = rapport.getPauses()[1] ? "actif - " : "en pause - ";
        log = log + "Ech2: " + s;
        s = rapport.getPauses()[2] ? "actif" : "en pause";
        log = log + "Ech3: " + s;
        
        rapport.setLog(log);
        rapport.setColor(Color.ORANGE);
        
    }
    
    private void gestionErreurs(String inputLine) {

        // @ERREURS:#0:1:0:1 remonte les cas d'erreur sur les échantillons 1=erreur, 0=aucune erreur
        String[] extraction = extraire(inputLine);
        
        System.out.println("traitement actifs");
        for (int i = 0; i < 5; i++) {
            
            System.out.println("recept num: " + i + " " + extraction[i]);
            
        }
        
        for (int i = 2; i < 5; i++) {
            
            rapport.getErreurs()[i - 2] = extraction[i].equals("0") ? false : true;
            System.out.println("erreur n°: " + i + " = " + rapport.getErreurs()[i - 2]);
        }
        
        String log = "RAPPORT ERREURS: Ech1: ";
        String s = rapport.getErreur1() ? "actif - " : "en erreur - ";
        log = log + s;
        s = rapport.getErreur2() ? "actif - " : "en erreur - ";
        log = log + "Ech2: " + s;
        s = rapport.getErreur3() ? "actif" : "en erreur";
        log = log + "Ech3: " + s;
        
        rapport.setLog(log);
        rapport.setColor(Color.RED);
        
    }
    
    private void gestionOrdres(String inputLine) {
        
        String[] recept = inputLine.split(":");
        if (recept[1].equals("ACQ")) {
            
            rapport.setSauvegarde(false);
            
        }
    }
    
    private void gestionSauvegarde(String inputLine) {
        
        enregistreur.sauvegarder(rapport);
    }
    
    public int creationFichier(String inputLine) {
        
        int i = enregistreur.creerFichier(inputLine);
        return i;
    }
    
    private String[] extraire(String inputLine) {
        
        String[] extraction = inputLine.split(":");
        
        return extraction;
        
    }
    
    private void gestionFin(String inputLine) {
        
        rapport.setLog("Fin de test");
        rapport.setSauvegarde(true);
        enregistreur.sauvegarder(rapport);
        
    }
    
    private void gestionAcquittement(String inputLine) {
        
        rapport.setAcquittement(true);
        rapport.setLog("Lancement du test!");
        rapport.setColor(Color.RED);
    }
    
}
