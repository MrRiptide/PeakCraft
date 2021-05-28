using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.IO;

namespace ItemManager
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private SortedDictionary<String, Item> items;
        private int index = -1;
        private String path;
        private string[] types = new string[] { "item", "armor", "chestplate", "helmet", "leggings", "boots", "weapon", "sword" };

        public MainWindow()
        {
            InitializeComponent();
            foreach (string type in types)
            {
                typeComboBox.Items.Add(type);
            }
        }

        private void loadItemFile(string path)
        {
            items = new SortedDictionary<String, Item>();
            try
            {
                Dictionary<String, Dictionary<String, String>> data = JsonSerializer.Deserialize<Dictionary<String, Dictionary<String, String>>>(File.OpenText(path).ReadToEnd());

                foreach (String key in data.Keys)
                {
                    Dictionary<String, String> itemData = data[key];
                    String type = itemData["type"].ToLower();
                    Item item;
                    if ((new string[] { "armor", "chestplate", "helmet", "leggings", "boots", "weapon", "sword" }).Contains(type))
                    {
                        item = AttributedItem.fromDictionary(itemData);
                    }
                    else
                    {
                        item = Item.fromDictionary(itemData);
                    }

                    items.Add(key.ToLower(), item);
                }
            }
            catch (JsonException exception)
            {
                MessageBox.Show("Invalid json");
            }
            
        }

        private void loadItems()
        {
            foreach (string itemID in items.Keys)
            {
                itemListBox.Items.Add(itemID);
            }
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog();

            dialog.DefaultExt = ".json";
            dialog.Filter = "JSON Files (.json) | *.json";
            bool? result = dialog.ShowDialog();

            if (result == true)
            {
                path = dialog.FileName;
                loadItemFile(path);

                loadItems();

                searchBox.IsEnabled = true;
                addItemButton.IsEnabled = true;
                saveItemsButton.IsEnabled = true;
            }

        }

        private void searchBox_TextChanged(object sender, TextChangedEventArgs e)
        {
            itemListBox.Items.Clear();

            if (items != null)
            {
                foreach (string itemID in items.Keys)
                {
                    if (itemID.ToLower().Contains(searchBox.Text.ToLower()))
                    {
                        itemListBox.Items.Add(itemID.ToLower());
                    }
                }
            }
        }

        private void itemListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (itemListBox.SelectedItem == null)
            {
                itemDataViewer.Visibility = Visibility.Hidden;
            }
            else
            {
                Item item = items[itemListBox.SelectedItem.ToString().ToLower()];

                if (item == null)
                {
                    itemDataViewer.Visibility = Visibility.Hidden;
                }
                else
                {
                    itemDataViewer.Visibility = Visibility.Visible;

                    loadItem(item);
                }
            }
            
        }

        private void saveItemButton_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                items[itemIDTextBox.Text.ToLower()] = getCurrentItem();
            }
            catch (FormatException exception)
            {
                MessageBox.Show("Invalid values");
            }
        }

        private Item getCurrentItem()
        {
            Item item = new Item(
                    itemIDTextBox.Text,
                    oreDictTextBox.Text,
                    displayNameTextBox.Text,
                    rarityComboBox.SelectedIndex + 1,
                    descriptionTextBox.Text,
                    new Material(materialTextBox.Text),
                    types[typeComboBox.SelectedIndex]);

            if ((new string[] { "armor", "chestplate", "helmet", "leggings", "boots", "weapon", "sword" }).Contains(item.type))
            {
                item = new AttributedItem(item);

                for (int i = 0; i < attributeLabels.Count; i++)
                {
                    if (attributeLimit[i] && !double.TryParse(attributeBoxes[i].Text, out double k))
                    {
                        MessageBox.Show($"Invalid data at index {i} \"{attributeBoxes[i].Text}\"");
                    } else
                    {
                        ((AttributedItem)item).setAttribute(attributeBoxes[i].Tag.ToString(), attributeBoxes[i].Text);
                    }
                }
            }
            return item;
        }

        private void addItem_Click(object sender, RoutedEventArgs e)
        {
            AddItemWindow window = new AddItemWindow(this);
            window.Show();
        }

        private void saveItemsButton_Click(object sender, RoutedEventArgs e)
        {

            Dictionary<string, Dictionary<string, string>> data = new Dictionary<string, Dictionary<string, string>>();

            foreach (Item item in items.Values)
            {
                Dictionary<string, string> itemData;
                if (item is AttributedItem)
                {
                    itemData = ((AttributedItem)item).toDictionary();
                } else
                {
                    itemData = item.toDictionary();
                }
                data.Add(item.id, itemData);
            }

            File.WriteAllText(path, JsonSerializer.Serialize(data));
        }

        public void addItem(string itemID)
        {
            items.Add(itemID.ToLower(),
                new Item(
                    itemID,
                    "",
                    "",
                    1,
                    "",
                    new Material(""),
                    ""));

            loadItems();
        }

        private List<Label> attributeLabels = new List<Label>();
        private List<TextBox> attributeBoxes = new List<TextBox>();
        private List<Boolean> attributeLimit = new List<bool>();

        private void typeComboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            loadItem(getCurrentItem());
        }

        private void addAttributeLabel(string name, string value = "0", bool limitTypes = true)
        {
            TextBox attributeTextBox = new TextBox();
            attributeTextBox.Text = value;
            attributeTextBox.Tag = name.ToLower();
            Grid.SetRow(attributeTextBox, attributeBoxes.Count + 8);
            Grid.SetColumn(attributeTextBox, 2);

            Label attributeLabel = new Label();
            attributeLabel.Content = name + ": ";
            attributeLabel.Tag = name.ToLower();
            Grid.SetRow(attributeLabel, attributeLabels.Count + 8);
            Grid.SetColumn(attributeLabel, 1);

            itemGrid.Children.Add(attributeLabel);
            itemGrid.Children.Add(attributeTextBox);
            attributeBoxes.Add(attributeTextBox);
            attributeLabels.Add(attributeLabel);
            attributeLimit.Add(limitTypes);
        }

        private void loadItem(Item item)
        {
            itemIDTextBox.Text = item.id;
            oreDictTextBox.Text = item.oreDict;
            displayNameTextBox.Text = item.displayName;
            rarityComboBox.SelectedIndex = item.rarity - 1;
            descriptionTextBox.Text = item.description;
            materialTextBox.Text = item.material.id;
            typeComboBox.SelectedIndex = Math.Max(Array.IndexOf(types, item.type), 0);

            // remove any current labels or text boxes
            for (int i = 0; i < attributeLabels.Count; i++)
            {
                itemGrid.Children.Remove(attributeLabels[i]);
                itemGrid.Children.Remove(attributeBoxes[i]);
            }

            attributeLabels.Clear();
            attributeBoxes.Clear();

            // armor
            if (typeComboBox.SelectedIndex >= 1 && typeComboBox.SelectedIndex <= 5)
            {
                addAttributeLabel("Set", limitTypes:false, value:(item is AttributedItem ? ((AttributedItem)item).getAttributeOrDefault("set", "") : ""));
                addAttributeLabel("Health", value: (item is AttributedItem ? ((AttributedItem)item).getAttributeOrDefault("health", "0") : "0"));
                addAttributeLabel("Defense", value: (item is AttributedItem ? ((AttributedItem)item).getAttributeOrDefault("defense", "0") : "0"));
            } // weapons
            else if (typeComboBox.SelectedIndex >= 6 && typeComboBox.SelectedIndex <= 7)
            {
                addAttributeLabel("Ability", limitTypes:false, value: (item is AttributedItem ? ((AttributedItem)item).getAttributeOrDefault("ability", "") : ""));
                addAttributeLabel("Damage", value: (item is AttributedItem ? ((AttributedItem)item).getAttributeOrDefault("damage", "0") : "0"));
            }

            itemGrid.Height = itemDataViewer.ActualHeight * (1 + 0.1 * attributeBoxes.Count);

            itemGrid.RowDefinitions.RemoveRange(9, itemGrid.RowDefinitions.Count - 10);
            for (int i = 0; i < attributeBoxes.Count; i++)
            {
                RowDefinition rowDefinition = new RowDefinition();
                rowDefinition.Height = new GridLength(1, GridUnitType.Star);
                itemGrid.RowDefinitions.Add(rowDefinition);
            }

            Grid.SetRow(saveItemButton, itemGrid.RowDefinitions.Count - 1);
        }
    }
}
