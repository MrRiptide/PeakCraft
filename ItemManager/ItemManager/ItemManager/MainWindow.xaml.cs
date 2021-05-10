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
        public MainWindow()
        {
            InitializeComponent();
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

                    itemIDTextBox.Text = item.id;
                    oreDictTextBox.Text = item.oreDict;
                    displayNameTextBox.Text = item.displayName;
                    rarityComboBox.SelectedIndex = item.rarity - 1;
                    descriptionTextBox.Text = item.description;
                    materialTextBox.Text = item.material.id;
                    typeTextBox.Text = item.type;
                }
            }
            
        }

        private void saveItemButton_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                items[itemIDTextBox.Text.ToLower()] = new Item(
                    itemIDTextBox.Text,
                    oreDictTextBox.Text,
                    displayNameTextBox.Text,
                    rarityComboBox.SelectedIndex + 1,
                    descriptionTextBox.Text,
                    new Material(materialTextBox.Text),
                    typeTextBox.Text);
            }
            catch (FormatException exception)
            {
                MessageBox.Show("Invalid values");
            }
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
                data.Add(item.id, item.toDictionary());
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
    }
}
