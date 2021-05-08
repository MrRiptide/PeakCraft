using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ItemManager
{
    class Item
    {
        public readonly String id;
        public readonly String oreDict;
        public readonly String displayName;
        public readonly int rarity;
        public readonly String description;
        public readonly Material material;
        public readonly String type;
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

        public Item(String id, String oreDict, String displayName, int rarity, String description, String materialId, String type)
        {
            this.id = id;
            this.oreDict = oreDict;
            this.displayName = displayName;
            this.rarity = rarity;
            this.description = description;
            this.material = new Material(materialId);
            this.type = (type != null && !String.IsNullOrEmpty(type)) ? type : "item";
        }
    }
}