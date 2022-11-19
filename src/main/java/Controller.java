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
    private boolean isErreur;
    private boolean isSequence;
    private boolean isOrdre;
    private boolean isAcquittement;
     private boolean isFichier;
    
    private Rapport rapport = new Rapport();
    private Enregistreur enregistreur = new Enregistreur();

    public Rapport parser(String inputLine) {

        inputLine = inputLine.trim();
        isCompteur = inputLine.startsWith(Constants.TOTAL);
        isActifs = inputLine.startsWith(Constants.ACTIFS);
        isArret = inputLine.startsWith(Constants.ARRET);
        isErreur = inputLine.startsWith(Constants.ERREUR);
        isOrdre = inputLine.startsWith(Constants.ORDRE);
        isSequence = inputLine.startsWith(Constants.SEQUENCE);
        isAcquittement = inputLine.startsWith(Constants.ACQUITTEMENT);
        isFichier = inputLine.startsWith(Constants.FICHIER);

        System.out.println("isCompteur: " + isCompteur);
        System.out.println("isActif: " + isActifs);
        System.out.println("isArret: " + isArret);
        System.out.println("isErreur: " + isErreur);
        System.out.println("isOrdre: " + isOrdre);
        System.out.println("isSequence: " + isSequence);

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

            gestionArret(inputLine);

        }

        if (isErreur) {

            gestionErreurs(inputLine);

        }

        if (isOrdre) {

            gestionOrdres(inputLine);

        }
        
        if (isAcquittement){
        
            gestionSauvegarde(inputLine);
        }
        
         if (isAcquittement){
        
            creationFichier(inputLine);
        }
        
        return rapport;

    }

    private void gestionCompteurs(String inputLine) {

        // @TOTAL:#0:111:222:333 remonte la valeur des compteurs. 111, valeur compteur 1, ...Transmis en fin de séquence pour l'enregsitrement
        // @TOTAL:#1:1233:na:na indique que le total pour l'échantillon 1 est 1233 cycles.
        String[] recept = inputLine.split(":");
        // recept[0] = @TOTAL 
        String ech = recept[1];
        String compteur1 = recept[2];
        String compteur2 = recept[3];
        String compteur3 = recept[4];

        //  System.out.println("num echantillon: " + recept[2]);
        //  System.out.println("Compteur: " + recept[3]);
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

    }

    private void gestionSequence(String inputLine) {

        rapport.setLog("Fin de séquence");
        rapport.setSauvegarde(true);

    }

    private void gestionActifs(String inputLine) {

        String[] recept = inputLine.split(":");

        System.out.println("traitement actifs");
        for (int i = 0; i < 4; i++) {

            System.out.println("recept num: " + i + " " + recept[i]);

        }
        if (recept[1].equals("0")) {

            System.out.println("recept1: " + recept[1]);
            rapport.setActif1(false);

        } else {

            System.out.println("recept1: " + recept[1]);
            rapport.setActif1(true);

        }

        if (recept[2].equals("0")) {
            System.out.println("recept2: " + recept[2]);
            rapport.setActif2(false);

        } else {

            rapport.setActif2(true);
        }

        if (recept[3].startsWith("0")) {

            System.out.println("recept3: " + recept[3]);
            rapport.setActif3(false);

        } else {

            rapport.setActif3(true);
        }

    }

    private void gestionArret(String inputLine) {

        rapport.setLog("Arret de la scéance de test!");

    }

    private void gestionErreurs(String inputLine) {

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

    private void creationFichier(String inputLine) {
        
        enregistreur.creerFichier(inputLine);
    }

}
