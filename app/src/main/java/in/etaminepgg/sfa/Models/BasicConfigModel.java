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
        private final String parameter_name;

        private final String parameter_value;

        private final String description;


        public AppData(String propname, String propval, String description)
        {
            this.parameter_name = propname;
            this.parameter_value = propval;
            this.description = description;
        }

        public String getParameter_name()
        {
            return parameter_name;
        }

        public String getParameter_value()
        {
            return parameter_value;
        }

        public String getDescription()
        {
            return description;
        }


    }
}
