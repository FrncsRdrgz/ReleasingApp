package govph.rsis.seedreleasingapp;

public class DecVar {
    public static final String receiver() {

        String production = "https://rsis.philrice.gov.ph/seed_ordering/api";
        String staging = "https://stagingdev.philrice.gov.ph/rsis/seed_ordering/api";
        String training = "https://rsistraining.philrice.gov.ph/seed_ordering/api";
        String localhost = "http://192.168.0.5/seed_ordering/api";

        return production;
    }
}