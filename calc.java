import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class calc extends DefaultHandler {
    private static final String CLASS_NAME = calc.class.getName();
    private final static Logger LOG = Logger.getLogger(CLASS_NAME);

    private SAXParser parser = null;
    private SAXParserFactory spf;

    private double totalSales;

    private double totalSal;

    private boolean inSales;

    private String currentElement;
    private String id;
    private String name;
    private String lastName;
    private String sales;
    private String state;
    private String dept;

    private String keyword;

    private HashMap<String, Double> subtotales, stotal;

    public calc() {
        super();
        spf = SAXParserFactory.newInstance();
        // verificar espacios de nombre
        spf.setNamespaceAware(true);
        // validar que el documento este bien formado (well formed)
        spf.setValidating(true);

        subtotales = new HashMap<>();
        stotal = new HashMap<>();
    }

    private void process(File file) {
        try {
            // obtener un parser para verificar el documento
            parser = spf.newSAXParser();

        } catch (SAXException | ParserConfigurationException e) {
            LOG.severe(e.getMessage());
            System.exit(1);
        }
        System.out.println("\nStarting parsing of " + file + "\n");
        try {
            // iniciar analisis del documento
            keyword = state;
            parser.parse(file, this);
        } catch (IOException | SAXException e) {
            LOG.severe(e.getMessage());
        }
    }

    @Override
    public void startDocument() throws SAXException {
        // al inicio del documento inicializar
        // las ventas totales
        totalSales = 0.0;
        totalSal = 0.0;
    }

    @Override
    public void endDocument() throws SAXException {
        // Se proceso todo el documento, imprimir resultado
        Set<Map.Entry<String,Double>> entries = subtotales.entrySet();
        for (Map.Entry<String,Double> entry: entries) {
            System.out.printf("%-15.15s $%,9.2f\n",entry.getKey(),entry.getValue());
        }
        System.out.println("");
        Set<Map.Entry<String,Double>> entries2 = stotal.entrySet();
        for (Map.Entry<String,Double> entry: entries2) {
            System.out.printf("%-15.15s $%,9.2f\n",entry.getKey(),entry.getValue());
        }
        System.out.println("");
        System.out.printf("Ventas totales por departamento: $%,9.2f\n",totalSales);
        System.out.println("");
        //totalSal
        System.out.printf("Ventas totales por estado: $%,9.2f\n",totalSal);

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (localName.equals("sale_record")) {
            inSales = true;
        }
        currentElement = localName;
    }

    @Override
    public void characters(char[] bytes, int start, int length) throws SAXException {

        switch (currentElement) {
            case "id":
                this.id = new String(bytes, start, length);
                break;
            case "first_name":
                this.name = new String(bytes, start, length);
                break;
            case "last_name":
                this.lastName = new String(bytes, start, length);
                break;
            case "sales":
                this.sales = new String(bytes, start, length);
                break;
            case "state":
                this.state = new String(bytes, start, length);
                break;
            case "department":
                this.dept = new String(bytes, start, length);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ( localName.equals("sale_record") ) {
            double val = 0.0;
            double val2 = 0.0;
            try {
                val = Double.parseDouble(this.sales);
            } catch (NumberFormatException e) {
                LOG.severe(e.getMessage());
            }
            try {
                val2 = Double.parseDouble(this.sales);
            } catch (NumberFormatException e) {
                LOG.severe(e.getMessage());
            }

            if ( subtotales.containsKey( this.dept ) ) {
                double sum = subtotales.get( this.dept );
                subtotales.put( this.dept, sum + val );
            } else {
                subtotales.put(this.dept, val );
            }
            totalSales = totalSales + val;
            inSales = false;

            //stotal

            if ( stotal.containsKey( this.state ) ) {
                double suma = stotal.get( this.state );
                stotal.put( this.state, suma + val2 );
            } else {
                stotal.put(this.state, val2 );
            }
            totalSal = totalSal + val2;
            inSales = false;


        }
    }

    private void printRecord() {
        System.out.printf("%4.4s %-10.10s %-10.10s %9.9s %-10.10s %-15.15s\n",
                id, name, lastName, sales, state, dept);
    }



    public static void main(String args[]) {
        if (args.length == 0) {
            LOG.severe("No file to process. Usage is:" + "\njava calc <keyword>");
            return;
        }
        File xmlFile = new File(args[0] );
        calc handler = new calc();
        handler.process( xmlFile );
    }

}
