package com.mrjaffesclass.apcs.mvc.template;

import com.mrjaffesclass.apcs.messenger.*;
import java.util.ArrayList;

public class Model implements MessageHandler {

    private final Messenger mvcMessaging;
    private boolean whoseMove;
    private boolean gameOver;
    private ArrayList<String> possibleCords = new ArrayList<>();
    private String[][] board;

    public Model(Messenger messages) {
        mvcMessaging = messages;
        this.board = new String[8][8];
    }

    public void init() {
        this.newGame();
        this.mvcMessaging.subscribe("playerMove", this);
        this.mvcMessaging.subscribe("newGame", this);
    }

    @Override
    public void messageHandler(String messageName, Object messagePayload) {
        if (messagePayload != null) {
            System.out.println("MSG: received by model: " + messageName + " | " + messagePayload.toString());
        } else {
            System.out.println("MSG: received by model: " + messageName + " | No data sent");
        }
        if (messageName.equals("playerMove") && this.gameOver == false) {
            String position = (String) messagePayload;
            Integer row = new Integer(position.substring(0, 1));
            Integer col = new Integer(position.substring(1, 2));
            if (!possibleCords.isEmpty()) {
                if (containsCord(Integer.toString(row * 10 + col))) {
                    placeMove(row, col);
                    this.whoseMove = !this.whoseMove;
                    possibleMoves();
                    boardChange();
                    if (this.whoseMove) {
                        this.mvcMessaging.notify("xMove", this);
                    }
                    if (!this.whoseMove) {
                        this.mvcMessaging.notify("oMove", this);
                    }
                } else {
                    System.out.println(possibleCords);
                    invalidMove();
                }
            } else {
                this.whoseMove = !this.whoseMove;
                possibleMoves();
                if (possibleCords.isEmpty()) {
                    if (getWinner().equals("X")) {
                        xWin();
                    } else {
                        if (getWinner().equals("O")) {
                            oWin();
                        } else {
                            tie();
                        }
                    }
                }
                if (this.whoseMove) {
                    this.mvcMessaging.notify("xMove", this);
                }
                if (!this.whoseMove) {
                    this.mvcMessaging.notify("oMove", this);
                }
            }
            if (getBoardVariables() == 64) {
                if (this.getWinner().equals("O")) {
                    oWin();
                } else if (this.getWinner().equals("X")) {
                    xWin();
                } else if (this.getWinner().equals("tie")) {
                    tie();
                }
            }
        }
        if (messageName.equals("newGame")) {
            newGame();
        }
    }

    public void possibleMoves() {
        possibleCords.clear();
        String currentPlayer = (this.whoseMove) ? "X" : "O", opponent = (!this.whoseMove) ? "X" : "O";
        for (int row = 0; row < this.board.length; row++) {
            for (int col = 0; col < this.board[0].length; col++) {
                if (board[row][col].equals(currentPlayer)) {
                    int upVector = row, downVector = row, finalUpVector = -1, finalDownVector = -1;
                    int leftVector = col, rightVector = col, finalLeftVector = -1, finalRightVector = -1;
                    int DURVector = row, DRUVector = col, finalDURVector = -1, finalDRUVector = -1;
                    int DULVector = row, DLUVector = col, finalDULVector = -1, finalDLUVector = -1;
                    int DBRVector = row, DRBVector = col, finalDBRVector = -1, finalDRBVector = -1;
                    int DBLVector = row, DLBVector = col, finalDBLVector = -1, finalDLBVector = -1;
                    while (!board[upVector][col].equals("") && upVector > 0) {
                        if (board[upVector][col].equals(opponent) && board[upVector - 1][col].equals("")) {
                            finalUpVector = upVector - 1;

                        }
                        upVector--;
                    }
                    if (finalUpVector != -1) {
                        possibleCords.add(Integer.toString(finalUpVector * 10 + col));
                    }
                    while (!board[downVector][col].equals("") && downVector < 7) {
                        if (board[downVector][col].equals(opponent) && board[downVector + 1][col].equals("")) {
                            finalDownVector = downVector + 1;
                        }
                        downVector++;
                    }
                    if (finalDownVector != -1) {
                        possibleCords.add(Integer.toString(finalDownVector * 10 + col));
                    }
                    while (!board[row][leftVector].equals("") && leftVector > 0) {
                        if (board[row][leftVector].equals(opponent) && board[row][leftVector - 1].equals("")) {
                            finalLeftVector = leftVector - 1;

                        }
                        leftVector--;
                    }
                    if (finalLeftVector != -1) {
                        possibleCords.add(Integer.toString(row * 10 + finalLeftVector));
                    }
                    while (!board[row][rightVector].equals("") && rightVector < 7) {
                        if (board[row][rightVector].equals(opponent) && board[row][rightVector + 1].equals("")) {
                            finalRightVector = rightVector + 1;
                        }
                        rightVector++;
                    }
                    if (finalRightVector != -1) {
                        possibleCords.add(Integer.toString(row * 10 + finalRightVector));
                    }
                    while (!board[DURVector][DRUVector].equals("") && DURVector > 0 && DRUVector < 7) {
                        if (board[DURVector][DRUVector].equals(opponent)  && board[DURVector - 1][ DRUVector + 1].equals("")) {
                            finalDURVector = DURVector - 1;
                            finalDRUVector = DRUVector + 1;
                        }
                        DURVector--;
                        DRUVector++;
                    }
                    if (finalDURVector != -1 && finalDRUVector != -1) {
                        possibleCords.add(Integer.toString(finalDURVector * 10 + finalDRUVector));
                    }
                    while (!board[DULVector][DLUVector].equals("") && DULVector > 0 && DLUVector > 0) {
                        if (board[DULVector][DLUVector].equals(opponent)  && board[DULVector - 1][DLUVector - 1].equals("")) {
                            finalDULVector = DULVector - 1;
                            finalDLUVector = DLUVector - 1;
                        }
                        DULVector--;
                        DLUVector--;
                    }
                    if (finalDULVector != -1 && finalDLUVector != -1) {
                        possibleCords.add(Integer.toString(finalDULVector * 10 + finalDLUVector));
                    }
                    while (!board[DBRVector][DRBVector].equals("") && DBRVector < 7 && DRBVector < 7) {
                        if (board[DBRVector][DRBVector].equals(opponent)  && board[DBRVector + 1][DRBVector + 1].equals("")) {
                            finalDBRVector = DBRVector + 1;
                            finalDRBVector = DRBVector + 1;
                        }
                        DBRVector++;
                        DRBVector++;
                    }
                    if (finalDBRVector != -1 && finalDRBVector != -1) {
                        possibleCords.add(Integer.toString(finalDBRVector * 10 + finalDRBVector));
                    }

                    while (!board[DBLVector][DLBVector].equals("") && DBLVector < 7 && DLBVector > 0) {
                        if (board[DBLVector][DLBVector].equals(opponent)  && board[DBLVector + 1][DLBVector - 1].equals("")) {
                            finalDBLVector = DBLVector + 1;
                            finalDLBVector = DLBVector - 1;
                        }
                        DBLVector++;
                        DLBVector--;
                    }
                    if (finalDBLVector != -1 && finalDLBVector != -1) {
                        possibleCords.add(Integer.toString(finalDBLVector * 10 + finalDLBVector));
                    }
                }
            }
        }
    }

    public void placeMove(int row, int col) {
        String currentPlayer = (this.whoseMove) ? "X" : "O";
        board[row][col] = currentPlayer;
    }

    public void xWin() {
        this.mvcMessaging.notify("xWin", this);
        this.gameOver = true;
    }

    public void oWin() {
        this.mvcMessaging.notify("oWin", this);
        this.gameOver = true;
    }

    public void xMove() {
        this.mvcMessaging.notify("xMove", this);
    }

    public void oMove() {
        this.mvcMessaging.notify("oMOVE", this);
    }

    public void tie() {
        this.mvcMessaging.notify("tie", this);
        this.gameOver = true;
    }

    public void boardChange() {
        this.mvcMessaging.notify("boardChange", this.board);
    }

    public void invalidMove() {
        this.mvcMessaging.notify("invalidMove", this);
    }

    private void newGame() {
        for (int row = 0; row < this.board.length; row++) {
            for (int col = 0; col < this.board[0].length; col++) {
                this.board[row][col] = "";
            }
        }
        this.board[3][3] = "O";
        this.board[4][4] = "O";
        this.board[3][4] = "X";
        this.board[4][3] = "X";
        this.whoseMove = false;
        this.gameOver = false;
        possibleMoves();
        boardChange();
    }

    private String getWinner() {
        int xCount = 0;
        int oCount = 0;
        for (int row = 0; row < this.board.length; row++) {
            for (int col = 0; col < this.board[0].length; col++) {
                if (board[row][col].equals("O")) {
                    oCount++;
                } else if (board[row][col].equals("X")) {
                    xCount++;
                }
            }
        }
        return (xCount > oCount) ? "X" : (oCount > xCount) ? "O" : "tie";
    }

    public boolean containsCord(String str) {
        for (String s : possibleCords) {
            if (s.equals(str)) {
                return true;
            }
        }
        return false;
    }
    
    public int getBoardVariables() {
        int count = 0;
        for (String[] s : board) {
            for (String x: s) {
                if (!x.equals("")) {
                    count++;
                }
            }
        }
        return count;
    }
}
