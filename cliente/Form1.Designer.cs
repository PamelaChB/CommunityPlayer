using System;
using System.Net.Sockets;
using System.Text;
using System.Windows.Forms;

namespace cliente
{
    partial class Form1
    {
        private System.ComponentModel.IContainer components = null;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox textBox1;

        // Declaración de TcpClient
        private TcpClient client;

        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(800, 450);
            this.Text = "Form3";


            // Button
            this.button1 = new System.Windows.Forms.Button();
            this.button1.Location = new System.Drawing.Point(100, 100);
            this.button1.Size = new System.Drawing.Size(100, 50);
            this.button1.Text = "Click Me";
            this.button1.Click += new System.EventHandler(this.button1_Click);
            this.Controls.Add(this.button1);

            // Label
            this.label1 = new System.Windows.Forms.Label();
            this.label1.Location = new System.Drawing.Point(100, 200);
            this.label1.Size = new System.Drawing.Size(100, 50);
            this.label1.Text = "";
            this.Controls.Add(this.label1);

            // TextBox
            this.textBox1 = new System.Windows.Forms.TextBox();
            this.textBox1.Location = new System.Drawing.Point(100, 50);
            this.textBox1.Size = new System.Drawing.Size(200, 25);
            this.Controls.Add(this.textBox1);
            this.button1 = new System.Windows.Forms.Button();
            this.button1.Location = new System.Drawing.Point(100, 100);
            this.button1.Size = new System.Drawing.Size(100, 50);
            this.button1.Text = "Click Me";
            this.button1.Click += new System.EventHandler(this.button1_Click);
            this.Controls.Add(this.button1);
        }

        // Manejador de eventos del botón
        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                // Establecer la conexión con el servidor
                client = new TcpClient("localhost", 12345);

                // Obtener el contenido del TextBox
                string textToSend = textBox1.Text;

                // Enviar el contenido al servidor
                NetworkStream stream = client.GetStream();
                byte[] data = Encoding.ASCII.GetBytes(textToSend);
                stream.Write(data, 0, data.Length);

                // Cerrar la conexión
                client.Close();

                // Actualizar el label 
                label1.Text = "Mensaje enviado al servidor: " + textToSend;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error al enviar datos al servidor: " + ex.Message);
            }
        }
    }
}