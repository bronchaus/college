import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class threeShapes extends JPanel 
{

	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		this.setBackground(Color.GRAY);
		
		g.setColor(Color.BLUE);
		g.drawRect(50, 50, 200, 300);
		
		g.setColor(new Color(149,200,55));
		g.drawOval(275, 50, 200, 300);
		
		g.setColor(Color.WHITE);
		g.drawLine(500, 350, 500, 50);
		
		g.setColor(Color.RED);
		g.drawString("HELLO THIS IS A DRAW STING", 50, 400);
	}

}
