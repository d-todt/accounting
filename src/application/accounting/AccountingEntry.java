package application.accounting;

import java.math.BigDecimal;

public class AccountingEntry {
    private int tag;
    private BigDecimal betrag;
    
    public AccountingEntry(int tag, BigDecimal betrag) {
        this.tag = tag;
        this.betrag = betrag;
    }
    
    public int getTag() { return tag; }
    
    public BigDecimal getBetrag() { return betrag; }
}
