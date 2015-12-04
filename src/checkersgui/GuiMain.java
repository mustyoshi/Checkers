package checkersgui;

import java.awt.Color;
import java.util.ArrayList;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * If kinged during a jump it ends the jump
 *
 * @author Bradley
 */
class Successor {

    public String state;
    public int level;
    public double value;
    public int chose;
    public ArrayList<Successor> successors;
    public boolean isJump;
    public boolean side;
    public boolean isKing;

    Successor(int depth, int maxDepth, String initState, boolean side) {
        this.side = side;
        isJump = false;
        isKing = false;
        successors = new ArrayList<Successor>();
        //System.out.println("Checking at depth  " + depth + " against " + initState);
        value = -0.0;

        state = initState;

        value = GuiMain.Evaluate(state, side);
        if (depth > maxDepth) {
            return;
        }
        //System.out.println("My value at depth " + depth + " is " + value);

        ArrayList<String> sucStates = GuiMain.GetSuccessors(state, side);

        for (String s : sucStates) {
            if (s.startsWith("J:")) {
                isJump = true;
                break;
            }
        }
        ArrayList<String> sucStates2 = new ArrayList<String>();
        if (isJump) {
            for (String s : sucStates) {
                if (s.startsWith("J:")) {
                    sucStates2.add(s);
                }
            }
            sucStates = sucStates2;

        }
        if(state.contains("K")){
            isKing = true;
        }
        double bestOpt = (side) ? -99999 : 99999;
        for (String s : sucStates) {
            Successor toAdd = new Successor(depth + 1, maxDepth, s.substring(s.indexOf(";") + 1), (!isKing && isJump) ? side : !side);
            //Check for successive jumps.
            if (!isKing && (isJump && toAdd.isJump)) {

                for (Successor t : toAdd.successors) {
                    successors.add(t);
                    if (side) {
                        bestOpt = Math.max(bestOpt, t.value);
                    } else {
                        bestOpt = Math.min(bestOpt, t.value);
                    }
                }
            } else {

                successors.add(toAdd);
            }
            //System.out.println("Mini maxing " + depth);
            //Check for successive jumps.

            if (side) {
                bestOpt = Math.max(bestOpt, toAdd.value);
            } else {
                bestOpt = Math.min(bestOpt, toAdd.value);

            }
        }
        if (depth ==0) {
            System.out.println("My best successor is " + bestOpt);
        }

        value = bestOpt;
        // System.out.println("My value at depth " + depth + " is now " + value);

    }

    Successor getState(String state) {
        if (this.state == state) {
            return this;
        }
        for (Successor s : this.successors) {
            if (s.state == state) {
                return s;
            }

        }
        for (Successor s : this.successors) {
            if (s.getState(state) != null) {
                return s.getState(state);
            }
        }
        return null;
    }
}

public class GuiMain extends javax.swing.JFrame {

    /**
     * Creates new form GuiMain
     */
    private int st = 0;
    private Successor currentSuc;
    private int look = 4;

    public GuiMain() {
        initComponents();
        initCheckerBoard();
        this.stateBox.setText(checkerBoard1.state);

        checkerBoard1.setState(checkerBoard1.state, true);
        //System.out.println(getMove(6, 7, 7, 5, false));
        //checkerBoard1.setState("ff78000000008bff0000000000000000", true);
        //System.out.println("Current State is " + (Evaluate(state, false)));
        //ArrayList<String> sucs = GetSuccessors(state, true);

    }

    public static String SaveState(char[][] eboard) {

        int topSlot = 0;
        int botSlot = 0;
        int topK = 0;
        int botK = 0;
        int p = 0;
        int t1 = 0;
        int t2 = 0;
        for (int y = 0; y < 8; y++) {

            for (int x = (y + 1) % 2; x < 8; x = x + 2) {
                //We only care about the black cells
                if (eboard[x][y] > 0) {

                    switch (eboard[x][y]) {
                        case 1:
                            topSlot += (1 << p);
                            t1++;
                            break;
                        case 2:
                            topK += 1 << p;
                            break;
                        case 3:
                            botSlot += 1 << p;
                            t2++;
                            break;
                        case 4:
                            botK += 1 << p;

                    }
                }
                p++;
            }

        }
        //System.out.println("Top has " + t1 + " and bot has " + t2);
        return String.format("%08x%08x%08x%08x", topSlot, botSlot, topK, botK);
    }

    public static String getMove(int x1, int y1, int x2, int y2, boolean side) {

        if (side) {
            return String.format("%d->%d:%s", (4 * y1) + (x1 / 2) + (y1 % 2), (4 * y2) + (x2 / 2) + (y2 % 2), y2 == 0 ? "K" : "");
        } else {
            return String.format("%d->%d:%s", (4 * (7 - y1)) + ((x1 - 7) / 2) + (y1 % 2), (4 * (7 - y2)) + ((x2 - 7) / 2) + (y2 % 2), y2 == 7 ? "K" : "");
        }
    }

    public static ArrayList<String> GetSuccessors(String state, boolean side) {
        // side = !side;
        //True for bottom
        //False for top
        ArrayList<String> arr = new ArrayList<String>();
        long topRow = Long.parseLong(state.substring(0, 8), 16);
        long botRow = Long.parseLong(state.substring(8, 16), 16);
        long topKing = Long.parseLong(state.substring(16, 24), 16);
        long botKing = Long.parseLong(state.substring(24, 32), 16);
        char[][] eboard = new char[8][8];
        int p = 0;

        for (int y = 0; y < 8; y++) {

            for (int x = (y + 1) % 2; x < 8; x = x + 2) {
                eboard[x][y] = 0;

                if (((topRow >> p) & 1) == 1) {
                    eboard[x][y] = 1;
                }
                if (((topKing >> p) & 1) == 1) {
                    eboard[x][y] = 2;
                }
                if (((botRow >> p) & 1) == 1) {
                    eboard[x][y] = 3;
                }
                if (((botKing >> p) & 1) == 1) {
                    eboard[x][y] = 4;
                }
                p++;
            }

        }
        int piece = (side) ? 1 : 3;
        int ydirS = (side) ? -1 : 1;
        int kingSpot = (side) ? 0 : 7;
        // System.out.println("Our kingside is " + kingSpot);
        boolean isKing = false;
        for (int y = 0; y < 8; y++) {

            for (int x = (y + 1) % 2; x < 8; x = x + 2) {
                if ((eboard[x][y] == piece || eboard[x][y] == piece + 1)) {
                    isKing = eboard[x][y] == piece + 1;
                    //Have a piece
                    //TODO:
                    //Add support for detecting jumps (and multiple jumps)
                    for (int ydir = -1; ydir < 2; ydir += 2) {
                        if (eboard[x][y] == piece && ydir != ydirS) {
                            continue;
                        }
                        if (y + ydir >= 0 && y + ydir <= 7) {
                            for (int xdir = -1; xdir < 2; xdir += 2) {
                                if (x + xdir >= 0 && x + xdir <= 7) { //Within boundsdd
                                    if (eboard[x + xdir][y + ydir] == 0) {
                                        //It can go forward to the left
                                        char save = eboard[x][y];
                                        eboard[x + xdir][y + ydir] = (char) ((isKing || y + ydir == kingSpot) ? piece + 1 : piece); //It gets converted to queen at y=0
                                        eboard[x][y] = 0;
                                        arr.add(getMove(x, y, x + xdir, y + ydir, side) + ";" + SaveState(eboard));
                                        eboard[x + xdir][y + ydir] = 0;
                                        eboard[x][y] = save;
                                    } else if ((x + xdir + xdir >= 0 && x + xdir + xdir <= 7) && (y + ydir + ydir >= 0 && y + ydir + ydir <= 7)
                                            && eboard[x + xdir][y + ydir] != piece && eboard[x + xdir][y + ydir] != piece + 1 && eboard[x + xdir + xdir][y + ydir + ydir] == 0) {
                                        //System.out.println("This is a jump");
                                        //This means the spot is held by an opposing piece.
                                        //I think for the sake of easyness... We'll check for successive jumps during the next phase.

                                        char save = eboard[x + xdir][y + ydir]; //Get piece we just killed
                                        char save1 = eboard[x][y]; //Get our own piece

                                        //Set our piece over the jump
                                        eboard[x + xdir + xdir][y + ydir + ydir] = (char) ((isKing || y + ydir + ydir == kingSpot) ? piece + 1 : piece);

                                        //We moved so zero our spot
                                        eboard[x][y] = 0;

                                        //We killed the other piece so remove it.
                                        eboard[x + xdir][y + ydir] = 0;
                                        arr.add("J:" + getMove(x, y, x + xdir, y + ydir, side) + ";" + SaveState(eboard));
                                        eboard[x + xdir][y + ydir] = save;
                                        eboard[x][y] = save1;
                                        eboard[x + xdir + xdir][y + ydir + ydir] = 0;
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        //System.out.println("Returning successors");
        return arr;
    }

    private void initCheckerBoard() {

        checkerBoard1.initBoard();
        currentSuc = new Successor(0, look, checkerBoard1.state, true);
    }

    public static float Evaluate(String state, boolean side) {
        int s = side ? 1 : -1;
        //This is our evaluator
        //Since we are only allowed to go like 6 deep, there's no need
        //To do lots of "hacky" things to conserve memory.
        char[][] eboard = new char[8][8];
        //System.out.println("Evaluating " + state);
        long topRow = Long.parseLong(state.substring(0, 8), 16);
        long botRow = Long.parseLong(state.substring(8, 16), 16);
        long topKing = Long.parseLong(state.substring(16, 24), 16);
        long botKing = Long.parseLong(state.substring(24, 32), 16);
        //Things we should try:
        //Pieces are weighted based on how close they are to the goal
        float goalValue = 0;

        int p = 0;
        for (int y = 0; y < 8; y++) {

            for (int x = (y + 1) % 2; x < 8; x = x + 2) {
                //eboard[x][y] = 0;

                /*Here are some ideas for the evaluator, but I'm unsure how to implement completely correctly with your code.
                 * They are not all necessarily good, but they are definite possibilities for implementation.
                 * 
                 * Pseudo code for evaluator: 
                 * 
                 * if no enemy pieces remain 
                 * 		goalValue increase (win state for game, should always be the move made)
                 * 
                 * if piece moving into jumpspace
                 * 		goalValue decrease (Avoid moving to be captured)
                 * 
                 * if piece moving to side of board
                 * 		goalValue decrease (Avoid moving to side of board since it lowers ability to move)
                 * 
                 * if non King piece
                 * 		goalValue increase (We would rather create more kings than move our current kings)
                 * 
                 * if pyramid is built (One piece in front, two diagonally behind)
                 * 		goalValue increase (Pyramids block players from capturing our pieces)
                 * 
                 * if backrow piece
                 * 		goalValue decrease (Back row shouldn't be moved for as long as possible)
                 * 
                 * if moving into center
                 *		goalValue increase (Center of the board is important territory to control for winning) 		
                 */
                
                if (((topRow >> p) & 1) == 1) {
                    //eboard[x][y] = 1;
                    //System.out.println("y:" + y + ", " + (8.0 - (y + 1)) / (8.0));
                    goalValue += 1.0 * ((1.0 + (8.0 - (y + 1)) / (8.0))) * s;
                }
                if (((topKing >> p) & 1) == 1) {
                    goalValue += 4.0 * (1.0 + (y) / 8.0) * s;
                }
                if (((botRow >> p) & 1) == 1) {
                    //eboard[x][y] = 3;
                    //System.out.println("y:" + y + ", " + (y) / 8.0);
                    goalValue += 1.0 * (1.0 + (y) / 8.0) * -s;
                }
                if (((botKing >> p) & 1) == 1) {
                    goalValue += 4.0 * ((1.0 + (8.0 - (y + 1)) / (8.0))) * -s;
                }

                p++;
            }

        }
        if (side && (botRow == 0 && botKing == 0)) {
            System.out.println("Detected move where they have no pieces");
            goalValue = 9999; //If no enemy pieces remain, take that move
        } else if (!side && (topRow == 0 && topKing == 0)) {
            System.out.println("Detected move where we have no pieces");
            goalValue = -9999; //If none of our pieces remain, do not let us take
        }
        return goalValue;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        checkerBoard1 = new checkersgui.CheckerBoard();
        jScrollPane1 = new javax.swing.JScrollPane();
        stateBox = new javax.swing.JTextArea();
        autoMove = new javax.swing.JCheckBox();
        compMove = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        compCon = new javax.swing.JLabel();
        turnInd = new javax.swing.JLabel();
        stateLoad = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        moveHistory = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(600, 400));

        checkerBoard1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        checkerBoard1.setPreferredSize(new java.awt.Dimension(320, 320));
        checkerBoard1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkerBoard1MouseClicked(evt);
            }
        });
        checkerBoard1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                checkerBoard1PropertyChange(evt);
            }
        });

        javax.swing.GroupLayout checkerBoard1Layout = new javax.swing.GroupLayout(checkerBoard1);
        checkerBoard1.setLayout(checkerBoard1Layout);
        checkerBoard1Layout.setHorizontalGroup(
            checkerBoard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 316, Short.MAX_VALUE)
        );
        checkerBoard1Layout.setVerticalGroup(
            checkerBoard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 316, Short.MAX_VALUE)
        );

        stateBox.setColumns(20);
        stateBox.setLineWrap(true);
        stateBox.setRows(5);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkerBoard1, org.jdesktop.beansbinding.ELProperty.create("${toolTipText}"), stateBox, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkerBoard1, org.jdesktop.beansbinding.ELProperty.create("${toolTipText}"), stateBox, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        stateBox.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                stateBoxPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(stateBox);

        autoMove.setText("Comp Auto Move");
        autoMove.setEnabled(false);

        compMove.setText("Comp Move");
        compMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compMoveActionPerformed(evt);
            }
        });

        jLabel1.setText("Confidence:");

        compCon.setBackground(new java.awt.Color(204, 204, 0));
        compCon.setText("Turns:");
        compCon.setOpaque(true);

        turnInd.setText("Player");

        stateLoad.setText("Load State");
        stateLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stateLoadActionPerformed(evt);
            }
        });

        moveHistory.setColumns(20);
        moveHistory.setRows(5);
        jScrollPane2.setViewportView(moveHistory);

        jButton1.setText("Undo");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(checkerBoard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(compCon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(turnInd))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(autoMove)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(stateLoad)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(compMove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(checkerBoard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(autoMove)
                            .addComponent(compMove))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(stateLoad))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(compCon)
                            .addComponent(turnInd))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(185, 185, 185)
                                .addComponent(jButton1))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void checkerBoard1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkerBoard1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_checkerBoard1MouseClicked

    private void stateBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_stateBoxPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_stateBoxPropertyChange

    private void checkerBoard1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_checkerBoard1PropertyChange
        if (evt.getPropertyName() == "ToolTipText" && this.st != checkerBoard1.st) {
            this.st = checkerBoard1.st;
            stateBox.setText(checkerBoard1.state);
            moveHistory.setText(moveHistory.getText() + "\n" + checkerBoard1.lastMove);
            //moveHistory.setText(moveHistory.getText()+ '\n' + evt.getNewValue().toString().split(":")[1] );
            //A move was made.
            turnInd.setText(checkerBoard1.getPlayerTurn() ? "Player" : "Comp");
            System.out.println("Current State is " + (Evaluate(stateBox.getText(), true)));
            jLabel1.setText("C:" + (Evaluate(stateBox.getText(), true)));
            if (currentSuc != null) {
                System.out.println("Checking states");
                //currentSuc = new Successor(0, look, checkerBoard1.state, true);
            }
            if (autoMove.isSelected()) {
                // MakeComputerMove();
            }
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_checkerBoard1PropertyChange

    private void stateLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stateLoadActionPerformed
        // TODO add your handling code here:
        checkerBoard1.setState(stateBox.getText(), true);
    }//GEN-LAST:event_stateLoadActionPerformed

    private void compMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compMoveActionPerformed
        // TODO add your handling code here:

        /*ArrayList<String> successors = GetSuccessors(checkerBoard1.state, true);
         double maxGoal = -999999999.0;
         String nextState = "";
         System.out.println(successors.size() + " possible next moves");
         for (String s : successors) {
         double eval = Evaluate(s, true);
         System.out.println(s + " = " + eval);
         if (eval >= maxGoal) {
         maxGoal = eval;
         nextState = s;
         }
         }
         System.out.println("Chose " + nextState);*/
        Successor tree;
        if (currentSuc == null) {
            tree = new Successor(0, look, checkerBoard1.state, true);
        } else {
            //currentSuc = currentSuc.getState(this.checkerBoard1.state);
            //currentSuc.Extend(0, look);
            tree = new Successor(0, look, checkerBoard1.state, true);
        }

        double w = -9999999;
        String nextState = "";
        //while (true) {
        for (Successor s : tree.successors) {
            System.out.println("Choice: " + s.value);
            if (s.value > w) {
                nextState = s.state;
                w = s.value;
                currentSuc = s;
            }
        }

        //}
        System.out.println("Picked " + w + ": " + nextState);

        checkerBoard1.setState(nextState, true);
        stateBox.setText(nextState);
        jLabel1.setText("C:" + (Evaluate(nextState, true)));
       
        //checkerBoard1.setState(nextState, true);
        this.invalidate();
        this.repaint();
    }//GEN-LAST:event_compMoveActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        checkerBoard1.pastStates.remove(checkerBoard1.pastStates.size() - 1);
        String lastState = checkerBoard1.pastStates.get(checkerBoard1.pastStates.size() - 1);

        checkerBoard1.pastStates.remove(checkerBoard1.pastStates.size() - 1);
        checkerBoard1.setState(lastState, true);
        stateBox.setText(lastState);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GuiMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GuiMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GuiMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GuiMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GuiMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoMove;
    private checkersgui.CheckerBoard checkerBoard1;
    private javax.swing.JLabel compCon;
    private javax.swing.JButton compMove;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea moveHistory;
    private javax.swing.JTextArea stateBox;
    private javax.swing.JButton stateLoad;
    private javax.swing.JLabel turnInd;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
