
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
public class PortSupplier implements ActionListener  {
    
    

    @Override
    public void actionPerformed(ActionEvent e) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
          //System.out.println("Port sélectionné: " + e.getActionCommand());
          Interface.portName = e.getActionCommand();
    }
    
}
