
import com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    }

}
