package govph.rsis.seedreleasingapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Seed {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sId")
    public int sId;

    @ColumnInfo(name = "orderId")
    public String orderId;

    @ColumnInfo(name = "variety")
    public String variety;

    @ColumnInfo(name="pallet_code")
    public String pallet_code;

    @ColumnInfo(name = "quantity")
    public String quantity;

    @ColumnInfo(name = "compare_quantity")
    public String compare_quantity;

    @ColumnInfo(name="lotCode")
    public String lotCode;

    @ColumnInfo(name="authTag")
    public String authTag;

    @ColumnInfo(name="isVerified")
    public String verified;

    @ColumnInfo(name="isReleased")
    public String isReleased;


    /*public Seed() {

    }*/

    /*public Seed(String variety,String pallet_code, String quantity,String lotCode) {
        this.variety = variety;
        this.pallet_code = pallet_code;
        this.quantity = quantity;
        this.lotCode = lotCode;
    }*/

    public String getOrderId(){ return orderId;}
    public void setOrderId(String orderId){this.orderId = orderId;}
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
    public String getVerified() { return verified; }
    public void setVerified(String verified){this.verified = verified;}
    public String getAuthTag() { return authTag; }
    public void setAuthTag(String authTag) { this.authTag = authTag; }

    public String getCompare_quantity() { return compare_quantity; }
    public void setCompare_quantity(String compare_quantity) { this.compare_quantity = compare_quantity; }

    public String getIsReleased(){return isReleased;}
    public void setIsReleased(String isReleased){ this.isReleased = isReleased;}

}
