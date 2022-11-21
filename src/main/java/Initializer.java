
import java.io.FileNotFoundException;
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
        cloudProperpies.load(reader);

        String username = cloudProperpies.getProperty("username");
        String password = cloudProperpies.getProperty("password");
        String remoteUrls = cloudProperpies.getProperty("remoteUrls");
        String remoteNames = cloudProperpies.getProperty("remoteNames");

        List<String> listeRemotesUrls = getRemoteUrls(remoteUrls);
        List<String> listeRemotesNames = getRemoteUrls(remoteNames);
        Initialisation init = new Initialisation(username, password, listeRemotesUrls,listeRemotesNames);

        init.setUsername(username);
        init.setPassword(password);

        System.out.println("username: " + username);
        System.out.println("password: " + password);
        System.out.println("Nombre de remotes: " + listeRemotesUrls.size());
        for (String r : listeRemotesUrls) {

            System.out.println("remote:" + r);
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

}
