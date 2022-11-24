
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michel
 */
public class Enregistreur {

    private FileWriter fluxSortie;
    private BufferedWriter Sortie;
    private String nomDeFichier;

    public int creerFichier(String inputLine) {

        nomDeFichier = inputLine;
        // Initialisation flux de sortie
        try {
            fluxSortie = new FileWriter(inputLine);
            Sortie = new BufferedWriter(fluxSortie);

            Sortie.write("Date;Heure;Echantillon1;Echantillon2;Echantillon3");
            Sortie.newLine();
            return 0;

        } catch (Exception ex) {

            // System.err.println("Erreur création de fichier de sauvegarde");
            // System.err.println(ex);
            return -1;

        }
    }

    public int initFichier() {

        // Initialisation flux de sortie
        try {
            fluxSortie = new FileWriter(nomDeFichier);
            Sortie = new BufferedWriter(fluxSortie);

            Sortie.write("Date;Heure;Echantillon1;Echantillon2;Echantillon3;Actif1;Actif2;Actif3;Pause1;Pause2;Pause3;Erreur1;Erreur2;Erreur3");
            Sortie.newLine();
            return 0;

        } catch (Exception ex) {

            return -1;

        }

    }

    private void sauvegarder(String chaine) {

        try {
            //
            Sortie.write(chaine);
            Sortie.newLine();
            Sortie.close();

        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int sauvegarder(Rapport rapport) {

        LocalDateTime dateActuelle = LocalDateTime.now();
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatterHeure = DateTimeFormatter.ofPattern("HH:mm:ss");
        String date = dateActuelle.format(formatterDate);
        String heure = dateActuelle.format(formatterHeure);
        initFichier();
        String ligneEnCours = date + ";" + heure + ";" + rapport.getFormSeance().getCompteur1() + ";" + rapport.getFormSeance().getCompteur2() + ";" + rapport.getFormSeance().getCompteur3() + ";" + rapport.getFormSeance().getActif1() + ";" + rapport.getFormSeance().getActif2() + ";" + rapport.getFormSeance().getActif3();
        sauvegarder(ligneEnCours);

        return 0;

    }

}
