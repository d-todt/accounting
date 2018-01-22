package application.accounting;

import java.util.List;
import java.math.BigDecimal;

public class Depositor {
    private String nummer, nachname, vorname;
    private BigDecimal guthaben;
    private List<AccountingEntry> einzahlungen;
    private static BigDecimal zinsen;
    
    public Depositor(String nummer, String nachname, String vorname, BigDecimal startguthaben, List<AccountingEntry> einzahlungen) {
        this.nummer = nummer;
        this.nachname = nachname;
        this.vorname = vorname;
        this.guthaben = startguthaben;
        this.einzahlungen = einzahlungen;
    }
    
    /** setzt den Zinssatz fuer alle Mitglieder */
    public static void setzeZinsen(BigDecimal zinssatz) {
        zinsen = zinssatz;
    }
    
    /** gibt den Betrag des AccountingEntry mit Zinsen zurueck */
    public static BigDecimal verrechneZinsen(AccountingEntry entry) {
        return verrechneZinsen(entry.getBetrag(), entry.getTag());
    }
    
    /** gibt den uebergebenen Betrag mit Zinsen zurueck */
    public static BigDecimal verrechneZinsen(BigDecimal betrag, int tag) {
        //return betrag.multiply(zinsen.divide(new BigDecimal(100)).multiply(new BigDecimal(360-tag)).divide(new BigDecimal(360)).add(new BigDecimal(1)));
        return betrag.multiply(new BigDecimal(zinsen.divide(new BigDecimal(100)).doubleValue()*(360.-tag)/360+1));
    }
    
    /** fuegt dem Mitglied eine Einzahlung hinzu */
    public void einzahlen(int tag, BigDecimal betrag) {
        einzahlungen.add(new AccountingEntry(tag, betrag));
    }
    
    /** berechnet das endgueltige Guthaben als BigDecimal */
    public BigDecimal berechneGuthaben() {
        guthaben = verrechneZinsen(guthaben, 0);
        while (!einzahlungen.isEmpty()) {
            AccountingEntry tmp = einzahlungen.get(0);
            guthaben = guthaben.add(verrechneZinsen(tmp));
            einzahlungen.remove(0);
        }
        return guthaben;
    }
    
    public String getNummer() { return nummer; }
    
    public String getNachname() { return nachname; }
    
    public String getVorname() { return vorname; }
}
