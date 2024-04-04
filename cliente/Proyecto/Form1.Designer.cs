using System;
using System.Net.Sockets;
using System.Text;
using System.Windows.Forms;
using IniParser;
using IniParser.Model;

namespace Proyecto
{
    partial class Form1
    {
        private IniData data;
        private System.ComponentModel.IContainer components = null;
        private System.Windows.Forms.Button btnUpVote;
        private System.Windows.Forms.Button btnDownVote;
        private System.Windows.Forms.Label label1;
        private TcpClient client;

        private void InitializeComponent()
        {
            var parser = new FileIniDataParser();
            this.data = parser.ReadFile("C:\\Users\\fmoreno\\source\\repos\\Proyecto\\config.ini");
            this.components = new System.ComponentModel.Container();
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(800, 450);
            this.Text = "Cliente";

            // Botón Up-Vote
            this.btnUpVote = new System.Windows.Forms.Button();
            this.btnUpVote.Location = new System.Drawing.Point(100, 100);
            this.btnUpVote.Size = new System.Drawing.Size(100, 50);
            this.btnUpVote.Text = "Up-Vote";
            this.btnUpVote.Click += new System.EventHandler(this.btnUpVote_Click);
            this.Controls.Add(this.btnUpVote);

            // Botón Down-Vote
            this.btnDownVote = new System.Windows.Forms.Button();
            this.btnDownVote.Location = new System.Drawing.Point(250, 100);
            this.btnDownVote.Size = new System.Drawing.Size(100, 50);
            this.btnDownVote.Text = "Down-Vote";
            this.btnDownVote.Click += new System.EventHandler(this.btnDownVote_Click);
            this.Controls.Add(this.btnDownVote);

            // Label
            this.label1 = new System.Windows.Forms.Label();
            this.label1.Location = new System.Drawing.Point(100, 200);
            this.label1.Size = new System.Drawing.Size(250, 50);
            this.label1.Text = "";
            this.Controls.Add(this.label1);

            // TextBox
            //this.textBox1 = new System.Windows.Forms.TextBox();
            //this.textBox1.Location = new System.Drawing.Point(100, 50);
            //this.textBox1.Size = new System.Drawing.Size(250, 25);
            //this.Controls.Add(this.textBox1);
        }

        private void btnUpVote_Click(object sender, EventArgs e)
        {
            // Leer la configuración del archivo .INI
            string puertoEscucha = data["Servidor"]["PuertoEscucha"];

            SendVote("up", puertoEscucha);

            // Imprimir el puerto en consola
            Console.WriteLine("Hello, World! My port is: " + puertoEscucha);
        }

        private void btnDownVote_Click(object sender, EventArgs e)
        {
            // Leer la configuración del archivo .INI
            string puertoEscucha = data["Servidor"]["PuertoEscucha"];

            SendVote("down", puertoEscucha);

            // Imprimir el puerto en consola
            Console.WriteLine("Hello, World! My port is: " + puertoEscucha);
        }

        private void SendVote(string vote, string puertoEscucha)
        {
            try
            {
                // Verificar si puertoEscucha es nulo o vacío antes de usarlo
                if (string.IsNullOrEmpty(puertoEscucha))
                {
                    MessageBox.Show("Error: Puerto de escucha no especificado en la configuración.");
                    return;
                }

                // Establecer la conexión con el servidor
                client = new TcpClient("localhost", int.Parse(puertoEscucha));

                NetworkStream stream = client.GetStream();

                string message = "vote:" + vote;

                byte[] data = Encoding.ASCII.GetBytes(message);

                stream.Write(data, 0, data.Length);

                client.Close();

                label1.Text = "Voto enviado al servidor: " + vote;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error al enviar datos al servidor: " + ex.Message);
            }
        }

    }
}
