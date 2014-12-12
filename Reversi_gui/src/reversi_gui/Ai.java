/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package reversi_gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.MathContext;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import static reversi_gui.Reversi_gui.ai;
import static reversi_gui.Reversi_gui.board;

/**
 *
 * @author Class2015
 * 
 * 0: empty
 * 1: w
 * 2:B
 */
public class Ai {
    public Thread send;
    public Thread receive;
    //public char [][] board_from_server = new char[8][8];
    public int[][] locationValues;
    //set use ui or not
    public boolean useAi = true;
    public Socket s;
    public BufferedReader in;
    public PrintWriter out ;
    public BufferedReader sysin;
    public int x_location;
    public int y_location;
    public char my_color;

    public boolean set_up_flag= false;
    public boolean my_turn;

    
    public Ai(){
        ;
    }
    public void set_location_value(){
        locationValues = new int[][] { { 50, -1, 5, 2, 2, 5, -1, 50 },
				{ -1, -10, 1, 1, 1, 1, -10, -1 }, { 5, 1, 1, 1, 1, 1, 1, 5 },
				{ 2, 1, 1, 0, 0, 1, 1, 2 }, { 2, 1, 1, 0, 0, 1, 1, 2 }, { 5, 1, 1, 1, 1, 1, 1, 5 },
				{ -1, -10, 1, 1, 1, 1, -10, -1 }, { 50, -1, 5, 2, 2, 5, -1, 50 } };
    }
    
    
    //for debug
    public void print_board(){
        for (int i =0 ;i < 8; i++){
            for (int j =0 ; j < 8 ;j++){
                System.out.print(board.board_from_server[i][j]+" ");
            }
            System.out.println("");
        }
    }
    
    //this is to send string to server 
    public void sendToServer(String input){
        System.out.println(input);
        out.println(input);
        out.flush();
    }
    
    public void connect_to_server(String ip_add, String port){
        int port_number = Integer.parseInt(port);
        try {
            s = new Socket(ip_add,port_number);
            //socket input
             in = new BufferedReader(new InputStreamReader(s.getInputStream()));

         //socket output
             out = new PrintWriter(s.getOutputStream());

                 //standard input
             sysin = new BufferedReader(new InputStreamReader(System.in)); 
             //establish two threads
            if(useAi){ 
                Thread send = new Thread(new SendThread(sysin,out));
                send.start();
            }
            Thread receive = new Thread(new ReceiveThread(in));


            receive.start();
        } catch (IOException ex) {
            Logger.getLogger(Ai.class.getName()).log(Level.SEVERE, null, ex);
        }
               
    }
    public char[][] convert_string_to_board_array(String s){
        String temp_string = s.substring(1); //get rid of B
        
        for (int i =0 ;i < 8; i++){
            for (int j =0 ; j < 8 ;j++){
                board.board_from_server[j][i]=temp_string.charAt(i+j*8);
            }
        }
        return board.board_from_server;
    }
    
    //check it is a valid move or not
    public boolean check_valid_move(int x, int y, char color){
        if(board.board_from_server[x][y]!='0'){
            return false;
        }
        //north
        if((y < 6) && (board.board_from_server[x][y+1] != color )&& (board.board_from_server[x][y+1] != '0') ){
            for(int i=y+2; i < 8; i++) {
                if(board.board_from_server[x][i]=='0') {
                    break;
                } else if(board.board_from_server[x][i] == color) {
                    return true;
                }
            }
        }
        //northeast
        if((y < 6) && (x < 6) && (board.board_from_server[x+1][y+1] != color)&& (board.board_from_server[x+1][y+1] != '0') ) {
            for(int i=0; (x+i+2 < 8) && (y+i+2 < 8); i++) {
                if(board.board_from_server[x+i+2][y+i+2]=='0') {
                    break;
                } else if(board.board_from_server[x+i+2][y+i+2] == color) {
                    return true;
                }
            }
        }
        //east
        if((x < 6) && (board.board_from_server[x+1][y] != color)&& (board.board_from_server[x+1][y] != '0') ) {
            for(int i=x+2; i < 8; i++) {
                if(board.board_from_server[i][y] =='0') {
                    break;
                } else if(board.board_from_server[i][y] == color) {
                    return true;
                }
            }
        }
        //southeast
        if((y > 1) && (x < 6) && (board.board_from_server[x+1][y-1] != color)&& (board.board_from_server[x+1][y-1] != '0') ) {
            for(int i=0; (x+i+2 < 8) && (y-i-2 >= 0); i++) {
                if(board.board_from_server[x+i+2][y-i-2] =='0') {
                    break;
                } else if(board.board_from_server[x+i+2][y-i-2] == color) {
                    return true;
                }
            }
        }
        //south
        if((y > 1) && (board.board_from_server[x][y-1] != color)&& (board.board_from_server[x][y-1] != '0') ) {
            for(int i=y-2; i >= 0; i--) {
                if(board.board_from_server[x][i] =='0') {
                    break;
                } else if(board.board_from_server[x][i] == color) {
                    return true;
                }
            }
        }
        //southwest
        if((y > 1) && (x > 1) && (board.board_from_server[x-1][y-1] != color)&& (board.board_from_server[x-1][y-1] != '0') ) {
            for(int i=0; (x-i-2 >= 0) && (y-i-2 >= 0); i++) {
                if(board.board_from_server[x-i-2][y-i-2]=='0') {
                    break;
                } else if(board.board_from_server[x-i-2][y-i-2] == color) {
                    return true;
                }
            }
        }
        //west
        if((x > 1) && (board.board_from_server[x-1][y] != color)&& (board.board_from_server[x-1][y] != '0') ) {
            for(int i=x-2; i >= 0; i--) {
                if(board.board_from_server[i][y]=='0') {
                    break;
                } else if(board.board_from_server[i][y] == color) {
                    return true;
                }
            }
        }
        //northwest
        if((y < 6) && (x > 1) && (board.board_from_server[x-1][y+1] != color)&& (board.board_from_server[x-1][y+1] != '0') ) {
            for(int i=0; (x-i-2 >= 0) && (y+i+2 < 8); i++) {
                if(board.board_from_server[x-i-2][y+i+2]=='0') {
                    break;
                } else if(board.board_from_server[x-i-2][y+i+2] == color) {
                    return true;
                }
            }
        }
        return false;
    }
    
    //check score of this color
    public int check_score(char color){
        int score=0;
        for (int i =0 ;i < 8; i++){
            for (int j =0 ; j < 8 ;j++){
                if(board.board_from_server[i][j]!='0'){
                    if(board.board_from_server[i][j] == color)
                        score += locationValues[i][j];
                    else {
                        score -= locationValues[i][j];
                    }
                }
            }
        }
        return score;
    }
    
    //undo 
    public void undo_board(HashSet<Integer> set,int index){
        Iterator<Integer> set_iterator = set.iterator();
        while(set_iterator.hasNext()){
            int i=set_iterator.next();
            if(board.board_from_server[i/8][i%8]=='1'){
                board.board_from_server[i/8][i%8]='2';
            }
                
            else{
                board.board_from_server[i/8][i%8]='1';
            }
        }
        board.board_from_server[index/8][index%8]='0';
        
    }
    //turn over cells, 
    public HashSet<Integer> update_board(int x,int y,char color){
        //hashset
        HashSet<Integer> return_set = new HashSet<>();
        
        //turn over flag
        boolean ture_over_flag=false;
        //north
        if((y < 6) && (board.board_from_server[x][y+1] != color)&& (board.board_from_server[x][y+1] != '0')) {
            for(int i=y+2; i < 8; i++) {
                if(board.board_from_server[x][i]=='0') {
                    break;
                } else if(board.board_from_server[x][i] == color) {
                    ture_over_flag =true;
                    break;
                }
            }
            if(ture_over_flag){
                for(int i=y+1; i < 8; i++) {
                  if(board.board_from_server[x][i] == color) {
                       break;
                    }
                  return_set.add(x*8+i);
                }
            }
            ture_over_flag=false;
        }
        
        //northeast
        if((y < 6) && (x < 6) && (board.board_from_server[x+1][y+1] != color)&& (board.board_from_server[x+1][y+1] != '0')) {
            for(int i=0; (x+i+2 < 8) && (y+i+2 < 8); i++) {
                if(board.board_from_server[x+i+2][y+i+2]=='0') {
                    break;
                } else if(board.board_from_server[x+i+2][y+i+2] == color) {
                    ture_over_flag =true;
                    break;
                }
            }
            if(ture_over_flag){
                for(int i=0; (x+i+1 < 8) && (y+i+1 < 8); i++) {
                  if(board.board_from_server[x+i+1][y+i+1] == color) {
                        break;
                    }
                  return_set.add((x+i+1)*8+(y+i+1));
                }
            }
            ture_over_flag=false;
        }
        //east
        if((x < 6) && (board.board_from_server[x+1][y] != color)&& (board.board_from_server[x+1][y] != '0')) {
            for(int i=x+2; i < 8; i++) {
                if(board.board_from_server[i][y] =='0') {
                    break;
                } else if(board.board_from_server[i][y] == color) {
                    ture_over_flag =true;
                    break;
                }
            }
            if(ture_over_flag){
                for(int i=x+1; i < 8; i++) {
                    if(board.board_from_server[i][y] == color) {
                        break;
                    }
                    return_set.add((i)*8+y);
                }
            }
        }
        
        //southeast
        ture_over_flag=false;
        if((y > 1) && (x < 6) && (board.board_from_server[x+1][y-1] != color)&& (board.board_from_server[x+1][y-1] != '0')) {
            for(int i=0; (x+i+2 < 8) && (y-i-2 >= 0); i++) {
                if(board.board_from_server[x+i+2][y-i-2] =='0') {
                    break;
                } else if(board.board_from_server[x+i+2][y-i-2] == color) {
                    ture_over_flag =true;
                    break;
                }
            }
            if(ture_over_flag){
                 for(int i=0; (x+i+1 < 8) && (y-i-1 >= 0); i++) {
                    if(board.board_from_server[x+i+1][y-i-1] == color) {
                       
                        break;
                    }
                    return_set.add((x+i+1)*8+(y-i-1));
                }
            }
        }
        //south
        ture_over_flag=false;
        
        if((y > 1) && (board.board_from_server[x][y-1] != color)&& (board.board_from_server[x][y-1] != '0')) {
            for(int i=y-2; i >= 0; i--) {
                if(board.board_from_server[x][i] =='0') {
                    break;
                } else if(board.board_from_server[x][i] == color) {
                    ture_over_flag =true;
                    break;
                }
            }
            if(ture_over_flag){
                for(int i=y-1; i >= 0; i--) {
                    if(board.board_from_server[x][i] == color) {
                        
                        break;
                    }
                    return_set.add((x)*8+i);
                }
            }
        }
        //southwest
        ture_over_flag=false;
        if((y > 1) && (x > 1) && (board.board_from_server[x-1][y-1] != color)&& (board.board_from_server[x-1][y-1] != '0')) {
            for(int i=0; (x-i-2 >= 0) && (y-i-2 >= 0); i++) {
                if(board.board_from_server[x-i-2][y-i-2]=='0') {
                    break;
                } else if(board.board_from_server[x-i-2][y-i-2] == color) {
                    ture_over_flag =true;
                    break;
                }
            }
            if(ture_over_flag){
                for(int i=0; (x-i-1 >= 0) && (y-i-1 >= 0); i++) {
                   if(board.board_from_server[x-i-1][y-i-1] == color) {
                   
                        break;
                    }
                   return_set.add((x-i-1)*8+(y-i-1));
                }
            }
        }
        //west
        ture_over_flag=false;
        if((x > 1) && (board.board_from_server[x-1][y] != color)&& (board.board_from_server[x-1][y] != '0')) {
            for(int i=x-2; i >= 0; i--) {
                if(board.board_from_server[i][y]=='0') {
                    break;
                } else if(board.board_from_server[i][y] == color) {
                    ture_over_flag =true;
                    break;
                }
            }
            if(ture_over_flag){
                for(int i=x-1; i >= 0; i--) {
                    if(board.board_from_server[i][y] == color) {
                        break;
                    }
                    return_set.add((i)*8+y);
                }
            }
        }
        //northwest
        ture_over_flag=false;
        if((y < 6) && (x > 1) && (board.board_from_server[x-1][y+1] != color)&& (board.board_from_server[x-1][y+1] != '0')) {
            for(int i=0; (x-i-2 >= 0) && (y+i+2 < 8); i++) {
                if(board.board_from_server[x-i-2][y+i+2]=='0') {
                    break;
                } else if(board.board_from_server[x-i-2][y+i+2] == color) {
                    ture_over_flag =true;
                    break;
                }
            }
            if(ture_over_flag){
                for(int i=0; (x-i-1 >= 0) && (y+i+1 < 8); i++) {
                    if(board.board_from_server[x-i-1][y+i+1] == color) {
                        break;
                    }
                    return_set.add((x-i-1)*8+(y+i+1));
                }
            }
        }
        return return_set;
    }
    
   Move think(int alpha, int beta, int depthleft, char color ){
       HashSet<Integer> valid_moves = new HashSet<>();
       
        for(int i =0 ; i< 8;i++){
            for (int j =0;j<8;j++){
                //if we can move here then put it into set
                if(check_valid_move(i, j, color)){
                    valid_moves.add(i*8+j);
                   
                }
            }
        }//end for 
        Move [] moves = new Move[valid_moves.size()];
        for (int i =0; i< valid_moves.size(); i++){
            moves[i] = new Move();
        }
        int index=0;
        Iterator<Integer> valid_moves_iterator = valid_moves.iterator();
        while ( valid_moves_iterator.hasNext()) {
            //next move index
            int next_move = valid_moves_iterator.next();
            moves[index].setIndex(next_move);
            //make move
            HashSet<Integer> moves_did = new HashSet<>();
            moves_did = update_board(next_move/8, next_move%8, color);
           int score = alphaBetaMin( alpha, beta, depthleft - 1,color=='1'?'2':'1');
           moves[index].setScore(score);
           //undo
            undo_board(moves_did, next_move);
           if( score >= beta )
              return moves[index];   // fail hard beta-cutoff
           if( score > alpha )
              alpha = score; // alpha acts like max in MiniMax
           index++;
        }
        
        return moves[index-1];
   }
    
   int alphaBetaMax( int alpha, int beta, int depthleft, char color ) {
        if ( depthleft == 0 ) 
            return check_score(color);
        HashSet<Integer> valid_moves = new HashSet<>();
        for(int i =0 ; i< 8;i++){
            for (int j =0;j<8;j++){
                //if we can move here then put it into set
                if(check_valid_move(i, j, color)){
                    valid_moves.add(i*8+j);
                }
            }
        }//end for 
        Iterator<Integer> valid_moves_iterator = valid_moves.iterator();
        while ( valid_moves_iterator.hasNext()) {
            //next move index
            int next_move = valid_moves_iterator.next();
            //make move
            HashSet<Integer> moves_did = new HashSet<>();
            moves_did = update_board(next_move/8, next_move%8, color);
           int score = alphaBetaMin( alpha, beta, depthleft - 1,color=='1'?'2':'1');
           //undo
            undo_board(moves_did, next_move);
           if( score >= beta )
              return beta;   // fail hard beta-cutoff
           if( score > alpha )
              alpha = score; // alpha acts like max in MiniMax
        }
        return alpha;
    }
 
int alphaBetaMin( int alpha, int beta, int depthleft, char color ) {
   if ( depthleft == 0 ) 
       //TODO
       //return -evaluate();
       return -check_score(color);
   HashSet<Integer> valid_moves = new HashSet<>();
    for(int i =0 ; i< 8;i++){
        for (int j =0;j<8;j++){
            //if we can move here then put it into set
            if(check_valid_move(i, j, color)){
                valid_moves.add(i*8+j);
            }
        }
    }//end for 
     Iterator<Integer> valid_moves_iterator = valid_moves.iterator();
    while ( valid_moves_iterator.hasNext()) {
        //next move index
        int next_move = valid_moves_iterator.next();
        //make move
        HashSet<Integer> moves_did = new HashSet<>();
        moves_did = update_board(next_move/8, next_move%8, color);
        
      int score = alphaBetaMax( alpha, beta, depthleft - 1, color=='1'?'2':'1' );
      //undo
      undo_board(moves_did, next_move);
      if( score <= alpha )
         return alpha; // fail hard alpha-cutoff
      if( score < beta )
         beta = score; // beta acts like min in MiniMax
   }
   return beta;
}
    	
    //to test
//    public static void main(String[] args) {
//        
//    }

    public void set_player_name(String text) {
        
        sendToServer(text);
        System.out.println(text);
        
        set_up_flag = true;
                 
        
       
    }
    
        public void start_game() {

               while(true){
                   try { 
                       String board_from_server_String = in.readLine();
                       System.out.println(board_from_server_String);
                       board.board_from_server= convert_string_to_board_array(board_from_server_String);
                       System.out.println("array");
                       print_board();
                       //show_board();
                       System.out.println(board_from_server_String);
                       my_turn = true;
                   } catch (IOException ex) {
                       Logger.getLogger(Reversi_board.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
    }

    public void move(int x,int y) {
        String temp = "M";
        temp = temp + x;
        temp = temp + y;
        
        sendToServer(temp);
    }
        
}
class SendThread implements Runnable
{
	public BufferedReader sysin;
	PrintWriter out;
	public SendThread(BufferedReader si,PrintWriter o)
	{
		sysin = si;
		out = o;
	}

	public void run()
	{
		try
		{
			while(true)
			{
                            String temp;
                            if(board.my_turn){
                                if(ai.useAi){
                                    reversi_gui.Move mo = ai.think(Integer.MIN_VALUE,Integer.MAX_VALUE,10,board.my_color);
                                        temp = "M";
                                        temp = temp + mo.getIndex()/8;
                                        temp = temp + mo.getIndex()%8;
                                        out.println(temp);   
                                        out.flush();  
                                }
                                board.my_turn = false;
				
                            }
                                 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

class ReceiveThread implements Runnable
{
	public BufferedReader in;
	public ReceiveThread(BufferedReader br)
	{
		in=br;
	}
	public void run()
	{
		try
		{
			while(true)
			{
                            String recive_string = in.readLine();
                            System.out.println("this one:"+recive_string);
                            if(recive_string.charAt(0)=='U'){
                                board.my_color = (recive_string.charAt(recive_string.length()-1) == '0'? '1':'2');
                                System.out.println("my color "+board.my_color);
                                //board.
                            }
                            else if (recive_string.charAt(0)=='G'){
                                System.out.print("game over");
                                board.gameover();
                                break;
                            }
                            else{
                                    
                      
                                board.board_from_server= ai.convert_string_to_board_array(recive_string);
                                System.out.println("array");
                                board.print_board();
                                board.show_board();
                      
                                board.my_turn = true;
                                
                            }
                            

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

class Move{
    public int index;
    public int score;
    public Move(){
        ;    
    }
    public void setIndex(int i){
        this.index = i;
                
    }
    public void setScore(int i){
        this.score = i;
        
    }
    public int getIndex(){
        return this.index;
    }
    public int getScore(){
        return this.score;
    }
    
}