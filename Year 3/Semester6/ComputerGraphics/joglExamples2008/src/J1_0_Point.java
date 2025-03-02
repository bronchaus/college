/***********************************************************
 * Created on 2004-2-20
 * @author Jim X. Chen: draw a point
 * Modified 2007-9-8 for the new JOGL system
 */
import java.awt.*;
import javax.media.opengl.*;

// import net.java.games.jogl.*;

public class J1_0_Point extends Frame implements GLEventListener {

	static int HEIGHT = 800, WIDTH = 800;
	static GL gl; // interface to OpenGL
	static GLCanvas canvas; // drawable in a frame
	static GLCapabilities capabilities;

	public J1_0_Point() {

		// 1. specify a drawable: canvas
		capabilities = new GLCapabilities();
		canvas = new GLCanvas();
		// canvas = GLDrawableFactory.getFactory().createGLCanvas(capabilities);

		// 2. listen to the events related to canvas: reshape
		canvas.addGLEventListener(this);

		// 3. add the canvas to fill the Frame container
		add(canvas, BorderLayout.CENTER);

		// 4. interface to OpenGL functions
		gl = canvas.getGL();
	}

	public static void main(String[] args) {
		J1_0_Point frame = new J1_0_Point();

		// 5. set the size of the frame and make it visible
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
	}

	// called once for OpenGL initialization
	public void init(GLAutoDrawable drawable) {

		// 6. specify a drawing color: white
		gl.glColor3f(1.0f, 1.0f, 1.0f);
	}

	// called for handling reshaped drawing area
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		WIDTH = width; // new width and height saved
		HEIGHT = height;

		// 7. specify the drawing area (frame) coordinates
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, width, 0, height, -1.0, 1.0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glViewport(100, 100, 100, 50);
	}

	// called for OpenGL rendering every reshape
	public void display(GLAutoDrawable drawable) {

		// 8. specify to draw a point
		gl.glPointSize(10);
		gl.glBegin(GL.GL_POINTS);
		gl.glVertex2i(WIDTH / 2, HEIGHT / 2);
		gl.glEnd();
	}

	// called if display mode or device are changed
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}
}
