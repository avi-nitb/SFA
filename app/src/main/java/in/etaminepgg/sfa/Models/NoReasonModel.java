package in.etaminepgg.sfa.Models;

/**
 * Created by etamine on 19/2/18.
 */

public class NoReasonModel
{

    private String reason_id;
    private String reason_desc;
    private boolean ischecked;

    public NoReasonModel(String reason_id, String reason_desc, boolean ischecked)
    {
        this.reason_id = reason_id;
        this.reason_desc = reason_desc;
        this.ischecked = ischecked;
    }

    public String getReason_id()
    {
        return reason_id;
    }

    public void setReason_id(String reason_id)
    {
        this.reason_id = reason_id;
    }

    public String getReason_desc()
    {
        return reason_desc;
    }

    public void setReason_desc(String reason_desc)
    {
        this.reason_desc = reason_desc;
    }

    public boolean isIschecked()
    {
        return ischecked;
    }

    public void setIschecked(boolean ischecked)
    {
        this.ischecked = ischecked;
    }
}
