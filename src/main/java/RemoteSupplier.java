
import com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class RemoteSupplier implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

        String remoteName = e.getActionCommand();
        Object o = e.getSource();
       
        System.out.println(o.toString());
        System.out.println( o.hashCode());
        
        Interface.initialisation.setRemoteName(remoteName);
        Interface.initialisation.setRemoteUrl(Interface.initialisation.findUrl(remoteName));
        try {
            Interface.findSourceMenuRemote(o);
        } catch (IOException ex) {
            Logger.getLogger(RemoteSupplier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
