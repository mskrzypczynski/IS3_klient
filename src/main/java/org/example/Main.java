package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Service;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Vector;

public class Main extends JFrame {

    public final String [] columnHeaders =
            {
                    "Producent",
                    "wielkość matrycy",
                    "rozdzielczość",
                    "typ matrycy",
                    "czy dotykowy ekran",
                    "procesor",
                    "liczba rdzeni fizycznych",
                    "taktowanie",
                    "RAM",
                    "pojemność dysku",
                    "typ dysku",
                    "karta graficzna",
                    "pamięć karty graficznej",
                    "system operacyjny",
                    "napęd optyczny"
            };
    public final String windowTitle = "Integracja Systemów - Mikołaj Skrzypczyński - zadanie 3 - aplikacja klienta";
    public final int width = 1366;
    public final int height = 768;
    public final Dimension windowDimension = new Dimension(width,height);

    private JScrollPane scrollPane;
    private JTable table;

    private DefaultTableModel tableModel;

    private JFileChooser fileChooser;

    private JComboBox firstArea = null;
    private JComboBox secondArea = null;

    private JComboBox physicalCores = null;
    private JComboBox touchscreen = null;
    private JComboBox ram = null;
    private JComboBox processor = null;
    private JComboBox graphicCard = null;
    private JTextArea responseArea = null;

    private JButton btnGetCountByRes;
    private JButton btnGetCountByMan;
    private JButton btnGetLaptopsByFeatures;
    private JLabel resultMan;
    private JLabel resultRes;
    private JLabel labelFirstArea;
    private JLabel labelSecondArea;
    private JLabel labelThirdArea;
    private JPanel pnlStart;
    private JPanel pnlFirstArea;
    private JPanel pnlSecondArea;
    private JPanel pnlThirdArea;
    private JPanel pnlButtons;
    private JPanel pnlLabel;

    private URL url;
    QName qNameService;
    QName qNamePort;
    Service service;
    LaptopsInterface laptopsInterface;


    public static void main(String[] args) {

        Main app = new Main();
        //  Main app = new Main();
        System.out.println("koniec funkcji main");
    }

    void initService(){
        URL url=null;
        try {
            url = new URL("http://localhost:8888/laptops?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        QName qname = new QName("http://example.org/", "LaptopsBeanService");
        QName qNamePort = new QName("http://example.org/","LaptopsBeanPort");
        service = Service.create(url, qname);
        laptopsInterface = service.getPort(qNamePort,LaptopsInterface.class);
    }



    void getLaptopsByFeat(){

        String f1 = physicalCores.getSelectedItem().toString();
        String f2 = touchscreen.getSelectedItem().toString();
        String f3 = ram.getSelectedItem().toString();
        String f4 = processor.getSelectedItem().toString();
        String f5 = graphicCard.getSelectedItem().toString();

        Laptop[] arr =  laptopsInterface.getLaptopListByFeatures(f1,f2,f3,f4,f5);

        tableModel.setRowCount(0);
        for(Laptop l : arr){
            Vector<String> e = new Vector();
            e.add(l.getManufacturer());
            e.add(l.getScreenSize());
            e.add(l.getResolution());
            e.add(l.getScreenType());
            e.add(l.getScreenTouchscreen());
            e.add(l.getProcessorName());
            e.add(l.getProcessorPhysicalCores().toString());
            e.add(l.getProcessorSpeed());
            e.add(l.getRam());
            e.add(l.getDiscStorage());
            e.add(l.getDiscType());
            e.add(l.getGraphicCardName());
            e.add(l.getGraphicCardMemory());
            e.add(l.getOs());
            e.add(l.getDiscReader());

            tableModel.addRow(e);
            System.out.println("wektor e: "  + e.toString());
        }

        System.out.println(arr);
       // this.resultMan.setText(arr.toString());

    }
    void getCountByMan(){
        int i = laptopsInterface.getManufacturerLaptopNumber(firstArea.getSelectedItem().toString());
        String option = firstArea.getSelectedItem().toString();
        System.out.println(i);
        System.out.println(option);
        this.resultMan.setText("Znaleziono " + i +  " laptopow producenta: " + option);
    }
    void getCountByRes(){
        int i = laptopsInterface.getResolutionLaptopNumber(secondArea.getSelectedItem().toString());
        String option = secondArea.getSelectedItem().toString();
        System.out.println(i);
        System.out.println(option);
        this.resultRes.setText("Znaleziono " + i +  " laptopow o wielkosci ekranu: " + option);
    }


    void saveToXml(){
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION){
            //file is selected
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            try{
                FileWriter fileWriter = new FileWriter(selectedFile.getAbsolutePath());
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                StringBuilder stringBuilder = new StringBuilder();

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.newDocument();

                Element rootElement  = doc.createElement("laptops");
                doc.appendChild(rootElement);

                int rowCount = tableModel.getRowCount();
                int colCount = tableModel.getColumnCount();

                for(int i = 0; i < rowCount; i++)
                {
                    Element laptop = doc.createElement("laptop");
                    String str = "";
                    Element manufacturer = doc.createElement("manufacturer");
                    if(table.getValueAt(i,0) != null )
                        str  = table.getValueAt(i,0).toString();
                    else str = "";
                    manufacturer.setTextContent(str);
                    laptop.appendChild(manufacturer);

                    Element screen = doc.createElement("screen");
                    Element screen_size = doc.createElement("size");
                    if(table.getValueAt(i,1) != null )
                        str  = table.getValueAt(i,1).toString();
                    else str = "";
                    screen_size.setTextContent(str);
                    screen.appendChild(screen_size);

                    Element screen_resolution = doc.createElement("resolution");
                    if(table.getValueAt(i,2) != null )
                        str  = table.getValueAt(i,2).toString();
                    else str = "";
                    screen_resolution.setTextContent(str);
                    screen.appendChild(screen_resolution);

                    Element screen_type = doc.createElement("type");
                    if(table.getValueAt(i,3) != null )
                        str  = table.getValueAt(i,3).toString();
                    else str = "";
                    screen_type.setTextContent(str);
                    screen.appendChild(screen_type);

                    Element screen_touchscreen = doc.createElement("touchscreen");
                    if(table.getValueAt(i,4) != null )
                        str  = table.getValueAt(i,4).toString();
                    else str = "";
                    screen_touchscreen.setTextContent(str);
                    screen.appendChild(screen_touchscreen);
                    laptop.appendChild(screen);

                    Element processor = doc.createElement("processor");
                    Element processor_name = doc.createElement("name");
                    if(table.getValueAt(i,5) != null )
                        str  = table.getValueAt(i,5).toString();
                    else str = "";
                    processor_name.setTextContent(str);
                    processor.appendChild(processor_name);


                    Element processor_physical_cores = doc.createElement("physical_cores");
                    if(table.getValueAt(i,6) != null )
                        str  = table.getValueAt(i,6).toString();
                    else str = "";
                    processor_physical_cores.setTextContent(str);
                    processor.appendChild(processor_physical_cores);

                    Element processor_clock_speed = doc.createElement("clock_speed");
                    if(table.getValueAt(i,7) != null )
                        str  = table.getValueAt(i,7).toString();
                    else str = "";
                    processor_clock_speed.setTextContent(str);
                    processor.appendChild(processor_clock_speed);
                    laptop.appendChild(processor);

                    Element ram = doc.createElement("ram");
                    if(table.getValueAt(i,8) != null )
                        str  = table.getValueAt(i,8).toString();
                    else str = "";
                    ram.setTextContent(str);
                    laptop.appendChild(ram);

                    Element disc = doc.createElement("disc");

                    Element disc_storage = doc.createElement("storage");
                    if(table.getValueAt(i,9) != null )
                        str  = table.getValueAt(i,9).toString();
                    else str = "";
                    disc_storage.setTextContent(str);
                    disc.appendChild(disc_storage);

                    Element disc_type = doc.createElement("type");
                    if(table.getValueAt(i,10) != null )
                        str  = table.getValueAt(i,10).toString();
                    else str = "";
                    disc_type.setTextContent(str);
                    disc.appendChild(disc_type);
                    laptop.appendChild(disc);

                    Element graphic_card = doc.createElement("graphic_card");
                    Element graphic_card_name = doc.createElement("name");
                    if(table.getValueAt(i,11) != null )
                        str  = table.getValueAt(i,11).toString();
                    else str = "";
                    graphic_card_name.setTextContent(str);
                    graphic_card.appendChild(graphic_card_name);

                    Element graphic_card_memory = doc.createElement("memory");
                    if(table.getValueAt(i,12) != null )
                        str  = table.getValueAt(i,12).toString();
                    else str = "";
                    graphic_card_memory.setTextContent(str);
                    graphic_card.appendChild(graphic_card_memory);
                    laptop.appendChild(graphic_card);

                    Element os = doc.createElement("os");
                    if(table.getValueAt(i,13) != null )
                        str  = table.getValueAt(i,13).toString();
                    else str = "";
                    os.setTextContent(str);
                    laptop.appendChild(os);

                    Element disc_reader = doc.createElement("disc_reader");
                    if(table.getValueAt(i,14) != null )
                        str  = table.getValueAt(i,14).toString();
                    else str = "";
                    disc_reader.setTextContent(str);
                    laptop.appendChild(disc_reader);

                    rootElement.appendChild(laptop);
                }


                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(doc);
                FileWriter writer = new FileWriter(selectedFile);
                Result streamResult = new StreamResult(writer);

                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(domSource,streamResult);



            }
            catch (Exception ex){
                System.out.println("error:" + ex.getMessage());
            }

        }
    }

    Main(){
        initService();
        loadAllLaptops();

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle(windowTitle);
        //set window size
        this.setSize(windowDimension);
        //set layout manager to border layout
        BorderLayout borderLayout = new BorderLayout();
        this.setLayout(new BorderLayout());


        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(columnHeaders);
        table = new JTable();
        table.setModel(tableModel);
        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);




        btnGetCountByMan = new JButton("Zapytaj o ilość laptopów producenta");
        btnGetCountByRes = new JButton("Zapytaj o ilość laptopów po wielkości ekranu");
        btnGetLaptopsByFeatures = new JButton("Pobierz listę laptopów o wybranych cechach");
        resultMan = new JLabel("Znaleziono 0 laptopow producenta");
        resultRes = new JLabel("Znaleziono 0 laptopow o wybranej wielkosci ekranu");

        btnGetCountByMan.addActionListener(actionEvent -> getCountByMan());
        btnGetCountByRes.addActionListener(actionEvent -> getCountByRes());
        btnGetLaptopsByFeatures.addActionListener(actionEvent -> getLaptopsByFeat());

        labelFirstArea = new JLabel("Wybierz producenta:");
        labelSecondArea = new JLabel("Wybierz wielkosc ekranu:");
        labelThirdArea = new JLabel("Wybierz wartosci cech do wyszukania laptopow:");


        pnlFirstArea = new JPanel();
        pnlFirstArea.setLayout(new BoxLayout(pnlFirstArea,BoxLayout.Y_AXIS));
        pnlFirstArea.add(labelFirstArea);
        pnlFirstArea.add(firstArea);
        pnlFirstArea.add(btnGetCountByMan);
        pnlFirstArea.add(resultMan);

        pnlSecondArea = new JPanel();
        pnlSecondArea.setLayout(new BoxLayout(pnlSecondArea,BoxLayout.Y_AXIS));
        pnlSecondArea.add(labelSecondArea);
        pnlSecondArea.add(secondArea);
        pnlSecondArea.add(btnGetCountByRes);
        pnlSecondArea.add(resultRes);

        pnlThirdArea = new JPanel();
        pnlThirdArea.setLayout(new GridLayout(0,2));
        pnlThirdArea.add(labelThirdArea);
        pnlThirdArea.add(new JLabel());

        pnlThirdArea.add(new JLabel("Liczba rdzeni: "));
        pnlThirdArea.add(physicalCores);

        pnlThirdArea.add(new JLabel("Czy ekran dotykowy: "));
        pnlThirdArea.add(touchscreen);

        pnlThirdArea.add(new JLabel("RAM: "));
        pnlThirdArea.add(ram);

        pnlThirdArea.add(new JLabel("Procesor: "));
        pnlThirdArea.add(processor);

        pnlThirdArea.add(new JLabel("Karta graficzna: "));
        pnlThirdArea.add(graphicCard);


        pnlThirdArea.add(btnGetLaptopsByFeatures);
        pnlThirdArea.add(new JLabel());


        pnlStart = new JPanel(new FlowLayout());
        pnlStart.add(pnlFirstArea);
        pnlStart.add(pnlSecondArea);
        pnlStart.add(pnlThirdArea);
        pnlStart.add(scrollPane);
        //pnlStart.add();

        this.add(pnlStart,BorderLayout.CENTER);
       // this.pack();
       /*
        this.add(btnGetLaptopsByFeatures);
        this.add(firstArea);
        this.add(secondArea);

*/
        setDefaultValue(firstArea);
        setDefaultValue(secondArea);
        setDefaultValue(physicalCores);
        setDefaultValue(touchscreen);
        setDefaultValue(ram);
        setDefaultValue(processor);
        setDefaultValue(graphicCard);

        this.setVisible(true);
    }


    public void loadAllLaptops(){

        Laptop[] allLaptops = laptopsInterface.getAllLaptops();
        Integer i = 1;
        HashSet<String> manufacturers = new HashSet<String>();
        HashSet<String> screenSizes = new HashSet<String>();
        HashSet<String> allPhysicalCore = new HashSet<String>();
        HashSet<String> allTouchscreen = new HashSet<String>();
        HashSet<String> allRam = new HashSet<String>();
        HashSet<String> allProcessor = new HashSet<String>();
        HashSet<String> allGraphicCard = new HashSet<String>();



        for(Laptop laptop : allLaptops) {
            String manufacturer = laptop.getManufacturer();
            String screenSize = laptop.getScreenSize();
            String physicalCore = String.valueOf(laptop.getProcessorPhysicalCores());
            String touchScreen = String.valueOf(laptop.getScreenTouchscreen());
            String ramValue = laptop.getRam();
            String processorValue = laptop.getProcessorName();
            String graphicCardValue = laptop.getGraphicCardName();

            if(manufacturer != "null" && manufacturer != null && manufacturer.length() > 0) {
                manufacturers.add(manufacturer);
            }
            if(screenSize != "null" && screenSize != null && screenSize.length() > 0) {
                screenSizes.add(screenSize);
            }

            if(physicalCore != "null" && physicalCore != null && physicalCore.length() > 0) {
                allPhysicalCore.add(physicalCore);
            }
            if(touchScreen != "null" && touchScreen != null && touchScreen.length() > 0) {
                allTouchscreen.add(touchScreen);
            }
            if(ramValue != "null" && ramValue != null && ramValue.length() > 0) {
                allRam.add(ramValue);
            }
            if(processorValue != "null" && processorValue != null && processorValue.length() > 0) {
                allProcessor.add(processorValue);
            }
            if(graphicCardValue != "null" && graphicCardValue != null && graphicCardValue.length() > 0) {
                allGraphicCard.add(graphicCardValue);
            }

            firstArea = new JComboBox(manufacturers.toArray());
            firstArea.addItem("");
            secondArea = new JComboBox(screenSizes.toArray());
            secondArea.addItem("");

            physicalCores = new JComboBox(allPhysicalCore.toArray());
            touchscreen = new JComboBox(allTouchscreen.toArray());
            ram = new JComboBox(allRam.toArray());
            processor = new JComboBox(allProcessor.toArray());
            graphicCard = new JComboBox(allGraphicCard.toArray());

            physicalCores.addItem("");
            touchscreen.addItem("");
            ram.addItem("");
            processor.addItem("");
            graphicCard.addItem("");


        }
    }

    public void setDefaultValue(JComboBox comboBox) {
        if(comboBox == null || comboBox.getItemCount() == 0) {

        }else {
            comboBox.setSelectedIndex(0);
        }
    }


}