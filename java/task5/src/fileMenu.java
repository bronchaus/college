import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class fileMenu extends JFrame implements ActionListener
{
	JFrame frame1 = new JFrame("File Menu");
	JMenuBar firstMenu = new JMenuBar();
	JMenu file = new JMenu("File", true);
	JMenu help = new JMenu("Help");
	JMenuItem linux = new JMenuItem("Linux");
	JMenuItem newItem = new JMenuItem("New");
	JMenuItem open = new JMenuItem("Open");
	JMenuItem exit = new JMenuItem("Exit");
	
	public static void main(String[] args) 
	{
		fileMenu file = new fileMenu();
		
		file.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
	public fileMenu()
	{
		super("Menu");
		
		frame1.setJMenuBar(firstMenu);
		firstMenu.add(file);
		firstMenu.add(help);
		
		file.add(newItem);
		file.add(open);
		file.add(exit);
		
		help.add(linux);
		
		newItem.setMnemonic('N');
		
		exit.addActionListener(this);

		
		frame1.setVisible(true);
		frame1.setSize(300,300);
		frame1.setLocation(100,100);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==exit)
		{
			System.exit(0);		
		}
		
	}
}
