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
        System.out.println(test.substring(0,test.indexOf("->")));
        long topSlot = 10;
        long botSlot = 1;
        long topK = 2;
        long botK = 3345455;
         String ts = Long.toHexString(topSlot);
        int  sz = ts.length();
        for (int i = 0; i < 8 - sz; i++) {
            ts = ts + "0";// + ts;
        }
        System.out.println(ts) ;
        // TODO code application logic here
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GuiMain().setVisible(true);
            }
        });
    }

}
