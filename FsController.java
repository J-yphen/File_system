/************************************************************************
                        Virtual File System 
                        simulator for small
                           embedded device
                                                Jay Bhatt
************************************************************************/
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


class FileOfFs
{
    String filename;
    long start, end;
}
class ConcurrentWork extends Thread
{
    private String method;
    public ConcurrentWork(String type)
    {
        method = type;
    }
    public void run()
    {
        if(method.equals("write"))
        {
            FsController wrt_in_File = new FsController();
            try
            {
                wrt_in_File.writeData();
            }
            catch(IOException e)
            {
                System.out.println("ERROR : " + e);
            }
        }
        else if(method.equals("dirWatch"))
        {
            String[] temp = "calling".split("");
            DirWatch.main(temp);
        }
    }
}
class FAT_table
{
    static long ptr = 4*1024, readptr = 4*1024;
    static String[] listFiles;
    public String[] listFile(File disk) throws IOException
    {
        ArrayList<String> list = new ArrayList<>();
        RandomAccessFile fout = new RandomAccessFile(disk, "r");
        fout.seek(readptr);

        while(fout.getFilePointer() < 4*1024*10)
        {
            final String lineFromFile = fout.readLine();
            Scanner match = new Scanner(lineFromFile);
            if(!lineFromFile.contains("::"))
                continue;
            match.useDelimiter("::");
            String temp = match.next();
            if(!isNumeric(temp))
                list.add(temp);
            match.close();   
        }
        fout.close();
        listFiles = list.toArray(new String[0]);
        return listFiles;
    }
    public void writeIn(String filename, File disk, long start) throws IOException
    {
        FileOfFs file = new FileOfFs();
        RandomAccessFile fout = new RandomAccessFile(disk, "rw");
        fout.seek(ptr);
        file.filename = filename;
        file.start = start;
        file.end = start + 4*1024;
        String data = filename +"::"+ file.start +"::"+ file.end +"\n";
        ptr += data.length();
        fout.write(data.getBytes());
        fout.close();
    }
    public long readOut(String filename, File disk) throws IOException
    {
        RandomAccessFile fout = new RandomAccessFile(disk, "r");
        fout.seek(readptr);

        while(fout.getFilePointer() < 4*1024*10)
        {
            final String lineFromFile = fout.readLine();
            Scanner match = new Scanner(lineFromFile);
            if(!lineFromFile.contains("::"))
                continue;
            match.useDelimiter("::");
            if(filename.equals(match.next()))
            {
                long startloc = Long.parseLong(match.next());
                match.close();
                fout.close();
                return startloc;
            }
            match.close();   
        }
        fout.close();
        return 0;
    }
    public void removeRef(String filename, File disk) throws IOException
    {
        RandomAccessFile fout = new RandomAccessFile(disk, "rw");
        fout.seek(readptr);

        while(fout.getFilePointer() < 4*1024*10)
        {
            String lineFromFile = fout.readLine();
            Scanner match = new Scanner(lineFromFile);
            if(!lineFromFile.contains("::"))
                continue;
            match.useDelimiter("::");
            if(filename.equals(match.next()))
            {
                fout.seek(fout.getFilePointer() - lineFromFile.length() - 1);
                byte[] nullBytes = new byte[lineFromFile.length()];
                fout.write(nullBytes, 0, lineFromFile.length());
                match.close();
                fout.close();
                return;
            }
            match.close();
        }
        fout.close();
    }
    public boolean isNumeric(String str)
    { 
        try
        {  
            Double.parseDouble(str);  
            return true;
        }
        catch(NumberFormatException e)
        {  
            return false;  
        }  
    }
}

class FsController
{
    String path = System.getProperty("user.dir") + File.separator + "data" + File.separator;
    File dir = new File(path);
    File disk = new File(System.getProperty("user.dir") + File.separator + "filesys.dat");
    String readingPath = System.getProperty("user.dir") + File.separator + "envData.txt";
    String outImg = System.getProperty("user.dir") + File.separator + "CapturedImg";
    FAT_table tab = new FAT_table();
    ImageCompression compressed = new ImageCompression();
    Enc_Denc textRead = new Enc_Denc();
    ConcurrentWork wrtthrd, wththrd, readthrd, ntfythrd;

    static long ptr = 4*1024*10, rdptr = 4*1024*10;
    public String envReading(String filename) throws IOException
    {
        FileInputStream fis = new FileInputStream(readingPath);
        Scanner sc = new Scanner(fis); 
        while (sc.hasNextLine())
        {
            final String lineFromFile = sc.nextLine();
            Scanner match = new Scanner(lineFromFile);
            match.useDelimiter("::::");
            if(filename.equals(match.next()))
            {
                String reading = match.next();
                match.close();
                sc.close();
                return reading;
            }
            match.close();
        } 
        sc.close();
        return "";
    }
    public void writeData() throws IOException
    {
        if (dir.isDirectory())
        {
            for (final File f : dir.listFiles())
            {
                String filePath = path + File.separator + f.getName();
                byte[] data = compressed.getByteByPic(filePath);
                data = compressed.compressUnderSize(data, 3900);

                RandomAccessFile fout = new RandomAccessFile(disk, "rw");
                fout.seek(ptr);
                tab.writeIn(f.getName(), disk, ptr);
                fout.write(data);
                ptr += 3900;
                fout.seek(ptr);
                String reading = textRead.encrypt(envReading(f.getName()), f.getName());
                fout.write(reading.getBytes());
                ptr +=196;
                fout.close();
            }
        }
    }
    public void readData(String filename) throws IOException
    {
        RandomAccessFile fout = new RandomAccessFile(disk, "r");
        rdptr = tab.readOut(filename, disk);
        fout.seek(rdptr);

        byte[] data = new byte[3900];
        fout.read(data);
        compressed.byte2image(data, outImg + File.separator + filename);
        fout.close();
        getReadingData(filename);
    }
    public void rmRef(String filename, String mode) throws IOException
    {
        if(mode.equals("write"))
        {
            RandomAccessFile fout = new RandomAccessFile(disk, "rw");
            rdptr = tab.readOut(filename, disk);
            fout.seek(rdptr);
            byte[] nullBytes = new byte[4*1024];
            fout.write(nullBytes);
            fout.close();
        }
        tab.removeRef(filename, disk);
    }
    public String[] getFilelist()
    {
        try
        {
            return tab.listFile(disk);
        }
        catch(IOException e)
        {
            System.out.println("ERROR : " + e);
        }
        return null;
    }
    public void getReadingData(String filename) throws IOException
    {
        RandomAccessFile fout = new RandomAccessFile(disk, "r");
        rdptr = tab.readOut(filename, disk);
        fout.seek(rdptr + 3900);
        byte[] reading = new byte[24];
        fout.read(reading);
        String temp = new String(reading);
        String data = textRead.decrypt(temp, filename);
        fout.close();

        if(data.contains("::") && !data.isBlank())
        {
            StringTokenizer match = new StringTokenizer(data, "::");
            String dataReading = "Image :- "+ filename+", Temperature :- "+match.nextToken()+", Humidity :- "+match.nextToken()+", Time :- "+match.nextToken().replace("-", " : ");
            DisplayData.filename = outImg + File.separator + filename;
            DisplayData.data = dataReading;
            DisplayData.main();
        }
    }
    public void storePtrValues() throws IOException
    {
        System.out.println("Storing disk state...");
        RandomAccessFile fout = new RandomAccessFile(disk, "rw");
        fout.seek(0);
        byte[] ptrData = (ptr + "::" + FAT_table.ptr).getBytes();
        fout.write(ptrData);
        fout.close();
    }
    public void retrieveData() throws IOException
    {
        System.out.println("Retrieving disk state...");
        RandomAccessFile fout = new RandomAccessFile(disk, "rw");
        fout.seek(0);
        byte[] prevData = new byte[11];
        fout.read(prevData);
        String data = new String(prevData);
        if(data.contains("::") && !data.isBlank())
        {
            StringTokenizer match = new StringTokenizer(data, "::");
            ptr = Long.parseLong(match.nextToken());
            FAT_table.ptr = Long.parseLong(match.nextToken());
        }
        fout.close();
    }
    synchronized public void notifyWrite()
    {
        // try
        // {
        //     while(readthrd.isAlive())
        //         readthrd.wait();
            wrtthrd = new ConcurrentWork("write");
            wrtthrd.run();
            wththrd = new ConcurrentWork("dirWatch");
            wththrd.run();
            // readthrd.notify();
    //     }
    //     catch(InterruptedException e)
    //     {
    //         System.out.println("ERROR : " + e);
    //     }
    }
    synchronized public void checkread()
    {
        try
        {
            while(wrtthrd.isAlive())
                readthrd.wait();
            
            readthrd.notify();
        }
        catch(InterruptedException e)
        {
            System.out.println("ERROR : " + e);
        }
    }
    public static void main(String[] args)
    {
        FsController fileSys = new FsController();
        try
        {
            if(fileSys.disk.isFile())
                fileSys.retrieveData();
            else
            {
                System.out.println("Creating Disk for your System : ");
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("bash", "make_fs.sh");
                Process process = processBuilder.start();
                process.waitFor();
            }
            boolean exit = false;
            System.out.println("a.) Read\nb.) Write\nc.) Delete\nd.) Exit");
            Scanner input = new Scanner(System.in);
            Runnable r = new Runnable()
            {
                public void run()
                {
                    fileSys.notifyWrite();
                }
            };
            new Thread(r).start();
            while(!exit)
            {
                System.out.println("Enter your choice");
                String temp = input.nextLine();
                String filesList[] = fileSys.getFilelist();
                int count = 0;
                if(!temp.equals("d"))
                {
                    System.out.println("List of Images : ");
                    for(String file : filesList)
                    {
                        count++;
                        System.out.println(count +".) File name: "+file);
                    }
                }
                switch(temp)
                {
                    case "a" :
                        System.out.println("Enter number to read a file (type all for reading all)");
                        temp = input.nextLine();
                        if(fileSys.isNumeric(temp) || temp.equals("all"))
                        {
                            if(temp.equals("all"))
                            {
                                for(String file : filesList)
                                    fileSys.readData(file);
                            }
                            else
                            {
                                count = Integer.parseInt(temp);
                                fileSys.readData(filesList[count - 1]);
                            }
                        }
                    break;
                    case "b" :
                        System.out.println("Enter number to permanently delete a file (type all for deleting all)");
                        temp = input.nextLine();
                        if(fileSys.isNumeric(temp) || temp.equals("all"))
                        {
                            if(temp.equals("all"))
                            {
                                for(String file : filesList)
                                    fileSys.rmRef(file, "write");
                            }
                            else
                            {
                                count = Integer.parseInt(temp);
                                fileSys.rmRef(filesList[count - 1], "write");
                            }
                        }
                    break;
                    case "c" :
                        System.out.println("Enter number to delete a file (type all for deleting all)");
                        temp = input.nextLine();
                        if(fileSys.isNumeric(temp) || temp.equals("all"))
                        {
                            if(temp.equals("all"))
                            {
                                for(String file : filesList)
                                    fileSys.rmRef(file, "del");
                            }
                            else
                            {
                                count = Integer.parseInt(temp);
                                fileSys.rmRef(filesList[count - 1], "del");
                            }
                        }
                    break;
                    case "d" :
                        fileSys.storePtrValues();
                        System.out.println("Exiting...");
                        exit = true;
                    break;
                    default :
                        System.out.println("!! INVALID INPUT !!\na.) Read\nb.) Write\nc.) Delete\nd.) Exit");
                }
            }
            input.close();
            System.exit(0);
        }
        catch(IOException e)
        {
            System.out.println("ERROR : " + e);
        }
        catch(InterruptedException e)
        {
            System.out.println("ERROR : " + e);
        }
    }
    public boolean isNumeric(String str)
    { 
        try
        {  
            Double.parseDouble(str);  
            return true;
        }
        catch(NumberFormatException e)
        {  
            return false;  
        }  
    }
}
