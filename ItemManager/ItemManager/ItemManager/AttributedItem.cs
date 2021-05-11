using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ItemManager
{
    class AttributedItem : Item
    {
        Dictionary<String, Double> attributes;

        public AttributedItem(String id, String oreDict, String displayName, int rarity, String description, Material material, String type) 
            : base(id, oreDict, displayName, rarity, description, material, type)
        {
            attributes = new Dictionary<string, double>();
        }

        public AttributedItem(Item item) : base(item)
        {
            attributes = new Dictionary<string, double>();
        }

        public static new Item fromDictionary(Dictionary<string, string> data)
        {
            AttributedItem item =  new AttributedItem(
                            data["id"],
                            data["oreDict"],
                            data["displayName"],
                            Int32.Parse(data["rarity"]),
                            data["description"],
                            new Material(data["materialID"]),
                            data["type"]
                            );


            foreach (string key in new string[]{ "id", "oreDict", "displayName", "rarity", "description", "materialID", "type" })
            {
                data.Remove(key);
            }

            foreach (string key in data.Keys)
            {
                item.attributes.Add(key, double.Parse(data[key]));
            }

            return item;
        }

        public new Dictionary<string, string> toDictionary()
        {
            Dictionary<string, string> data = base.toDictionary();

            foreach (string key in attributes.Keys)
            {
                data.Add(key, attributes[key].ToString());
            }

            return data;
        }

        public void setAttribute(string name, double value)
        {
            attributes[name] = value;
        }
    }
}
