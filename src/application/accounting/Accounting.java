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
import java.lang.NumberFormatException;
import java.math.BigDecimal;
import java.math.MathContext;

public class Accounting {
    public String applicationVersion = "Id: Accounting.java, version c4951d2 of 2017-12-04 16:27:41 +0100 by se110512";
    /** Logger */
    private static final Logger logger = Logger.getLogger(Accounting.class.getName());
    
    /** Resource Bundle */
    private static String baseName = "Accounting";
    private static ResourceBundle rb = ResourceBundle.getBundle(baseName);
    
    /** zur Fehlerbehandlung */
    private static void beende(String msg) {
        logger.warning(msg);
        System.out.println("Ausfuehrung beendet (siehe Logdatei):");
        System.out.println(msg);
        System.exit(1);
    }

    /** wandelt einen Betrag im Stringformat in den entsprechenden Long um */
    public static long parseBetrag(String betr) {
        try {
            return (long) (Double.parseDouble(betr.replace(",", "."))*100*1000);
        } catch (NumberFormatException e) {
            beende("Fehlerhaftes Format im Betrag: " + betr);
        }
        return 0;
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
                if (feld[1].length() == 0 || feld[2].length() == 0) {
                    beende("Namensangabe fehlt (gefunden: " + feld[1] + " " + feld[2] + ")");
                } else if (feld[0].length() != 6) {
                    beende("Ungueltige ID: " + feld[0]);
                } else if (feld[3].length() == 0) {
                    beende("Fehlende Angabe bei " + feld[1] + " " + feld[2]);
                }
                Depositor tmp = null;
                try {
                    tmp = new Depositor(feld[0], feld[1], feld[2], new BigDecimal(feld[3].replace(",",".")), new ArrayList<>());
                } catch (Exception e) {
                    beende("Illegaler Betrag: " + feld[3]);
                }
                
                for (int i = 4; i < feld.length; i += 2) {
                    if (feld[i].length() == 0 || feld[i+1].length() == 0) {
                        beende("Fehlende Angabe bei " + feld[1] + " " + feld[2]);
                    }
                    tmp.einzahlen(Integer.parseInt(feld[i]), new BigDecimal(feld[i+1].replace(",",".")));
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
        BigDecimal zinssatz = null;
        if (args.length == 0) {
            // Dateinamen und Zinssatz einlesen
            Scanner sc = new Scanner(System.in);
            dateiname = sc.nextLine();
            zinssatz = new BigDecimal(sc.nextLine());
            sc.close();
        } else {
            ArgParser ap = new ArgParser(args);
            log = ap.getLogFilename();
            dateiname = ap.getInputFilename();
            ausgabedateiname = ap.getOutputFilename();
            try {
                zinssatz = new BigDecimal(ap.getNonOptions());
            } catch (Exception e) {
                beende("Illegaler Zinssatz: " + ap.getNonOptions());
            }
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
            System.out.printf("%s;%s;%s;%s\n", mensch.getNummer(), mensch.getNachname(), mensch.getVorname(), Math.round(mensch.berechneGuthaben().doubleValue()*100)/100.);
        }
    }
}
