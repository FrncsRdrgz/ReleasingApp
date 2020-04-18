package govph.rsis.releasingapp;

public class Seed {
    public String variety;
    public String palletName;
    public String quantity;

    public Seed() {

    }

    public Seed(String variety,String palletName, String quantity) {
        this.variety = variety;
        this.palletName = palletName;
        this.quantity = quantity;
    }

    public String getVariety() {
        return variety;
    }
    public void setVariety(String variety) {
        this.variety = variety;
    }
    public String getpalletName() {
        return palletName;
    }
    public void setpalletName(String palletName) {
        this.palletName = palletName;
    }
    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
