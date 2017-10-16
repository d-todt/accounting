package application.accounting;

public class AccountingEntry {
    private int tag;
    private long betrag;
    
    public AccountingEntry(int tag, long betrag) {
        this.tag = tag;
        this.betrag = betrag;
    }
    
    public int getTag() { return tag; }
    
    public long getBetrag() { return betrag; }
}
