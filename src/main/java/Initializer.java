
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michel
 */
public class Initializer {

    public Initialisation getInit() throws FileNotFoundException, IOException {

        Properties cloudProperpies = new Properties();
        
        FileReader reader = new FileReader("src\\main\\java\\remote.properties");
       // FileReader reader = new FileReader(".\\remote.properties");
        cloudProperpies.load(reader);

        String username = cloudProperpies.getProperty("username");
        String password = cloudProperpies.getProperty("password");
        String remoteUrls = cloudProperpies.getProperty("remoteUrls");
        String remoteNames = cloudProperpies.getProperty("remoteNames");
        String sceance = cloudProperpies.getProperty("sceance");

        List<String> listeRemotesUrls = getRemoteUrls(remoteUrls);
        List<String> listeRemotesNames = getRemoteUrls(remoteNames);
        Initialisation init = new Initialisation(username, password, listeRemotesUrls, listeRemotesNames, sceance);

        init.setUsername(username);
        init.setPassword(password);
        init.setRemoteUrls(listeRemotesUrls);
        init.setRemoteNames(listeRemotesNames);
        init.setSceance(sceance);

        System.out.println("username: " + username);
        System.out.println("password: " + password);
        System.out.println("Nombre de url remotes: " + listeRemotesUrls.size());
        System.out.println("Nombre de noms remotes: " + listeRemotesNames.size());
        System.out.println("sceance id: " + sceance);

        for (String r : listeRemotesUrls) {

            System.out.println("remote url:" + r);

        }

        return init;
    }

    private List<String> getRemoteUrls(String remoteUrls) {

        List<String> listeRemoteUrls = new ArrayList<String>();

        String[] extraction = extraire(remoteUrls);

        for (int i = 0; i < extraction.length; i++) {

            listeRemoteUrls.add(extraction[i]);
        }

        return listeRemoteUrls;
    }

    private List<String> getRemoteNames(String remoteNames) {

        List<String> listeRemoteNames = new ArrayList<String>();

        String[] extraction = extraire(remoteNames);

        for (int i = 0; i < extraction.length; i++) {

            listeRemoteNames.add(extraction[i]);
        }

        return listeRemoteNames;
    }

    private String[] extraire(String remotes) {

        String[] extraction = remotes.split(";");

        return extraction;

    }

    public void update(String key, String value) {

        try {

            Properties cloudProperpies = new Properties();
            //first load old one:

            FileInputStream configStream = new FileInputStream("src\\main\\java\\remote.properties");
            //   FileInputStream configStream = new FileInputStream(".\\remote.properties");
            cloudProperpies.load(configStream);
            configStream.close();

            //modifies existing or adds new property
            cloudProperpies.setProperty(key, value);

            //save modified property file
            FileOutputStream output = new FileOutputStream("src\\main\\java\\remote.properties");
            //FileOutputStream output = new FileOutputStream(".\\remote.properties");
            cloudProperpies.store(output, "DX200I tester - Properties");
            output.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    void addRemote(String newRemoteName, String newRemoteAdress) throws FileNotFoundException, IOException {

        Properties cloudProperpies = new Properties();

        FileReader reader = new FileReader("src\\main\\java\\remote.properties");
        //FileReader reader = new FileReader(".\\remote.properties");
        cloudProperpies.load(reader);

        String remoteUrls = cloudProperpies.getProperty("remoteUrls");
        String remoteNames = cloudProperpies.getProperty("remoteNames");

        remoteNames = remoteNames + ";" + newRemoteName;
        remoteUrls = remoteUrls + ";" + "http://" + newRemoteAdress;

        System.out.println("Noms de remotes: " + remoteNames);
        System.out.println("URLs de remotes: " + remoteUrls);
        update("remoteNames", remoteNames);
        update("remoteUrls", remoteUrls);

    }

    void deleteRemote(String nom) throws FileNotFoundException, IOException {

        Properties cloudProperpies = new Properties();

        FileReader reader = new FileReader("src\\main\\java\\remote.properties");
       // FileReader reader = new FileReader(".\\remote.properties");
        cloudProperpies.load(reader);

        String remoteUrls = cloudProperpies.getProperty("remoteUrls");
        String remoteNames = cloudProperpies.getProperty("remoteNames");
        String[] extraction = extraire(remoteNames);
        int indiceASupprimer = -1;
        boolean finded = false;
        while (!finded) {
            indiceASupprimer++;
            if (extraction[indiceASupprimer].equals(nom)) {

                finded = true;
            }
        }
        System.out.println("Indice du remote correspondant au nom à supprimer: " + indiceASupprimer);
        String nouveauNomdeRemote = "";

        for (int i = 0; i < extraction.length; i++) {

            System.out.println("Valeur extraction " + i + ":" + extraction[i]);
            if (i != indiceASupprimer) {

                if (i != extraction.length - 1) {

                    if (!extraction[i].equals("")) {

                        nouveauNomdeRemote = nouveauNomdeRemote + extraction[i] + ";";
                    }

                } else {

                    if (!extraction[i].equals("")) {
                        nouveauNomdeRemote = nouveauNomdeRemote + extraction[i];
                    }

                }

            }

        }

        System.out.println("Nouveaux remoteNames: " + nouveauNomdeRemote);

        extraction = extraire(remoteUrls);

        String nouveauUrlsDeRemote = "";

        for (int i = 0; i < extraction.length; i++) {

            if (i != indiceASupprimer) {

                if (i != extraction.length - 1) {

                    nouveauUrlsDeRemote = nouveauUrlsDeRemote + extraction[i] + ";";
                } else {

                    nouveauUrlsDeRemote = nouveauUrlsDeRemote + extraction[i];

                }

            }

        }

        System.out.println("Nouveaux remoteUrls: " + nouveauUrlsDeRemote);

        update("remoteNames", nouveauNomdeRemote);
        update("remoteUrls", nouveauUrlsDeRemote);

    }

}
