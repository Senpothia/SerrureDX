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
    private String acquittement ="W:";

    private boolean isCompteur;
    private boolean isActifs;
    private boolean isArret;
    private boolean isErreur;
    private boolean isSequence;
    private boolean isMessage;
    private Rapport rapport = new Rapport();

    public Rapport parser(String inputLine) {

        isCompteur = inputLine.startsWith("@TOTAL");
        isActifs = inputLine.startsWith("@ACTIFS");
        isArret = inputLine.startsWith("@ARRET");
        isErreur = inputLine.startsWith("@Erreur");
        isMessage = inputLine.startsWith("W:");
        isSequence = inputLine.startsWith("@SEQ");

        System.out.println("isCompteur: " + isCompteur);
        System.out.println("isActif: " + isActifs);
        System.out.println("isArret: " + isArret);

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
        
        if (isMessage){
            
            gestionMessages(inputLine);
        
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

    private void gestionMessages(String inputLine) {
        
        
        String[] recept = inputLine.split(":");
        if(recept[1].equals("ACQ")){
        
            rapport.setSauvegarde(false);
        
        }
    }

}
