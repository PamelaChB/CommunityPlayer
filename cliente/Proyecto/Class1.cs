using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace Proyecto
{
    internal class Mensaje
    {
        [JsonProperty("command")]
        public string Command { get; set; }

        public Mensaje(string command)
        {
            Command = command;


        }
        public string ToJson()
        { 
            return JsonConvert.SerializeObject(this);
        }
    }
}
