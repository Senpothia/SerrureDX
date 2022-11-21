
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
        String remotes = cloudProperpies.getProperty("remotes");

        List<String> listeRemotes = getRemotes(remotes);
        Initialisation init = new Initialisation(username, password, listeRemotes);

        init.setUsername(username);
        init.setPassword(password);

        System.out.println("username: " + username);
        System.out.println("password: " + password);
        System.out.println("Nombre de remotes: " + listeRemotes.size());
        for(String r: listeRemotes){
            
            System.out.println("remote:" + r);
        }

        return init;
    }

    private List<String> getRemotes(String remotes) {

        List<String> listeRemotes = new ArrayList<String>();

        String[] extraction = extraire(remotes);

        for (int i = 0; i < extraction.length; i++) {

            listeRemotes.add(extraction[i]);
        }

        return listeRemotes;
    }

    private String[] extraire(String remotes) {

        String[] extraction = remotes.split(";");

        return extraction;

    }

}
