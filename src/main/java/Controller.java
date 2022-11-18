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

    private String actifs = "@ACTIFS";
    private String total = "@TOTAL";
    private String arret = "@ARRET";
    private String sequence = "@SEQ";
    private String erreur = "@ERREUR";
    private String message = "W:";

    private boolean isCompteur;
    private boolean isActifs;
    private boolean isArret;
    private boolean isErreur;
    private boolean isSequence;
    private boolean isOrdre;
    private Rapport rapport = new Rapport();

    public Rapport parser(String inputLine) {

        isCompteur = inputLine.startsWith(Constants.TOTAL);
        isActifs = inputLine.startsWith(Constants.ACTIFS);
        isArret = inputLine.startsWith(Constants.ARRET);
        isErreur = inputLine.startsWith(Constants.ERREUR);
        isOrdre = inputLine.startsWith(Constants.ORDRE);
        isSequence = inputLine.startsWith(Constants.SEQUENCE);

        System.out.println("isCompteur: " + isCompteur);
        System.out.println("isActif: " + isActifs);
        System.out.println("isArret: " + isArret);
        System.out.println("isErreur: " + isErreur);
        System.out.println("isOrdre: " + isOrdre);
        System.out.println("isSequence: " + isSequence);

        if (isCompteur) {

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
        return rapport;

    }

    private void gestionCompteurs(String inputLine) {

        String[] recept = inputLine.split(" ");
        String compteur = recept[3];
        String ech = recept[2];
        //  System.out.println("num echantillon: " + recept[2]);
        //  System.out.println("Compteur: " + recept[3]);

        if (ech.equals("#1:")) {

            rapport.setTotal1(Long.parseLong(compteur));

        }

        if (ech.equals("#2:")) {

            rapport.setTotal2(Long.parseLong(compteur));
        }

        if (ech.equals("#3:")) {

            rapport.setTotal3(Long.parseLong(compteur));

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

}
