/************************************************************************
                        Watch Directory and
                          Random generator
                                                Jay Bhatt
************************************************************************/
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DirWatch
{
    FsController writeNotify = new FsController();
    public void callFcn()
    {
        writeNotify.notifyWrite();
    }
    public int getNum(int min, int max)
    {
        return (int) ((Math.random() * (max - min)) + min);
    }
   public static void main(String[] args)
   {
        String dir = System.getProperty("user.dir") + File.separator + "data" + File.separator;
        String readingPath = System.getProperty("user.dir") + File.separator + "envData.txt";
        Path path = Paths.get(dir);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH-mm-ss");
        LocalTime now = LocalTime.now();
        try
        {
            WatchService watcher = path.getFileSystem().newWatchService();
            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey watchKey = watcher.take();
            List<WatchEvent<?>> events = watchKey.pollEvents();
            for (WatchEvent event : events)
            {
                if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                {
                    System.out.println("Created: " + event.context().toString());
                    DirWatch wch = new DirWatch();
                    String reading = event.context().toString() + "::::" + wch.getNum(-90, 60) +"::"+ wch.getNum(0, 100) +"::"+ format.format(now);
                    FileWriter writer = new FileWriter(readingPath, true);
                    PrintWriter out = new PrintWriter(writer);
                    out.println(reading);
                    writer.close();
                    wch.callFcn();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
   }
}
