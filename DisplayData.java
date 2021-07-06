/************************************************************************
                        Display Images with 
                             given path
                                                Jay Bhatt
************************************************************************/
import java.awt.*;  
import javax.swing.JFrame;
import javax.swing.JLabel;  
  
public class DisplayData extends Canvas
{
    static String filename;
    static String data;
    public void paint(Graphics g)
    {
        Toolkit t = Toolkit.getDefaultToolkit();  
        Image i = t.getImage(filename);  
        g.drawImage(i, 120, 100, this);  
          
    }
    public static void main()
    {  
        DisplayData m = new DisplayData();
        JFrame f = new JFrame();
        f.add(m);  
        f.setBounds(50, 50, 300, 300);
        f.setSize(300, 300);
        f.setVisible(true);

        JLabel showdata = new JLabel(data);
        showdata.setBounds(50, 500, 300, 50);
        showdata.setVisible(true);
        f.add(showdata);
    }  
  
}  
