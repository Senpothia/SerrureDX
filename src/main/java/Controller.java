
import java.awt.Color;
import java.io.IOException;
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
    private boolean isFermeture;
    private Context context = new Context();

    private Rapport rapport = new Rapport();
    private FormSeance formSceance = new FormSeance();
    private Enregistreur enregistreur = new Enregistreur();
    private RemoteController remoteController = new RemoteController();

    public Rapport parser(String inputLine) throws IOException {

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
        isFermeture = inputLine.startsWith(Constants.ACQ_FERMER);
        rapport.setAcquittement(false);

        System.out.println("isCompteur: " + isCompteur);
        System.out.println("isActif: " + isActifs);
        System.out.println("isArret: " + isArret);
        System.out.println("isArret: " + isPause);
        System.out.println("isErreur: " + isErreur);
        System.out.println("isSequence: " + isSequence);
        System.out.println("isFin: " + isFin);
        System.out.println("isFermeture: " + isFermeture);
        System.out.println("isAcquittement: " + isAcquittement);

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

        }

        if (isFichier) {

        }

        if (isFin) {

            gestionFin(inputLine);
        }

        if (isFermeture) {
            gestionFermeture(inputLine);

        }

        rapport.setFormSeance(formSceance);
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
            formSceance.setCompteur1(Long.parseLong(compteur1));

        }

        if (ech.equals("#2")) {

            System.out.println("Réception total pour ech 2: " + compteur1);
            formSceance.setCompteur2(Long.parseLong(compteur2));

        }

        if (ech.equals("#3")) {

            System.out.println("Réception total pour ech 3: " + compteur1);
            formSceance.setCompteur3(Long.parseLong(compteur3));

        }

        if (ech.equals("#0")) {

            System.out.println("Réception total des 3 échantillons: " + compteur1 + ";" + compteur2 + ";" + compteur3);

            formSceance.setCompteur1(Long.parseLong(compteur1));
            System.out.println("formSceance: compteur1: " + formSceance.getCompteur1());
            formSceance.setCompteur2(Long.parseLong(compteur2));
            System.out.println("formSceance: compteur2: " + formSceance.getCompteur2());
            formSceance.setCompteur3(Long.parseLong(compteur3));
            System.out.println("formSceance: compteur3: " + formSceance.getCompteur3());

        }

        rapport.setLog("Test en cours");
        rapport.setColor(Color.BLUE);

    }

    private void gestionSequence(String inputLine) throws IOException {

        rapport.setLog("FIN DE SEQUENCE");
        rapport.setColor(Color.RED);
        rapport.setSauvegarde(true);
        enregistreur.sauvegarder(rapport);   //  sauvegardes en locale
        if (context.isWithoutRemote()) {

            remoteController.sauvegarderSequence(context.getFormSceance(), context.getLogin());

        }

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

        formSceance.setActif1(rapport.getActif1());
        formSceance.setActif2(rapport.getActif2());
        formSceance.setActif3(rapport.getActif3());

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

        formSceance.setInterrompu1(rapport.getArrets()[0]);
        formSceance.setInterrompu2(rapport.getArrets()[1]);
        formSceance.setInterrompu3(rapport.getArrets()[2]);

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

        formSceance.setPause1(rapport.getPauses()[0]);
        formSceance.setPause2(rapport.getPauses()[1]);
        formSceance.setPause3(rapport.getPauses()[2]);

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

        formSceance.setErreur1(rapport.getErreurs()[0]);
        formSceance.setErreur2(rapport.getErreurs()[1]);
        formSceance.setErreur3(rapport.getErreurs()[2]);

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

    private void gestionFermeture(String inputLine) {

        rapport.setLog("Fermeture en cours!");
        rapport.setColor(Color.RED);
        rapport.setSauvegarde(true);
        try {

            rapport.setFermeture(true);
            enregistreur.sauvegarder(rapport);

        } catch (Exception e) {
        }

    }

    public boolean enregistrerSceance(FormSeance sceance, Login login) {

        if (context.isConnexionRemoteActive()) {

            try {
                boolean result = remoteController.enregistrerSceance(sceance, login);
                if (!result) {

                    return false;
                } else {

                    return true;
                }
            } catch (IOException ex) {
                Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        }
        return false;

    }

    public boolean connexionRemote(Login login) throws IOException {

        if (!context.isWithoutRemote()) {

            boolean autorisation = remoteController.connexionRequest(login);
            return autorisation;
        }
        return false;

    }

    public FormSeance getSceance(String idSceance, Login login) throws IOException {

        if (!context.isWithoutRemote()) {

            FormSeance f = remoteController.getSceance(idSceance, login);
            return f;

        }
        return null;

    }

    boolean modifierSceance(FormSeance sceance, Login login) {

        if (!context.isWithoutRemote()) {

            try {
                boolean result = remoteController.modifierSceance(sceance, login);
                if (!result) {

                    return false;
                } else {

                    return true;
                }
            } catch (IOException ex) {
                Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        }
        return false;

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    void actualiserSceance(FormSeance formSeance, Login login) throws IOException {

        if (!context.isWithoutRemote()) {

            boolean reponse = remoteController.actualiserSceance(formSeance, login);
        }

    }

}
