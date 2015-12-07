/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkersgui;

/**
 *
 * @author Bradley
 */
public class CheckersGUI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String test = "12->16:";
        System.out.println(test.substring(test.indexOf("->")+2,test.indexOf(":")));
        
        // TODO code application logic here
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GuiMain().setVisible(true);
            }
        });
    }

}
