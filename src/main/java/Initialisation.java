
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michel
 */
public class Initialisation {

    private String username;
    private String password;
    private List<String> remoteUrls = new ArrayList<>();
    private List<String> remoteNames = new ArrayList<>();;
    private String remoteName;
    private String remoteUrl;
    private String sceance;

    public Initialisation() {
    }

    public Initialisation(String username, String password, List<String> remoteUrls, List<String> remoteNames, String remoteName, String remoteUrl, String sceance) {
        this.username = username;
        this.password = password;
        this.remoteUrls = remoteUrls;
        this.remoteNames = remoteNames;
        this.remoteName = remoteName;
        this.remoteUrl = remoteUrl;
        this.sceance = sceance;
    }
    
    

    Initialisation(String username, String password, List<String> listeRemotesUrls, List<String> listeRemotesNames, String sceance) {

        this.username = username;
        this.password = password;
        this.remoteUrls = remoteUrls;
        this.remoteNames = remoteNames;
         this.sceance = sceance;
      
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRemoteUrls() {
        return remoteUrls;
    }

    public void setRemoteUrls(List<String> remoteUrls) {
        this.remoteUrls = remoteUrls;
    }

    public List<String> getRemoteNames() {
        return remoteNames;
    }

    public void setRemoteNames(List<String> remoteNames) {
        this.remoteNames = remoteNames;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String findUrl(String remoteName) {

        int i = 0;
        int index = 0;
        for (String n : remoteNames) {

            if (n.equals(remoteName)) {

                index = i;
            }
            i++;

        }

        System.out.println("remote name: " + remoteNames.get(index));
        System.out.println("remote url: " + remoteUrls.get(index));
        return remoteUrls.get(index);
    }

    public String getSceance() {
        return sceance;
    }

    public void setSceance(String sceance) {
        this.sceance = sceance;
    }

}
