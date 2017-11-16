package application.accounting;

import java.util.*;
import java.io.*;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Accounting {
    /** Logger */
    private static final Logger logger = Logger.getLogger(Accounting.class.getName());
    
    /** Resource Bundle */
    private static String baseName = "Accounting";
    private static ResourceBundle rb = ResourceBundle.getBundle(baseName);

    /** wandelt einen Betrag im Stringformat in den entsprechenden Long um */
    public static long parseBetrag(String betr) {
        return (long) (Double.parseDouble(betr.replace(",", "."))*100*1000);
    }

    /** liest eine CSV-Datei ein und gibt ihren Inhalt als Depositor-List zurueck */
    public static List<Depositor> liesDatei(String dateiname) throws IOException {
        List<Depositor> erg = new ArrayList<Depositor>();
        
        // Datei einlesen
        try (BufferedReader br = new BufferedReader(new FileReader(dateiname))) {
            String readinput_msg = rb.getString("readinput_msg");
            logger.info(readinput_msg + ": " + dateiname);
            // Parsen
            String zeile;
            while ((zeile = br.readLine()) != null) {
                zeile = zeile.trim();
                logger.info("Gelesene Zeile: " + zeile);
                if (zeile.length() == 0 || zeile.charAt(0) == '#') {
                    System.out.println(zeile);
                    continue;
                }
                
                String feld[] = zeile.split(";");
                Depositor tmp = new Depositor(feld[0], feld[1], feld[2], parseBetrag(feld[3]), new ArrayList<>());
                for (int i = 4; i < feld.length; i += 2) {
                    tmp.einzahlen(Integer.parseInt(feld[i]), parseBetrag(feld[i+1]));
                }
                erg.add(tmp);
            }
        } catch (IOException e) {
            logger.warning("Ein-/Ausgabefehler (Datei " + dateiname + ")");
            throw e;
        }
        
        return erg;
    }

    public static void main(String args[]) throws IOException {
        // Nochmal Resource Bundle?
        File file = new File("./dist/data/lang/");
        rb = null;
        try {
            URL[] urls = {file.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            rb = ResourceBundle.getBundle(baseName, Locale.getDefault(), loader);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    
        // ArgParser
        String dateiname, ausgabedateiname = "", log = "";
        double zinssatz;
        if (args.length == 0) {
            // Dateinamen und Zinssatz einlesen
            Scanner sc = new Scanner(System.in);
            dateiname = sc.nextLine();
            zinssatz = sc.nextDouble();
            sc.close();
        } else {
            ArgParser ap = new ArgParser(args);
            log = ap.getLogFilename();
            dateiname = ap.getInputFilename();
            ausgabedateiname = ap.getOutputFilename();
            zinssatz = Double.parseDouble(ap.getNonOptions());
        }
        
        // Logger wird aktiviert
        if (log != null && log.length() > 0) {
            try {
                boolean append = true;
                FileHandler fh = new FileHandler(log, append);
                fh.setFormatter(new Formatter() {
                    public String format(LogRecord rec) {
                        StringBuffer buf = new StringBuffer(1000);
                        buf.append(new java.util.Date()).append(' ');
                        buf.append(rec.getLevel()).append(' ');
                        buf.append(formatMessage(rec)).append('\n');
                        return buf.toString();
                    }
                });
                logger.addHandler(fh);
                logger.setLevel(Level.ALL);
            } catch (IOException e) {
                logger.severe("Datei kann nicht geschrieben werden");
                e.printStackTrace();
            }
        }
        
        // Daten einlesen
        if (ausgabedateiname != null && ausgabedateiname.length() > 0) {
            System.setOut(new PrintStream(new FileOutputStream(ausgabedateiname)));
            logger.info("Lenke Ausgabe in Datei " + ausgabedateiname + " um");
        }
        Depositor.setzeZinsen(zinssatz);
        logger.info("Setze Zinssatz auf " + zinssatz);
        List<Depositor> leute = liesDatei(dateiname);
        for (Depositor mensch : leute) {
            System.out.printf("%s;%s;%s;%s\n", mensch.getNummer(), mensch.getNachname(), mensch.getVorname(), (mensch.berechneGuthaben()+"").replace(".", ","));
        }
    }
}
