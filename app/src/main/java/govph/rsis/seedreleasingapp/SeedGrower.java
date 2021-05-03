package govph.rsis.seedreleasingapp;

public class SeedGrower {

    public String fullname;
    public String orderId;
    public int status;

    public SeedGrower() {

    }

    public SeedGrower(String fullname,String orderId,int status) {
        this.fullname = fullname;
        this.orderId = orderId;
        this.status = status;
    }

    public String getFullname() {return fullname;}
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getOrderId() {return orderId;}
    public void setOrderId(String orderId) {this.orderId = orderId;}
    public int getStatus() {return status;}
    public void setStatus(int status){this.status = status;}
}
