package in.etaminepgg.sfa.Models;

import java.util.List;

public class BasicConfigModel
{
    private final List<AppData> app_data;
    private int api_status;

    public BasicConfigModel(int api_status, List<AppData> app_data)
    {
        this.api_status = api_status;
        this.app_data = app_data;
    }

    public int getApi_status()
    {
        return api_status;
    }

    public List<AppData> getApp_data()
    {
        return app_data;
    }

    public static class AppData
    {
        private final String propname;

        private final String propval;

        private final String app_version;

        public AppData(String propname, String propval, String app_version)
        {
            this.propname = propname;
            this.propval = propval;
            this.app_version = app_version;
        }

        public String getPropname()
        {
            return propname;
        }

        public String getPropval()
        {
            return propval;
        }

        public String getApp_version()
        {
            return app_version;
        }
    }
}
