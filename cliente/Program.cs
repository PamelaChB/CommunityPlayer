using System;
using System.IO;
using System.Net.Sockets;
using System.Text;

namespace Sockets
{
    class ClientSocket
    {
        private int port;
        private TcpClient client;
        private NetworkStream stream;

        public ClientSocket(int port, int sendTimeout, int receiveTimeout)
        {
            this.port = port;
            client = new TcpClient("localhost", port);

            client.ReceiveTimeout = sendTimeout;
            client.SendTimeout = receiveTimeout;
            stream = client.GetStream();
        }

        public void processData(String data)
        {
            byte[] buf;

            /* append newline as server expects a line to be read */
            buf = Encoding.UTF8.GetBytes(data + "\n");

            Console.WriteLine("Sending data \'{0}\' to server", data);

            stream.Write(buf, 0, data.Length + 1);

            // Receive the response from the server
            using (StreamReader reader = new StreamReader(stream))
            {
                string response = reader.ReadLine();
                Console.WriteLine("Received Response: \'{0}\', of length {1}", response, response.Length);
            }

            client.Close();
        }
    }

    class Client
    {
        static void Main(string[] args)
        {
            try
            {
                ClientSocket clientSock = new ClientSocket(12345, 3000, 3000);
                String data = "Hello World";

                clientSock.processData(data);

            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }

            Console.WriteLine("Press Enter to close the connection");
            Console.Read();
        }
    }
}