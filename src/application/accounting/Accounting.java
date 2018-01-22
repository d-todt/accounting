package application.accounting;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;

public class Accounting {
    public String applicationVersion = "Id: Accounting.java, version c4951d2 of 2017-12-04 16:27:41 +0100 by se110512";

    /** liest eine CSV-Datei ein und gibt ihren Inhalt als Depositor-List zurueck */
    public static List<Depositor> liesDatei(String dateiname) throws IOException {
        List<Depositor> erg = new ArrayList<Depositor>();
        
        // Datei einlesen
        try (BufferedReader br = new BufferedReader(new FileReader(dateiname))) {
            // Parsen
            String zeile;
            while ((zeile = br.readLine()) != null) {
                zeile = zeile.trim();
                if (zeile.length() == 0 || zeile.charAt(0) == '#') {
                    System.out.println(zeile);
                    continue;
                }
                
                String feld[] = zeile.split(";");
                Depositor tmp = new Depositor(feld[0], feld[1], feld[2], new BigDecimal(feld[3].replace(",",".")), new ArrayList<>());
                for (int i = 4; i < feld.length; i += 2) {
                    tmp.einzahlen(Integer.parseInt(feld[i]), new BigDecimal(feld[i+1].replace(",",".")));
                }
                erg.add(tmp);
            }
        } catch (IOException e) {
            System.out.println("Ein-/Ausgabefehler (Datei " + dateiname + ")");
            throw e;
        }
        
        return erg;
    }

    public static void main(String args[]) throws IOException {
        // ArgParser
        String dateiname, ausgabedateiname = "", log = "";
        BigDecimal zinssatz;
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
            zinssatz = new BigDecimal(ap.getNonOptions());
        }
        
        // Daten einlesen
        Depositor.setzeZinsen(zinssatz);
        List<Depositor> leute = liesDatei(dateiname);
        
        for (Depositor mensch : leute) {
            System.out.printf("%s;%s;%s;%s\n", mensch.getNummer(), mensch.getNachname(), mensch.getVorname(), Math.round(mensch.berechneGuthaben().doubleValue()*100)/100.);
        }
    }
}
