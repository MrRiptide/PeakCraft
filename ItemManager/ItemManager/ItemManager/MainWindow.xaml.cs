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
using System.Xml;

namespace ItemManager
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private XmlDocument doc;
        public MainWindow()
        {
            InitializeComponent();
        }

        private void loadItemFile(string path)
        {
            doc = new XmlDocument();
            doc.PreserveWhitespace = true;
            try
            {
                doc.Load(path);
            }
            catch (System.IO.FileNotFoundException)
            {
                return;
            }
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog();

            dialog.DefaultExt = ".xml";
            dialog.Filter = "XML Files (.xml) | *.xml";
            bool? result = dialog.ShowDialog();

            if (result == true)
            {

            }

        }
    }
}
