

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
public class RemoteSupplier implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent e) {
              
         RemoteController.remoteUrl = e.getActionCommand();
    }
    
}
