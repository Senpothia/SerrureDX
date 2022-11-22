/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class RemoteController {
    
    private String token;
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public boolean connexionRequest(Login login) throws MalformedURLException, IOException {
        
        URL url = new URL(Interface.initialisation.getRemoteUrl() + "/connexion/apps");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        //  Login login = new Login("michel@gmail.com", "michel");
        String reponseServeur = null;
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(login);
        System.out.println("Conversion Json = " + json);
        
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        String responseLine = null;   // ligne déplacée
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            // String responseLine = null;   // ligne d'origine
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            reponseServeur = response.toString();
            
        }
        
        System.out.println("RemoteController.connexionRequest()");
        System.out.println(reponseServeur);
        
        if (reponseServeur.equals("Authorised")) {
            return true;
        } else {
            return false;
        }
        // System.out.println(responseLine);

    }
    
    public boolean enregistrerSceance(FormSeance sceance, Login login) throws MalformedURLException, IOException {
        
        boolean autorisation = connexionRequest(login);
        if (autorisation) {
            
            System.out.println("RemoteController.enregistrerSceance()");
            //   URL url = new URL("http://127.0.0.1:8090/creer/sceance/windows");
            URL url = new URL(Interface.initialisation.getRemoteUrl() + "/creer/sceance/windows");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(sceance);
            System.out.println("Conversion Json = " + json);
            
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("réponse: " + response.toString());
                Interface.initialisation.setIdSceance(response.toString());
                return true;
                
            }
        } else {
            
            return false;
            
        }
        
    }
    
    public boolean sauvegarderSequence(FormSeance sceance, Login login) throws MalformedURLException, IOException {
        
        boolean autorisation = connexionRequest(login);
        if (autorisation) {
            
            System.out.println("RemoteController.enregistrerSceance()");
            //   URL url = new URL("http://127.0.0.1:8090/creer/sceance/windows");
            URL url = new URL(Interface.initialisation.getRemoteUrl() + "/enregistrer/sequence/windows/" + Interface.initialisation.getIdSceance());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(sceance);
            System.out.println("Conversion Json = " + json);
            
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("réponse: " + response.toString());
                return true;
                
            }
        } else {
            
            return false;
            
        }
        
    }
    
}
