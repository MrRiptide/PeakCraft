using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ItemManager
{
    class Item
    {
        private String id;
        private String oreDict;
        private String displayName;
        private int rarity;
        private String description;
        private Material material;
        private String type;
        public Item(String id, String oreDict, String displayName, int rarity, String description, Material material, String type)
        {
            this.id = id;
            this.oreDict = oreDict;
            this.displayName = displayName;
            this.rarity = rarity;
            this.description = description;
            this.material = material;
            this.type = (type != null && !String.IsNullOrEmpty(type)) ? type : "item";
        }
    }
}