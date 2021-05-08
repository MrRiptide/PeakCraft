using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ItemManager
{
    class AttributedItem : Item
    {
        Dictionary<String, String> attributes;

        public AttributedItem(String id, String oreDict, String displayName, int rarity, String description, Material material, String type) 
            : base(id, oreDict, displayName, rarity, description, material, type)
        {
            attributes = new Dictionary<string, string>();
        }
    }
}
