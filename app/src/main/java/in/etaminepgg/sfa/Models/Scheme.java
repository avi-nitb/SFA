package in.etaminepgg.sfa.Models;

/**
 * Created by etamine on 29/8/17.
 */

public class Scheme
{
    private String ID;
    private String name;
    private String description;
    private String createdDate;
    private String startDate;
    private String endDate;
    private String isViewed;

    public Scheme(String ID, String name, String description, String startDate, String endDate)
    {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(String createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getIsViewed()
    {
        return isViewed;
    }

    public void setIsViewed(String isViewed)
    {
        this.isViewed = isViewed;
    }
}
