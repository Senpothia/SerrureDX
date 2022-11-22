
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
    private List<String> remoteUrls;
    private List<String> remoteNames;
    private String remoteName;
    private String remoteUrl;
    private String idSceance;

    public Initialisation() {
    }

    public Initialisation(String username, String password, List<String> remoteUrls, List<String> remoteNames) {
        this.username = username;
        this.password = password;
        this.remoteUrls = remoteUrls;
        this.remoteNames = remoteNames;
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

    public String getIdSceance() {
        return idSceance;
    }

    public void setIdSceance(String idSceance) {
        this.idSceance = idSceance;
    }

}
