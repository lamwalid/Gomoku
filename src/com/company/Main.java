package com.company;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseEvent;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private static final int
            EMPTY = 0,
            BLACK = 1,
            WHITE = 2;
    private GoMokuBoard board;
    private Button newGameButton;
    private Button resignButton;
    private Label message;


    public void start(Stage stage){

        message = new Label("Click \"New Game\" to begin.");
        message.setTextFill( Color.rgb(100,255,100) );
        message.setFont( Font.font(null, FontWeight.BOLD, 18) );


        newGameButton = new Button("New Game");
        resignButton = new Button("Resign");

        board = new GoMokuBoard();
        board.drawBoard();

        newGameButton.setOnAction( e -> board.doNewGame() );
        resignButton.setOnAction( e -> board.doResign() );
        board.setOnMousePressed( e -> board.mousePressed(e) );

        board.relocate(20,20);
        newGameButton.relocate(370, 120);
        resignButton.relocate(370, 200);
        message.relocate(20, 370);

        resignButton.setManaged(false);
        resignButton.resize(100,30);
        newGameButton.setManaged(false);
        newGameButton.resize(100,30);

        Pane root = new Pane();

        root.setPrefWidth(500);
        root.setPrefHeight(420);

        root.getChildren().addAll(board, newGameButton, resignButton, message);
        root.setStyle("-fx-background-color: darkgreen; "  + "-fx-border-color: darkred; -fx-border-width:3");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Go Moku!");
        stage.show();

    }

    private class GoMokuBoard extends Canvas {

        int[][] boardData;

        boolean gameInProgress;

        int currentPlayer;

        int win_r1, win_c1, win_r2, win_c2;

        GoMokuBoard() {
            super(314, 314);
            doNewGame();
        }

        void doNewGame() {
            if (gameInProgress == true) {

                message.setText("Finish the current game first!");
                return;
            }
            boardData = new int[13][13];

            currentPlayer = BLACK;
            message.setText("Black:  Make your move.");
            gameInProgress = true;
            newGameButton.setDisable(true);
            resignButton.setDisable(false);
            drawBoard();
        }

        void doResign() {
            if (gameInProgress == false) {
                message.setText("There is no game in progress!");
                return;
            }
            if (currentPlayer == WHITE)
                gameOver("WHITE resigns.  BLACK wins.");
            else
                gameOver("BLACK resigns.  WHITE wins.");
        }


        void gameOver(String str) {
            message.setText(str);
            newGameButton.setDisable(false);
            resignButton.setDisable(true);
            gameInProgress = false;
        }

        void doClickSquare(int row, int col) {

            if (boardData[row][col] != EMPTY) {
                if (currentPlayer == BLACK)
                    message.setText("BLACK:  Please click an empty square.");
                else
                    message.setText("WHITE:  Please click an empty square.");
                return;
            }



            boardData[row][col] = currentPlayer;
            drawBoard();

            if (winner(row, col)) {
                if (currentPlayer == WHITE)
                    gameOver("WHITE wins the game!");
                else
                    gameOver("BLACK wins the game!");
                drawWinLine();
                return;
            }

            boolean emptySpace = false;
            for (int i = 0; i < 13; i++)
                for (int j = 0; j < 13; j++)
                    if (boardData[i][j] == EMPTY)
                        emptySpace = true;
            if (emptySpace == false) {
                gameOver("The game ends in a draw.");
                return;
            }



            if (currentPlayer == BLACK) {
                currentPlayer = WHITE;
                message.setText("WHITE:  Make your move.");
            } else {
                currentPlayer = BLACK;
                message.setText("BLACK:  Make your move.");
            }

        }

        private boolean winner(int row, int col) {

            if (count(boardData[row][col], row, col, 1, 0) >= 5)
                return true;
            if (count(boardData[row][col], row, col, 0, 1) >= 5)
                return true;
            if (count(boardData[row][col], row, col, 1, -1) >= 5)
                return true;
            if (count(boardData[row][col], row, col, 1, 1) >= 5)
                return true;



            return false;

        }

        private int count(int player, int row, int col, int dirX, int dirY) {

            int ct = 1;

            int r, c;

            r = row + dirX;
            c = col + dirY;
            while (r >= 0 && r < 13 && c >= 0 && c < 13 && boardData[r][c] == player) {

                ct++;
                r += dirX;
                c += dirY;
            }

            win_r1 = r - dirX;
            win_c1 = c - dirY;


            r = row - dirX;
            c = col - dirY;
            while (r >= 0 && r < 13 && c >= 0 && c < 13 && boardData[r][c] == player) {

                ct++;
                r -= dirX;
                c -= dirY;
            }

            win_r2 = r + dirX;
            win_c2 = c + dirY;



            return ct;

        }

        public void drawBoard() {

            GraphicsContext g = getGraphicsContext2D();
            g.setFill(Color.LIGHTGRAY);
            g.fillRect(0, 0, 314, 314);



            g.setStroke(Color.BLACK);
            g.setLineWidth(2);
            for (int i = 0; i <= 13; i++) {
                g.strokeLine(1 + 24 * i, 0, 1 + 24 * i, 314);
                g.strokeLine(0, 1 + 24 * i, 314, 1 + 24 * i);
            }



            for (int row = 0; row < 13; row++)
                for (int col = 0; col < 13; col++)
                    if (boardData[row][col] != EMPTY)
                        drawPiece(g, boardData[row][col], row, col);

        }


        private void drawPiece(GraphicsContext g, int piece, int row, int col) {
            if (piece == WHITE) {
                g.setFill(Color.WHITE);
                g.fillOval(4 + 24 * col, 4 + 24 * row, 18, 18);
                g.setStroke(Color.BLACK);
                g.setLineWidth(1);
                g.strokeOval(4 + 24 * col, 4 + 24 * row, 18, 18);
            } else {
                g.setFill(Color.BLACK);
                g.fillOval(4 + 24 * col, 4 + 24 * row, 18, 18);
            }
        }

        private void drawWinLine() {
            GraphicsContext g = getGraphicsContext2D();
            g.setStroke(Color.RED);
            g.setLineWidth(5);
            g.strokeLine(13 + 24 * win_c1, 13 + 24 * win_r1, 13 + 24 * win_c2, 13 + 24 * win_r2);
        }


        public void mousePressed(MouseEvent evt) {
            if (gameInProgress == false)
                message.setText("Click \"New Game\" to start a new game.");
            else {
                int col = (int) ((evt.getX() - 2) / 24);
                int row = (int) ((evt.getY() - 2) / 24);
                if (col >= 0 && col < 13 && row >= 0 && row < 13)
                    doClickSquare(row, col);
            }
        }

    }
}
