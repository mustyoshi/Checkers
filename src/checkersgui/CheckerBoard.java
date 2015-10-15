/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkersgui;

import java.awt.Color;
import java.util.ArrayList;

public class CheckerBoard extends javax.swing.JPanel {

    /**
     * Creates new form CheckerBoard
     */
    private int cX;
    private int cY;
    private javax.swing.JPanel[][] boards;
    private javax.swing.JLabel activePiece;
    public Color topColor;
    public Color botColor;
    private Color tKing;
    private Color bKing;
    boolean topTurn;
    private ArrayList<javax.swing.JLabel> topPieces;
    private int tp;
    private ArrayList<javax.swing.JLabel> botPieces;
    private int bp;
    public String state;
    public String lastMove;
    public int st;
    public int lastCell;
    private int lbx;
    private int lby;
    public ArrayList<String> pastStates;

    public CheckerBoard() {

        initComponents();
        topColor = Color.red;
        botColor = Color.white;
        tKing = Color.white;
        bKing = Color.pink;
        topTurn = true;
        state = "Test";
        lastMove = "";
        //saveState();
        topPieces = new ArrayList<javax.swing.JLabel>();
        botPieces = new ArrayList<javax.swing.JLabel>();
        pastStates = new ArrayList<String>();
        st = 0;
    }

    public String getState() {
        return state;
    }

    public boolean getPlayerTurn() {
        return topTurn;
    }

    private void saveState() {
        //Saves each piece as a single bit in an int
        //Kings are saved in a seperate int.
        //

        int topSlot = 0;
        int botSlot = 0;
        int topK = 0;
        int botK = 0;
        int p = 0;
        for (int y = 0; y < 8; y++) {

            for (int x = (y + 1) % 2; x < 8; x = x + 2) {
                //We only care about the black cells
                if (boards[x][y].getComponentCount() > 0) {
                    Color pC = boards[x][y].getComponent(0).getBackground();
                    javax.swing.JLabel jl = (javax.swing.JLabel) boards[x][y].getComponent(0);
                    if (pC == topColor) {
                        if (jl.getText().equals("")) {
                            topSlot += (1 << p);
                        } else {
                            topK += 1 << p;
                        }
                    } else if (pC == botColor) {
                        if (jl.getText().equals("")) {
                            botSlot += (1 << p);
                        } else {
                            botK += 1 << p;
                        }
                    }
                }
                p++;
            }

        }

        state = String.format("%08x%08x%08x%08x", topSlot, botSlot, topK, botK);
        //I'm not really sure how to send this to the parent container 
        //this is the best way I can think of so far.
        //The parent container listens for property changes and checks the
        //tooltip
        this.setToolTipText(state + ":" + lastMove);
    }

    public void setState(String state, boolean pTurn) {
        this.topTurn = pTurn;
        topPieces.clear();
        botPieces.clear();
        //topRow is the player that starts on the top
        //botRow is the player that starts on the bottom.
        System.out.println("Setting state from " + state.length());
        pastStates.add(state);
        long topRow = Long.parseLong(state.substring(0, 8), 16);
        long botRow = Long.parseLong(state.substring(8, 16), 16);
        long topKing = Long.parseLong(state.substring(16, 24), 16);
        long botKing = Long.parseLong(state.substring(24, 32), 16);
        int p = 0;
        for (int y = 0; y < 8; y++) {

            for (int x = (y + 1) % 2; x < 8; x = x + 2) {
                boards[x][y].removeAll();

                if (((topRow >> p) & 1) == 1) {

                    javax.swing.JLabel peace = new javax.swing.JLabel();
                    peace.setOpaque(true);
                    peace.setBackground(topColor);

                    peace.setBounds(cX / 4, cY / 4, cX / 2, cY / 2);
                    boards[x][y].add(peace);
                    topPieces.add(peace);
                }
                if (((topKing >> p) & 1) == 1) {

                    javax.swing.JLabel peace = new javax.swing.JLabel();
                    peace.setOpaque(true);
                    peace.setText("K");
                    peace.setBackground(topColor);
                    peace.setBounds(cX / 4, cY / 4, cX / 2, cY / 2);
                    boards[x][y].add(peace);
                    topPieces.add(peace);
                }
                if (((botRow >> p) & 1) == 1) {
                    javax.swing.JLabel peace = new javax.swing.JLabel();
                    peace.setOpaque(true);
                    peace.setBackground(botColor);
                    peace.setBounds(cX / 4, cY / 4, cX / 2, cY / 2);
                    boards[x][y].add(peace);
                    botPieces.add(peace);
                }
                if (((botKing >> p) & 1) == 1) {
                    javax.swing.JLabel peace = new javax.swing.JLabel();
                    peace.setOpaque(true);
                    peace.setText("K");
                    peace.setBackground(botColor);
                    peace.setBounds(cX / 4, cY / 4, cX / 2, cY / 2);
                    boards[x][y].add(peace);
                    botPieces.add(peace);
                }
                p++;
            }

        }
        System.out.println("Set state");
        this.invalidate();
        this.repaint();
    }

    public void initBoard() {
        boards = new javax.swing.JPanel[8][8];
        topPieces.clear();
        botPieces.clear();
        tp = bp = 0;
        cY = this.getHeight() / 8;
        cX = this.getWidth() / 8;
        System.out.println(this.getHeight());
        boolean p = true;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {

                final javax.swing.JPanel space = new javax.swing.JPanel();

                if (p) {
                    space.setBackground(Color.white);
                } else {
                    space.setBackground(Color.black);
                }
                p = !p;

                space.setBounds(cX * x, cY * y, cX, cY);

                if (y < 3 && (y + x) % 2 == 1) { //Black

                    javax.swing.JLabel peace = new javax.swing.JLabel();
                    peace.setOpaque(true);
                    peace.setBackground(botColor);
                    peace.setBounds(cX / 4, cY / 4, cX / 2, cY / 2);
                    space.add(peace);
                    botPieces.add(peace);
                } else if (y > 4 && (y + x) % 2 == 1) {
                    javax.swing.JLabel peace = new javax.swing.JLabel();
                    peace.setOpaque(true);
                    peace.setBackground(topColor);
                    peace.setBounds(cX / 4, cY / 4, cX / 2, cY / 2);
                    space.add(peace);

                    topPieces.add(peace);

                }
                this.add(space);
                boards[x][y] = space;
                space.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        onClicked(evt, space);
                    }
                });
            }
            p = !p;
        }
        System.out.println("Saved state");
        saveState();
        this.invalidate();
        this.repaint();
    }

    private void compTurn() {

    }

    private void onClicked(java.awt.event.MouseEvent evt, javax.swing.JPanel spc) {

        System.out.println(spc.getComponentCount());

        if (spc.getComponentCount() > 0 && activePiece == null) {
            System.out.println("Got piece of " + spc.getComponentCount());
            lastMove = "";
            int xCell = (spc.getX() / cX) / 2;

            int yCell = spc.getY() / cY;
            int cell = (1 + xCell + (4 * yCell));
            lastMove = "T:" + cell + " -> ";
            activePiece = (javax.swing.JLabel) spc.getComponent(0);
            if (activePiece.getBackground() == botColor) {
                activePiece.getParent().remove(activePiece);
            } else {
                activePiece = null;
            }
            lbx = (spc.getX() / cX);
            lby = (spc.getY() / cY);
            System.out.println("Clicked on " + lbx + "," + lby);
        } else if (spc.getComponentCount() == 0 && activePiece != null && spc.getBackground() == Color.black) {
            int xCell = (spc.getX() / cX) / 2;

            int yCell = spc.getY() / cY;
            int cell = (1 + xCell + (4 * yCell));
            lastMove += cell;
            lastCell = cell;
            spc.add(activePiece);
            if(yCell == 7)
                System.out.println("King me");
            activePiece = null;
            topTurn = false;
            //compTurn();
            this.invalidate();
            this.repaint();
            st += 1;
            saveState();

        } else if (spc.getComponentCount() > 0 && activePiece != null && spc.getBackground() == Color.black) {
            javax.swing.JLabel deadPiece = (javax.swing.JLabel) spc.getComponent(0);
            //System.out.println("Jumping from " + lbx +"," + lby);
            int cx = (spc.getX() / cX);
            int cy = (spc.getY() / cY);
            System.out.println("Clicked on " + cx + "," + cy);
            int dx = (spc.getX() / cX) - lbx;
            int dy = (spc.getY() / cY) - lby;
            int tx = lbx + dx + dx;
            int ty = lby + dy + dy;
            if (tx >= 0 && tx <= 7 && ty >= 0 && ty <= 7) {
                System.out.println("Valid jump");
                spc.removeAll();

                int xCell = (tx) / 2;

                int yCell = ty / cY;

                int cell = (1 + xCell + (4 * yCell));
                System.out.println("Moving from cell " + lastCell);

                lastMove += cell;
                System.out.println(tx + "," + ty);
                spc = boards[tx][ty];
                spc.add(activePiece);

                activePiece = null;
                topTurn = false;
                //compTurn();
                this.invalidate();
                this.repaint();
                st += 1;
                saveState();
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setToolTipText("");
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                formPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_formPropertyChange


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
