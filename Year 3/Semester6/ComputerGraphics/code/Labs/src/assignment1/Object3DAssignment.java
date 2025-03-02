package assignment1;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;

import assignment.TextureReader;

import com.sun.opengl.util.*;
import com.sun.opengl.util.gl2.*;

public class Object3DAssignment implements GLEventListener, KeyListener {
	GLProfile glp;
	GLCapabilities caps;
	GLCanvas canvas;
	GLU glu;
	
	boolean rotating = false;
	boolean scaling = false;
	boolean translate = false;
	boolean scaler = true;
	boolean cameraRotate = false;
	boolean lightsOn = true;
	boolean textureOn = true;

	
	int x;
	double y=1;
    double xCamera;
    double zCamera;
    double r=0;
	double theta = 0;
	
	private int door_tex;		
	private int wall_tex;		
	private int roof_tex;		
	TextureReader.Texture texture = null;
	
	public Object3DAssignment()
	{
		glp = GLProfile.getDefault();
        caps = new GLCapabilities(glp);
        canvas = new GLCanvas(caps);
        glu = new GLU();
        
        JFrame frame = new JFrame("Assingment 1");
        JMenuBar menuBar = new JMenuBar();
        JMenu options = new JMenu("Options");
        JMenuItem lights = new JMenuItem("Toggle Light");
        JMenuItem texture = new JMenuItem("Toggle Texture");
        JMenuItem camera = new JMenuItem("Toggle Camera");
        
        lights.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				lightsOn=!lightsOn;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        texture.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				textureOn=!textureOn;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        camera.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				//cameraRotate=!cameraRotate;
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				cameraRotate=!cameraRotate;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        options.add(lights);
        options.add(texture);
        options.add(camera);
        menuBar.add(options);
        frame.add(menuBar, BorderLayout.NORTH);
        frame.add(canvas);

        frame.setSize(800, 800);
        frame.setVisible(true);



        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.requestFocus();
        Animator animator = new FPSAnimator(canvas,90);
        animator.add(canvas);
        animator.start();
		
	}
	
	public static void main(String[] args) {
		
		Object3DAssignment obj3d = new Object3DAssignment();
    }

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 glDoor = drawable.getGL().getGL2();	
		GL2 glRoof = drawable.getGL().getGL2();	
		GL2 glWalls = drawable.getGL().getGL2();	
		GL2 gl = drawable.getGL().getGL2();	
		GLU glu = new GLU();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		//make sure we are in Projection mode
		//For rotating camera around Y AXIS!!
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		//gl.glOrtho(-10, 10, -10, 15, 5, 40);
        glu.gluPerspective(80.0f, 1, 1.0, 80.0);
        
		camera(gl, glDoor, glRoof);
		
    	//render ground plane
    	gl.glColor3f(1, 1, 1);
    	
    	
    	//////////////////////////////////////DOOR
    	glDoor.glPushMatrix();
    	glDoor.glTranslatef(1,0,4);
    	glDoor.glRotated(theta, 0, 1, 0);
		//render Door
    	glDoor.glColor3f(1, 1, 0);
    	
    	buildHouse(gl, glDoor, glWalls, glRoof);
    	
	}

	
	public void buildHouse(GL2 gl, GL2 glDoor, GL2 glWalls, GL2 glRoof){
		if(textureOn==true){
	        gl.glEnable(GL.GL_TEXTURE_2D);
			//Create Polygon Door
			glDoor.glPushMatrix();
	        door_tex = loadTexture("door1.png",glDoor);
			glDoor.glBegin(GL2.GL_POLYGON);
				//external door
		    	glDoor.glNormal3f(0, 0, 1);
		        gl.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
		        glDoor.glVertex3f(-3, 0, 0);
		        gl.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
		        glDoor.glVertex3f(0, 0, 0);
		        gl.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
		        glDoor.glVertex3f(0, 8, 0);
		        gl.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
		        glDoor.glVertex3f(-3, 8, 0);
	    	glDoor.glEnd();
			//Create Polygon Door
	    	glDoor.glBegin(GL2.GL_POLYGON);
				//internal door
		    	glDoor.glNormal3f(0, 0, -1);
		        gl.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
		    	glDoor.glVertex3f(0, 0, 0);
		        gl.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
		    	glDoor.glVertex3f(-3, 0, 0);
		        gl.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
		    	glDoor.glVertex3f(-3, 8, 0);
		        gl.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
		    	glDoor.glVertex3f(0, 8, 0);
	    	glDoor.glEnd();
			gl.glPopMatrix();
			//////////////////////////////////END DOOR
			
			glWalls.glPushMatrix();
	        wall_tex = loadTexture("walls1.png",glWalls);
			//Create Polygon House
			glWalls.glBegin(GL2.GL_POLYGON);
				//Gable End positive x
				glWalls.glNormal3f(1, 0, 0);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, 4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, -4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 10, -4);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 14, 0);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 10, 4);
				glWalls.glEnd();	
			
			glWalls.glBegin(GL2.GL_POLYGON);
				//Gable End negative x
				glWalls.glNormal3f(-1, 0, 0);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, 4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 10, 4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 14, 0);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 10, -4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, -4);
			glWalls.glEnd();
		
			/*// Normal House
			gl.glBegin(GL2.GL_POLYGON);
				//Side of house positive z
				gl.glNormal3f(0, 0, 1);
				gl.glVertex3f(-8, 0, 4);
				gl.glVertex3f(8, 0, 4);
				gl.glVertex3f(8, 10, 4);
				gl.glVertex3f(-8, 10, 4);
			gl.glEnd();
			*/
			
			glWalls.glBegin(GL2.GL_POLYGON);
			//Side of house positive z
			//Side on negative x
				glWalls.glNormal3f(0, 0, 1);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, 4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-2, 0, 4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-2, 10, 4);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 10, 4);
			glWalls.glEnd();
			
			glWalls.glBegin(GL2.GL_POLYGON);
			//Side of house positive z
			//Side on positive x
				glWalls.glNormal3f(0, 0, 1);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(1, 0, 4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, 4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 10, 4);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(1, 10, 4);
			glWalls.glEnd();
			
			glWalls.glBegin(GL2.GL_POLYGON);
			//Side of house positive z
			//Piece above the door
				glWalls.glNormal3f(0, 0, 1);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-2, 8, 4);
				glWalls.glTexCoord2f(0.3f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(1, 8, 4);
				glWalls.glTexCoord2f(0.3f, 0.3f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(1, 10, 4);
				glWalls.glTexCoord2f(0.0f, 0.3f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-2, 10, 4);
			glWalls.glEnd();
			
			glWalls.glBegin(GL2.GL_POLYGON);
				//Side of house negative z
				glWalls.glNormal3f(0, 0, -1);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, -4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, -4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 10, -4);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 10, -4);
			glWalls.glEnd();
		
			glWalls.glBegin(GL2.GL_POLYGON);
				//Bottom of house bottom
				glWalls.glNormal3f(0, -1, 0);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, 4);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, 4);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, -4);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, -4);
			glWalls.glEnd();
			glWalls.glPopMatrix();
			
			glRoof.glPushMatrix();
	        roof_tex = loadTexture("roof1.png",glWalls);
			glRoof.glBegin(GL2.GL_POLYGON);
				//Roof of house positive
				glRoof.glNormal3f(0, 8, 6);
		        gl.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(8, 10, 4);
		        gl.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(8, 14, 0);
		        gl.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(-8, 14, 0);
		        gl.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(-8, 10, 4);
			glRoof.glEnd();
			
			glRoof.glBegin(GL2.GL_POLYGON);
				//Roof of house negative
				glRoof.glNormal3f(0, 8, -6);
		        gl.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(-8, 10, -4);
		        gl.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(-8, 14, 0);
		        gl.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(8, 14, 0);
		        gl.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(8, 10, -4);
			glRoof.glEnd();
			//End polygon house creation
			glRoof.glPopMatrix();
			update(gl, glDoor, glRoof);
		}
		
		else{
	        gl.glDisable(GL.GL_TEXTURE_2D);
			//Create Polygon Door
			glDoor.glPushMatrix();
	        door_tex = loadTexture("door1.png",glDoor);
			glDoor.glBegin(GL2.GL_POLYGON);
				//external door
		    	glDoor.glNormal3f(0, 0, 1);
		        gl.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
		        glDoor.glVertex3f(-3, 0, 0);
		        gl.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
		        glDoor.glVertex3f(0, 0, 0);
		        gl.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
		        glDoor.glVertex3f(0, 8, 0);
		        gl.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
		        glDoor.glVertex3f(-3, 8, 0);
	    	glDoor.glEnd();
			//Create Polygon Door
	    	glDoor.glBegin(GL2.GL_POLYGON);
				//internal door
		    	glDoor.glNormal3f(0, 0, -1);
		        gl.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
		    	glDoor.glVertex3f(0, 0, 0);
		        gl.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
		    	glDoor.glVertex3f(-3, 0, 0);
		        gl.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
		    	glDoor.glVertex3f(-3, 8, 0);
		        gl.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
		    	glDoor.glVertex3f(0, 8, 0);
	    	glDoor.glEnd();
			gl.glPopMatrix();
			//////////////////////////////////END DOOR
			
			glWalls.glPushMatrix();
	        wall_tex = loadTexture("walls1.png",glWalls);
			//Create Polygon House
			glWalls.glBegin(GL2.GL_POLYGON);
				//Gable End positive x
				glWalls.glNormal3f(1, 0, 0);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, 4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, -4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 10, -4);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 14, 0);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 10, 4);
				glWalls.glEnd();	
			
			glWalls.glBegin(GL2.GL_POLYGON);
				//Gable End negative x
				glWalls.glNormal3f(-1, 0, 0);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, 4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 10, 4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 14, 0);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 10, -4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, -4);
			glWalls.glEnd();
		
			/*// Normal House
			gl.glBegin(GL2.GL_POLYGON);
				//Side of house positive z
				gl.glNormal3f(0, 0, 1);
				gl.glVertex3f(-8, 0, 4);
				gl.glVertex3f(8, 0, 4);
				gl.glVertex3f(8, 10, 4);
				gl.glVertex3f(-8, 10, 4);
			gl.glEnd();
			*/
			
			glWalls.glBegin(GL2.GL_POLYGON);
			//Side of house positive z
			//Side on negative x
				glWalls.glNormal3f(0, 0, 1);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, 4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-2, 0, 4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-2, 10, 4);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 10, 4);
			glWalls.glEnd();
			
			glWalls.glBegin(GL2.GL_POLYGON);
			//Side of house positive z
			//Side on positive x
				glWalls.glNormal3f(0, 0, 1);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(1, 0, 4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, 4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 10, 4);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(1, 10, 4);
			glWalls.glEnd();
			
			glWalls.glBegin(GL2.GL_POLYGON);
			//Side of house positive z
			//Piece above the door
				glWalls.glNormal3f(0, 0, 1);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-2, 8, 4);
				glWalls.glTexCoord2f(0.3f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(1, 8, 4);
				glWalls.glTexCoord2f(0.3f, 0.3f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(1, 10, 4);
				glWalls.glTexCoord2f(0.0f, 0.3f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-2, 10, 4);
			glWalls.glEnd();
			
			glWalls.glBegin(GL2.GL_POLYGON);
				//Side of house negative z
				glWalls.glNormal3f(0, 0, -1);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, -4);
				glWalls.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, -4);
				glWalls.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 10, -4);
				glWalls.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 10, -4);
			glWalls.glEnd();
		
			glWalls.glBegin(GL2.GL_POLYGON);
				//Bottom of house bottom
				glWalls.glNormal3f(0, -1, 0);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, 4);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, 4);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(-8, 0, -4);
				glWalls.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glWalls.glVertex3f(8, 0, -4);
			glWalls.glEnd();
			glWalls.glPopMatrix();
			
			glRoof.glPushMatrix();
	        roof_tex = loadTexture("roof1.png",glWalls);
			glRoof.glBegin(GL2.GL_POLYGON);
				//Roof of house positive
				glRoof.glNormal3f(0, 8, 6);
		        gl.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(8, 10, 4);
		        gl.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(8, 14, 0);
		        gl.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(-8, 14, 0);
		        gl.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(-8, 10, 4);
			glRoof.glEnd();
			
			glRoof.glBegin(GL2.GL_POLYGON);
				//Roof of house negative
				glRoof.glNormal3f(0, 8, -6);
		        gl.glTexCoord2f(0.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(-8, 10, -4);
		        gl.glTexCoord2f(1.0f, 0.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(-8, 14, 0);
		        gl.glTexCoord2f(1.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(8, 14, 0);
		        gl.glTexCoord2f(0.0f, 1.0f); //this texture point will be mapped to the vertex below it
				glRoof.glVertex3f(8, 10, -4);
			glRoof.glEnd();
			//End polygon house creation
			glRoof.glPopMatrix();
			update(gl, glDoor, glRoof);		}
		
		update(gl, glDoor, glRoof);
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {	
		GL2 gl = drawable.getGL().getGL2();
		GL2 glRoof = drawable.getGL().getGL2();
		GL2 glWall = drawable.getGL().getGL2();
		GL2 glDoor = drawable.getGL().getGL2();
		
		buildHouse(gl, glDoor, glWall, glRoof);

        gl.glEnable(GL.GL_CULL_FACE); 
		gl.glCullFace(GL.GL_BACK); 
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		lights(gl);
		
		final GL2 gl1 = drawable.getGL().getGL2();
        gl1.glShadeModel(GL2.GL_SMOOTH);              // Enable Smooth Shading
        gl1.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
        gl1.glClearDepth(1.0f);                      // Depth Buffer Setup
        gl1.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
        gl1.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do 
        gl1.glEnable(GL.GL_TEXTURE_2D);
        //here we are calling a function defined below to load in the image and return
        //a texture id that we can use with glBindTexture (see display)
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		
		if (height < 1) { // avoid a divide by zero error!   
            height = 1;
        }
        
		float aspect = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
       
        //set up the camera
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(80.0f, aspect, 1.0, 40.0);	
	}

	public void update(GL2 gl, GL2 glDoor, GL2 glRoof){			
		lights(gl);
		
		if(rotating){
			gl.glRotatef(x, 0, 1, 0);
			x=x+1;
		}
		
		if(scaling){
			glDoor=gl;
			gl.glScaled(y, y, y);
			glDoor.glScaled(y, y, y);
			glRoof.glScaled(y, y, y);
			if(y==1){
				scaler = false;
			}
			if(y<=0){
				scaler = true;
			}
			
			if(scaler){
				y=y+.01;
			}
			if(!scaler){
				y=y-.01;
			}
			
		}
		
		if(translate){
			gl.glTranslated(0, 0, -5);
			glDoor.glTranslated(0, 0, -5);
			glRoof.glTranslated(0, 0, -5);
		}
	}
	
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getKeyCode() == KeyEvent.VK_T)
			translate=!translate;
		
		if (arg0.getKeyCode() == KeyEvent.VK_R){
			rotating=!rotating;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_S){
			scaling=!scaling;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_O){
			theta = theta+10;
			if(theta >= 160){
				JOptionPane.showMessageDialog(new JFrame(), "Door cannot be oppened further\n" +
						"Press 'c' to close door" , "Error",
				        JOptionPane.ERROR_MESSAGE);
				theta = 160;
			}
		}
		if (arg0.getKeyCode() == KeyEvent.VK_C){
			theta = theta-10;
			if(theta <= -1){
				JOptionPane.showMessageDialog(new JFrame(), "Door cannot be closed further\n" +
						"Press 'o' to open door" , "Error",
				        JOptionPane.ERROR_MESSAGE);
				theta = 0;
			}
		}
	}
	
	public void camera(GL2 gl, GL2 glDoor, GL2 glRoof){
		if(cameraRotate==true){
		//Rotates the camera around the Y axis
		int d=25;
        xCamera = d*Math.sin(Math.toRadians(r));
        zCamera = d*Math.cos(Math.toRadians(r));
        r+=.5;
		glu.gluLookAt(xCamera,15,zCamera,0,2,0,0, 20, 0);
		}
		else{
		//Set up static camera
		glu.gluLookAt(-15,15,20,0,2,0,0, 1, 0);
		}
		
		update(gl, glDoor, glRoof);
	}
    
	
	public void lights(GL2 gl){
		if(lightsOn==true){
			gl.glEnable(gl.GL_LIGHTING);
			gl.glEnable(gl.GL_LIGHT0);
			float [] whiteLight = {1, 1, 1, 1};
			float [] ambientLight = {0.1f, 0.1f, 0.1f, 1.0f}; 
			gl.glLightfv(gl.GL_LIGHT0, gl.GL_DIFFUSE, whiteLight, 1); 
			gl.glLightfv(gl.GL_LIGHT0, gl.GL_SPECULAR, whiteLight, 0); 
			gl.glLightfv(gl.GL_LIGHT0, gl.GL_AMBIENT, ambientLight, 1);
			float [] lightPosition = {25, 25, 25,1}; 
			gl.glLightfv(gl.GL_LIGHT0, gl.GL_POSITION, lightPosition, 0);
			float [] diffuse_mp = {1.0f,1.0f,1.0f,1.0f};//white 
			gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_DIFFUSE, diffuse_mp,0);	
		}
		else{
			gl.glEnable(gl.GL_LIGHTING);
			gl.glEnable(gl.GL_LIGHT0);
			float [] whiteLight = {0, 0, 0, 0};
			float [] ambientLight = {0.1f, 0.1f, 0.1f, 1.0f}; 
			gl.glLightfv(gl.GL_LIGHT0, gl.GL_DIFFUSE, whiteLight, 1); 
			gl.glLightfv(gl.GL_LIGHT0, gl.GL_SPECULAR, whiteLight, 0); 
			gl.glLightfv(gl.GL_LIGHT0, gl.GL_AMBIENT, ambientLight, 1);
			float [] lightPosition = {25, 25, 25,1}; 
			gl.glLightfv(gl.GL_LIGHT0, gl.GL_POSITION, lightPosition, 0);
			float [] diffuse_mp = {1.0f,1.0f,1.0f,1.0f};//white 
			gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_DIFFUSE, diffuse_mp,0);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

    private void makeRGBTexture(GL gl, GLU glu, TextureReader.Texture img, 
            int target, boolean mipmapped) {
    	
    	//the function glTexImage2D (below) loads the texture into video memory, once loaded we can free
    	//the image memory (in java the garbage collector will do this).
    	//Parameter 1:	GL_TEXTURE_2D -> inform openGL the texture is a 2D texture
    	//Parameter 2:	0-> Should remain 0 for us, used for building texture maps of different sizes
    	//				0 means just use the texture map in its current form
    	//Parameter 3:	GL_RGB->inform openGL the image is an RGB image (could also be GL_LUMINANCE)
    	//Parameter 4:	im_size-> user defined variable that defines the number of rows in the texture
    	//Parameter 5:	im_size-> user defined variable that defines the number of columns in the texture
    	//Parameter 6:	0-> sets the border of the texture (0 indicates no border)
    	//Parameter 7:	GL_RGB-> Specifies the type of texels to be used
    	//Parameter 8:	GL_UNSIGNED_BYTE-> Identifies the size the texels to be used 
    	//				GL_UNSIGNED_BYTE means 1 byte for each colour channel or 24 bit colour texels
    	//Parameter 9:	image-> the actual raw image pixel values to become texel values
        
        if (mipmapped) {
            glu.gluBuild2DMipmaps(target, GL.GL_RGB8, img.getWidth(), 
                    img.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
        } else {
            gl.glTexImage2D(target, 0, GL.GL_RGB, img.getWidth(), 
                    img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
        }
    }
	
    private int genTexture(GL gl) {
        final int[] tmp = new int[3];
        //glGenTextures generates an unused integer id for the texture were about
    	//to create, we can use this to reference the texture in future
        gl.glGenTextures(1, tmp, 0);
        return tmp[0];
    }
	
    private int loadTexture(String filename, GL gl)
    {
    	int tex_id = genTexture(gl);//create an unused id for the texture
    	//we must inform openGL that this texture should become the current texture
    	//so subsequent texture functions will apply it, like the ones below
        gl.glBindTexture(GL.GL_TEXTURE_2D, tex_id);
        //read in the image
        try {
            texture = TextureReader.readTexture(filename);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //make an openGL texture from the image
        makeRGBTexture(gl, glu, texture, GL.GL_TEXTURE_2D, false);
        /*glTexParameteri is used to set various propertiers of the texturing procedure
    	all texture properties are set by glTexParameteri
        
        Each pixel in the rendered image corresponds to a small area on the surface of an object and
    	hence a small area of the texture map. If the object is far away from the viewer a single pixel
    	may be representative of a number of texels. If the object is close to the viewer a single texel 
    	may be representative of a number of pixels. This is called minification and magnification.
    	So openGL must either take the large area of the texture map and squash it into the smaller pixel
    	area or take the small texel area and magnify it so it fits into the larger pixel area.
    	The following function calls tell openGL how to do this
    	
    	GL_NEAREST and GL_LINEAR are two techniqes used for magnification/minification.
    	GL_NEAREST is faster but GL_LINEAR normally yields better results*/
        
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        
        /* when using the 2D texture defined above, if the s texture coordinate goes outside of the
    	texture range (0->1) then clamp it, that is if its greater than 1 set it to one
    	if it is less than zeros set it to zero. Other option-> GL_REPEAT repeats the texure again
    	outside of the map bounds (0->1)*/

    	gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
    	//same goes for the t texture coordinate
    	gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        return tex_id;
    	
    }
}