package application.accounting;

import java.util.List;

public class Depositor {
    private String nummer, nachname, vorname;
    private long guthaben;
    private List<AccountingEntry> einzahlungen;
    private static double zinsen;
    
    public Depositor(String nummer, String nachname, String vorname, long startguthaben, List<AccountingEntry> einzahlungen) {
        this.nummer = nummer;
        this.nachname = nachname;
        this.vorname = vorname;
        this.guthaben = startguthaben;
        this.einzahlungen = einzahlungen;
    }
    
    /** setzt den Zinssatz fuer alle Mitglieder */
    public static void setzeZinsen(double zinssatz) {
        zinsen = zinssatz;
    }
    
    /** gibt den Betrag des AccountingEntry mit Zinsen zurueck */
    public static long verrechneZinsen(AccountingEntry entry) {
        return verrechneZinsen(entry.getBetrag(), entry.getTag());
    }
    
    /** gibt den uebergebenen Betrag mit Zinsen zurueck */
    public static long verrechneZinsen(long betrag, int tag) {
        return (long) (betrag * (zinsen/100*(360-tag)/360+1));
    }
    
    /** fuegt dem Mitglied eine Einzahlung hinzu */
    public void einzahlen(int tag, long betrag) {
        einzahlungen.add(new AccountingEntry(tag, betrag));
    }
    
    /** berechnet das endgueltige Guthaben als double */
    public double berechneGuthaben() {
        guthaben = verrechneZinsen(guthaben, 0);
        while (!einzahlungen.isEmpty()) {
            AccountingEntry tmp = einzahlungen.get(0);
            guthaben += verrechneZinsen(tmp);
            einzahlungen.remove(0);
        }
        return Math.round(guthaben/1000.)/100.;
    }
    
    public String getNummer() { return nummer; }
    
    public String getNachname() { return nachname; }
    
    public String getVorname() { return vorname; }
}
