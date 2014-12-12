/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package reversi_gui;

/**
 *
 * @author Class2015
 */
public class Reversi_gui {

    /**
     * @param args the command line arguments
     */
    public static Reversi_board board;
    public static Ai ai;
    public static void main(String[] args) {
       
        board = new Reversi_board();
        board.setVisible(true);
        ai = new Ai();
        ai.set_location_value();
       //ai.start_game();
    }
    
}
