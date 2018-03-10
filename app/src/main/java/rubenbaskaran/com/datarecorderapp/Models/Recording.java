package rubenbaskaran.com.datarecorderapp.Models;

/**
 * Created by Ruben on 14-07-2017.
 */

public class Recording
{
    public String getFilepath()
    {
        return filepath;
    }

    public String getTitle()
    {
        return title;
    }

    public String getLength()
    {
        return length;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setFilepath(String filepath)
    {
        this.filepath = filepath;
    }

    public void setLength(String length)
    {
        this.length = length;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    private int id;
    private String title;
    private String length;
    private String timestamp;
    private String filepath;


    public Recording()
    {
    }

    public Recording(int Id, String filepath, String title, String length, String timestamp)
    {
        this.id = Id;
        this.filepath = filepath;
        this.title = title;
        this.length = length;
        this.timestamp = timestamp;
    }
}
