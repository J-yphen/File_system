/************************************************************************
                        Compress and De-compress images 
                              and display images 
                                from given byte
                                     array
                                                Jay Bhatt
                                                2019BTCSE010
************************************************************************/
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

class ImageCompression
{
    public byte[] compressUnderSize(byte[] srcImgData, long maxSize)
    {
        double scale = 0.9;
        byte[] imgData = Arrays.copyOf(srcImgData, srcImgData.length);
 
        if (imgData.length > maxSize)
        {
            do
            {
                try
                {
                    imgData = compress(imgData, scale);
                }
                catch (IOException e)
                {
                    throw new IllegalStateException ("ERROR", e);
                }
 
            }while (imgData.length > maxSize);
        }
 
        return imgData;
    }
    public byte[] compress(byte[] srcImgData, double scale) throws IOException
    {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(srcImgData));
        int width = (int) (bi.getWidth () * scale);
        int height = (int) (bi.getHeight () * scale);
 
        Image image = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
 
        Graphics g = tag.getGraphics();
        g.setColor(Color.RED);
        g.drawImage (image, 0, 0, null);
        g.dispose();
 
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ImageIO.write(tag, "JPEG", bOut);
 
        return bOut.toByteArray();
    }
    public byte[] getByteByPic(String imageUrl) throws IOException
    {
        File imageFile = new File(imageUrl);
        InputStream inStream = new FileInputStream(imageFile);
        BufferedInputStream bis = new BufferedInputStream(inStream);
        BufferedImage bm = ImageIO.read(bis);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String type = imageUrl.substring(imageUrl.length() - 4);
        ImageIO.write(bm, type, bos);
        bos.flush();
        byte[] data = bos.toByteArray();
        return data;
    }
    public void byte2image(byte[] data, String path)
    {
        if(data.length<3||path.equals("")) return;
        try
        {
            File checkDir = new File(path);
            checkDir.getParentFile().mkdir();
            checkDir.createNewFile();
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
        }
        catch(Exception ex)
        {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException
    {
        
    }
}