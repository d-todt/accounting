package application.accounting;

import java.util.*;
import java.io.*;

public class Accounting {
    /** wandelt einen Betrag im Stringformat in den entsprechenden Long um */
    public static long parseBetrag(String betr) {
        return (long) (Double.parseDouble(betr.replace(",", "."))*100*1000);
    }

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
                Depositor tmp = new Depositor(feld[0], feld[1], feld[2], parseBetrag(feld[3]), new ArrayList<>());
                for (int i = 4; i < feld.length; i += 2) {
                    tmp.einzahlen(Integer.parseInt(feld[i]), parseBetrag(feld[i+1]));
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
        String dateiname, ausgabedateiname = "";
        double zinssatz;
        if (args.length == 0) {
            // Dateinamen und Zinssatz einlesen
            Scanner sc = new Scanner(System.in);
            dateiname = sc.nextLine();
            zinssatz = sc.nextDouble();
            sc.close();
        } else {
            ArgParser ap = new ArgParser(args);
            dateiname = ap.getInputFilename();
            ausgabedateiname = ap.getOutputFilename();
            zinssatz = Double.parseDouble(ap.getNonOptions());
        }
        
        // Daten einlesen
        if (ausgabedateiname.length() > 0) {
            System.setOut(new PrintStream(new FileOutputStream(ausgabedateiname)));
        }
        Depositor.setzeZinsen(zinssatz);
        List<Depositor> leute = liesDatei(dateiname);
        for (Depositor mensch : leute) {
            System.out.printf("%s;%s;%s;%s\n", mensch.getNummer(), mensch.getNachname(), mensch.getVorname(), (mensch.berechneGuthaben()+"").replace(".", ","));
        }
    }
}
