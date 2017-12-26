package in.etaminepgg.sfa.InputModel_For_Network;

/**
 * Created by Jayattama Prusty on 23-Dec-17.
 */

public class IM_RetailerInfo {

    private  String authToken;
    private  String retailer_id;

    public IM_RetailerInfo(String authToken, String retailer_id) {
        this.authToken = authToken;
        this.retailer_id = retailer_id;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRetailer_id() {
        return retailer_id;
    }

    public void setRetailer_id(String retailer_id) {
        this.retailer_id = retailer_id;
    }
}
