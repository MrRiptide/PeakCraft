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
        private List<Item> items;
        public MainWindow()
        {
            InitializeComponent();
        }

        private void loadItemFile(string path)
        {
            List<Dictionary<String, String>> data = JsonSerializer.Deserialize<List<Dictionary<String, String>>>(File.OpenText(path).ReadToEnd());

            foreach (Dictionary<String, String> itemData in data)
            {

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

            }

        }
    }
}
