package govph.rsis.releasingapp;

public class Seed {
    public String variety;
    public String pallet_code;
    public String quantity;
    public String lotCode;
    /*public Seed() {

    }*/

    /*public Seed(String variety,String pallet_code, String quantity,String lotCode) {
        this.variety = variety;
        this.pallet_code = pallet_code;
        this.quantity = quantity;
        this.lotCode = lotCode;
    }*/

    public String getVariety() {
        return variety;
    }
    public void setVariety(String variety) {
        this.variety = variety;
    }
    public String getPallet_code() {
        return pallet_code;
    }
    public void setPallet_code(String pallet_code) {
        this.pallet_code = pallet_code;
    }
    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public String getLotCode() { return lotCode; }
    public void setLotCode(String lotCode){this.lotCode = lotCode;}
}
