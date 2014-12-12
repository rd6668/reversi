///Reversi client sample codes
///Language type C#
///Please send email to cchen14@stevens.edu if you have any question

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;

namespace ReversiClient
{
    class Program
    {

        //server port number
        static int port = 4000;

        //server ip address
        static string server = "127.0.0.1";

        //the piece type your AI hold
        static bool white = true;

        //the board information server sent. AI should make move based on this information
        static int[,] boardInf = new int[8,8];

        //the game over information server sent.
        static bool gameover = false;

        //the winning information server sent. 0 lose, 1 win, 2 tie. You may customize reaction if AI win :)
        static int win = -1;

        //the x,y position of the move your AI made
        static int xIndex = 0;
        static int yIndex = 0;

        //whether client should send move message
        static bool sendMoveMessage = false;

        //Name your own AI. The length of name is less than 10 letters.
        static string nickname = "deepblue";

        //declare your own global variables



        static void Main(string[] args)
        {
            try
            {
                //input port number
                Console.WriteLine("Please input server port:");
                port = int.Parse(Console.ReadLine());
                
                //input server ip
                Console.WriteLine("Please input server ip:");
                server = Console.ReadLine();

                //create ip end point
                IPAddress ip = IPAddress.Parse(server);
                IPEndPoint ipe = new IPEndPoint(ip, port);

                //declare socket
                Socket cSock = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                Console.WriteLine("connecting port {0}", port);

                //connect server
                cSock.Connect(ipe);

                //send nickname to server
                SendMessage("N" + nickname, cSock);

                string recvMessage = "";
                
                //communication loop
                while(!gameover)
                {
                    recvMessage = ReceiveMessage(cSock);

                    ReadMessage(recvMessage);

                    //send move message
                    if (sendMoveMessage) 
                    {
                        string moveMessage = "M" + xIndex.ToString() + yIndex.ToString();
                        SendMessage(moveMessage, cSock);
                        sendMoveMessage = false;
                    }
                }

                //close socket
                cSock.Close();

            }
            catch (ArgumentNullException e)
            {
                Console.WriteLine("argumentNullException:{0}", e);
            }
            catch (SocketException e)
            {
                Console.WriteLine("socketException:{0}", e);
            }
            Console.WriteLine("Press Enter to Exit");
            Console.ReadLine();

        }

        //Decode string message
        static bool ReadMessage(string message) 
        {
            string subString = "";

            //Read board information
            if (message.StartsWith("B"))
            {
                subString = message.Substring(1, 64);
                for (int y = 0; y < 8; y++)
                {
                    for (int x = 0; x < 8; x++)
                    {
                        //store board inf in an int[8,8] array
                        if (int.TryParse(subString.Substring(x + 8 * y, 1), out boardInf[x, y])) ;
                        else
                        {
                            Console.WriteLine("ERROR!! can not read board information.");
                            return false;
                        }
                    }
                }
                Console.WriteLine("board information received");

                //AI make a move
                MakeMove(ref xIndex, ref yIndex);

                return true;
            }
            //Read winning message
            else if (message.StartsWith("W"))
            {
                subString = message.Substring(1, 1);
                if (int.TryParse(subString, out win))
                {
                    //"W0" lose
                    if (win == 0) Console.WriteLine("lose :(");
                    //"W1" win
                    else if (win == 1) Console.WriteLine("win!! :D");
                    //"W2" tie
                    else if (win == 2) Console.WriteLine("tie");
                    else
                    {
                        Console.WriteLine("ERROR!! can not read winning message.");
                        return false;
                    }

                    Console.WriteLine("winning message received");
                    return true;
                }
                else
                {
                    Console.WriteLine("ERROR!! can not read winning message.");
                    return false;
                }
            }
            //Read Gameover message
            else if (message.StartsWith("G"))
            {
                gameover = true;
                Console.WriteLine("game over message recieved");
                return true;
            }
            //Read upper hand message
            else if (message.StartsWith("U"))
            {
                subString = message.Substring(1, 1);
                int upper = -1;
                if (int.TryParse(subString, out upper))
                {
                    //"U0" AI is in defensive position and hold white piece
                    if (upper == 0)
                    {
                        white = true;
                        Console.WriteLine("upper hand message received, defensive position, white piece.");
                    }
                    //"U1" AI is upper hand and hold black piece
                    else if (upper == 1)
                    {
                        white = false;
                        Console.WriteLine("upper hand message received, upper hand, black piece.");
                    }
                    else
                    {
                        Console.WriteLine("ERROR!! can not read upper hand message");
                        return false;
                    }

                    return true;
                }
                else
                {
                    Console.WriteLine("ERROR!! can not read upper hand message.");
                    return false;
                }
            }
            //Read legal message
            else if (message.StartsWith("L"))
            {
                subString = message.Substring(1, 1);

                //"L0" illegal move
                if (int.Parse(subString) == 0)
                {
                    Console.WriteLine("message illegal, resend.");
                    sendMoveMessage = true;
                }
                //"L1" legal move
                else if (int.Parse(subString) == 1)
                {
                    Console.WriteLine("message sent");
                }
                else
                {
                    Console.WriteLine("ERROR!! can not read legal message.");
                    return false;
                }

                return true;
            }
            else
            {
                Console.WriteLine("ERROR!! illegal prefix, can not read message.");
                return false;
            }
        }

        //Convert string message to bytes than send it to server
        static void SendMessage(string message, Socket sock)
        {
            byte[] sendBytes = Encoding.ASCII.GetBytes(message + "\n");

            sock.Send(sendBytes, sendBytes.Length, SocketFlags.None);
        }

        //Receiver bytes from server and convert it to string
        static string ReceiveMessage(Socket sock)
        {
            string recvMessage = "";
            byte[] recvBytes = new byte[128];
            int bytesCount;
            bytesCount = sock.Receive(recvBytes, recvBytes.Length, SocketFlags.None);
            recvMessage += Encoding.ASCII.GetString(recvBytes, 0, bytesCount);

            return recvMessage;
        }


        static void MakeMove(ref int x, ref int y) 
        {
            //here is where your AI works
            //boardInf[x,y] values: 
            // 7 |0 0 0 0 0 0 0 0
            // 6 |0 0 0 0 0 0 0 0
            // 5 |0 0 0 0 0 0 0 0
            // 4 |0 0 0 1 2 0 0 0
            // 3 |0 0 0 2 1 0 0 0
            // 2 |0 0 0 0 0 0 0 0
            // 1 |0 0 0 0 0 0 0 0
            // 0 |0 0 0 0 0 0 0 0
            // y  - - - - - - - -
            // x  0 1 2 3 4 5 6 7
            //0 no piece in location x,y
            //1 white piece in location x,y
            //2 black piece in location x,y

            //start AI codes
            
            //test codes
            Console.WriteLine("input x y:");
            x = int.Parse(Console.ReadLine());
            y = int.Parse(Console.ReadLine());


            //end AI codes

            sendMoveMessage = true;
        }

        //add your own functions here



    }
}
