package in.etaminepgg.sfa.InputModel_For_Network;

/**
 * Created by Jaya on 21-12-2017.
 */

public class IM_Config
{

   /* {"IMEI": "erdddddddddddasdasdasdasdasdasdw10","mobile_app_version":"0.0.1"}*/
   private  String IMEI;
   private  String mobile_app_version;

    public IM_Config(String IMEI, String mobile_app_version)
    {
        this.IMEI = IMEI;
        this.mobile_app_version = mobile_app_version;
    }

    public String getIMEI()
    {
        return IMEI;
    }

    public void setIMEI(String IMEI)
    {
        this.IMEI = IMEI;
    }

    public String getMobile_app_version()
    {
        return mobile_app_version;
    }

    public void setMobile_app_version(String mobile_app_version)
    {
        this.mobile_app_version = mobile_app_version;
    }
}
