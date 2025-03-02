import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class chess extends JFrame implements ActionListener 
{
	JFrame frame1 = new JFrame();
	
	//Declaring and initializing 3 panels containing the 4 chess squares and the directional button grid
	JPanel panel1 = new JPanel();
	JPanel topRow = new JPanel();
	JPanel bottomRow = new JPanel();
	
	//adding the arrow buttons to variable names..... 
	//I understand in textpad the image path only has to go to folder images but eclipse makes me go to Users
	Icon leftImage = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/left.png");
	Icon rightImage = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/right.png");
	Icon upImage = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/up.png");
	Icon downImage = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/down.png");
	Icon leftUpImage = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/leftUp.png");
	Icon leftDownImage = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/leftDown.png");
	Icon rightUpImage = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/rightUp.png");
	Icon rightDownImage = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/rightDown.png");
	Icon resetImage = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/reset.png");
	
	//adding the pawn icon
	Icon pawn = new ImageIcon("/Users/mjrbronchaus/Desktop/College/java/chessBoard/images/pawn.png");

	//declaring 4 labels each one is a square on the "chess board"
	JLabel label2 = new JLabel(pawn);
	JLabel label3 = new JLabel();
	JLabel label4 = new JLabel();
	JLabel label5 = new JLabel();

	//declaring 9 JButtons 8 arrows and a reset button and adding the images to the buttons
	JButton reset = new JButton(resetImage);
	
	JButton left = new JButton(leftImage);
	JButton right = new JButton(rightImage);
	JButton up = new JButton(upImage);
	JButton down = new JButton(downImage);
	JButton leftUp = new JButton(leftUpImage);
	JButton leftDown = new JButton(leftDownImage);
	JButton rightUp = new JButton(rightUpImage);
	JButton rightDown = new JButton(rightDownImage);
	
	//initializing the container to add the JPanels to
	Container c = getContentPane();
	
	//Main method
	public static void main(String[] args) 
	{
		chess chessGame = new chess();
		
		chessGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	//Constructor class
	public chess()
	{
		//Title of the window
		super("Chess");
		//setting the layout style for all the panels
		panel1.setLayout(new GridLayout(3,3));
		topRow.setLayout(new FlowLayout());
		bottomRow.setLayout(new FlowLayout());
		
		//adding all the buttons to the panel with the grid layout
		panel1.add(leftUp);
		panel1.add(up);
		panel1.add(rightUp);		
		panel1.add(left); 
		panel1.add(reset);
		panel1.add(right);
		panel1.add(leftDown);
		panel1.add(down);
		panel1.add(rightDown);
		
		//changing the background color to emphasize the clickable buttons and add contrast to the chess board
		panel1.setBackground(new Color(255, 100, 100));
		topRow.setBackground(new Color(255, 100, 100));
		bottomRow.setBackground(new Color(255, 100, 100));
		
		//setting the colors to the chess board
		label2.setBackground(Color.BLACK);
		label3.setBackground(Color.WHITE);
		label4.setBackground(Color.WHITE);
		label5.setBackground(Color.BLACK);
		
		//setting opaque from default false to true allows the colors to be seen on the chess square labels
		label2.setOpaque(true);
		label3.setOpaque(true);
		label4.setOpaque(true);
		label5.setOpaque(true);
		
		//setting the dimensions of the chess squares
		label2.setPreferredSize(new Dimension (110, 110));
		label3.setPreferredSize(new Dimension (110, 110));
		label4.setPreferredSize(new Dimension (110, 110));
		label5.setPreferredSize(new Dimension (110, 110));

		//adding the chess square labels to the panels
		topRow.add(label2);
		topRow.add(label3);
		bottomRow.add(label4);
		bottomRow.add(label5);
		
		//adding and setting the placement of the panels to the content pane
		c.add(panel1, BorderLayout.NORTH);
		c.add(topRow);
		c.add(bottomRow, BorderLayout.SOUTH);
		
		//frame1.add();
		
		//turning the unmovable buttons off from the start of the program
		left.setEnabled(false);
		up.setEnabled(false);
		leftUp.setEnabled(false);
		leftDown.setEnabled(false);
		rightUp.setEnabled(false);
		
		//adding action listeners to the JButtons in the gridlayout
		reset.addActionListener(this);
		left.addActionListener(this);
		right.addActionListener(this);
		up.addActionListener(this);
		down.addActionListener(this);
		leftUp.addActionListener(this);
		leftDown.addActionListener(this);
		rightUp.addActionListener(this);
		rightDown.addActionListener(this);
		
		//setting visible, size, location, packing the window and setting the window to visible
		setVisible(true);
		setResizable(false);
		setSize(350,590);
		setLocation(200,0);
		//pack();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		//Setting all the actions for the different button action outcomes
		if(e.getSource()==reset)
		{	
			label2.setIcon(pawn);
			label3.setIcon(null);
			label4.setIcon(null);
			label5.setIcon(null);
			
			left.setEnabled(false);
			up.setEnabled(false);
			rightUp.setEnabled(false);
			leftUp.setEnabled(false);
			leftDown.setEnabled(false);
		
			rightDown.setEnabled(true);
			down.setEnabled(true);
			right.setEnabled(true);
		}
		
		//nested if statements for left button
		if(e.getSource()==left)
		{
			if(label3.getIcon()==pawn)
			{
				label3.setIcon(null);
				label2.setIcon(pawn);
				down.setEnabled(true);
				right.setEnabled(true);
				rightDown.setEnabled(true);
				
				left.setEnabled(false);
				leftDown.setEnabled(false);
				leftUp.setEnabled(false);
				up.setEnabled(false);
				rightUp.setEnabled(false);
			}
			
			if(label5.getIcon()==pawn)
			{
				label5.setIcon(null);
				label4.setIcon(pawn);
				right.setEnabled(true);
				up.setEnabled(true);
				rightUp.setEnabled(true);
				
				rightDown.setEnabled(false);
				left.setEnabled(false);
				leftUp.setEnabled(false);
				down.setEnabled(false);
				leftDown.setEnabled(false);
			}
		}
		
		//nested if statements for down button
		if(e.getSource()==down)
		{
			if(label2.getIcon()==pawn)
			{
				label2.setIcon(null);
				label4.setIcon(pawn);
				right.setEnabled(true);
				up.setEnabled(true);
				rightUp.setEnabled(true);
				
				down.setEnabled(false);
				rightDown.setEnabled(false);
				left.setEnabled(false);
				leftDown.setEnabled(false);
				leftUp.setEnabled(false);
			}
			
			if(label3.getIcon()==pawn)
			{
				label3.setIcon(null);
				label5.setIcon(pawn);
				left.setEnabled(true);
				leftUp.setEnabled(true);
				up.setEnabled(true);
				
				rightUp.setEnabled(false);
				rightDown.setEnabled(false);
				right.setEnabled(false);
				down.setEnabled(false);
				leftDown.setEnabled(false);
			}
		}
		
		//nested if statements for right button		
		if(e.getSource()==right)
		{
			if(label2.getIcon()==pawn)
			{
				label2.setIcon(null);
				label3.setIcon(pawn);
				left.setEnabled(true);
				leftDown.setEnabled(true);
				down.setEnabled(true);
				
				right.setEnabled(false);
				up.setEnabled(false);
				rightUp.setEnabled(false);
				rightDown.setEnabled(false);
				leftUp.setEnabled(false);
			}
			
			if(label4.getIcon()==pawn)
			{
				label4.setIcon(null);
				label5.setIcon(pawn);
				left.setEnabled(true);
				leftUp.setEnabled(true);
				up.setEnabled(true);
	
				right.setEnabled(false);
				rightUp.setEnabled(false);
				rightDown.setEnabled(false);
				down.setEnabled(false);
				leftDown.setEnabled(false);
			}
		}
		//nested if statements for up button		
		if(e.getSource()==up)
		{
			if(label4.getIcon()==pawn)
			{
				label4.setIcon(null);
				label2.setIcon(pawn);
				down.setEnabled(true);
				rightDown.setEnabled(true);
				right.setEnabled(true);
				
				leftUp.setEnabled(false);
				leftDown.setEnabled(false);
				up.setEnabled(false);
				rightUp.setEnabled(false);
				left.setEnabled(false);
			}
			
			if(label5.getIcon()==pawn)
			{
				label5.setIcon(null);
				label3.setIcon(pawn);
				left.setEnabled(true);
				leftDown.setEnabled(true);
				down.setEnabled(true);
				
				rightUp.setEnabled(false);
				rightDown.setEnabled(false);
				right.setEnabled(false);
				up.setEnabled(false);
				leftUp.setEnabled(false);
			}
		}
		
		//Setting the 4 options for the diagonal buttons
		if(e.getSource()==rightDown)
		{
			label2.setIcon(null);
			label5.setIcon(pawn);
			left.setEnabled(true);
			leftUp.setEnabled(true);
			up.setEnabled(true);

			right.setEnabled(false);
			rightUp.setEnabled(false);
			rightDown.setEnabled(false);
			down.setEnabled(false);
			leftDown.setEnabled(false);
		}
		
		if(e.getSource()==leftDown)
		{
			label3.setIcon(null);
			label4.setIcon(pawn);
			right.setEnabled(true);
			up.setEnabled(true);
			rightUp.setEnabled(true);
			
			down.setEnabled(false);
			rightDown.setEnabled(false);
			left.setEnabled(false);
			leftDown.setEnabled(false);
			leftUp.setEnabled(false);
		}
		
		if(e.getSource()==rightUp)
		{
			label4.setIcon(null);
			label3.setIcon(pawn);
			left.setEnabled(true);
			leftDown.setEnabled(true);
			down.setEnabled(true);
			
			rightUp.setEnabled(false);
			rightDown.setEnabled(false);
			right.setEnabled(false);
			up.setEnabled(false);
			leftUp.setEnabled(false);
		}
		
		if(e.getSource()==leftUp)
		{
			label5.setIcon(null);
			label2.setIcon(pawn);
			down.setEnabled(true);
			rightDown.setEnabled(true);
			right.setEnabled(true);
			
			leftUp.setEnabled(false);
			leftDown.setEnabled(false);
			up.setEnabled(false);
			rightUp.setEnabled(false);
			left.setEnabled(false);
		}
	}
}