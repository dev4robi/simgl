package com.robi.util.opengl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.jogamp.opengl.DefaultGLCapabilitiesChooser;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.GLBuffers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLUtil {

    private static final Logger logger = LoggerFactory.getLogger(GLUtil.class);

    private static GLUtil glUtil;
    private static GLProfile glProfile;
    private static GLCapabilities glCaps;
    private static GLContext glCtx;

    public static synchronized GLUtil getInstance() {
        if (glUtil == null) {
            glUtil = new GLUtil();
            glProfile = GLProfile.getDefault(GLProfile.getDefaultDevice());
            glCaps = new GLCapabilities(glProfile);
            glCaps.setRedBits(8);
            glCaps.setBlueBits(8);
            glCaps.setGreenBits(8);
            glCaps.setAlphaBits(8);
            glCaps.setDoubleBuffered(false);
            glCaps.setOnscreen(false);
            glCaps.setPBuffer(true);
            glCaps.setHardwareAccelerated(true);
            logger.info("OpenGL successfully initialized!");
        }

        return glUtil;
    }

    public synchronized void render(int width, int height) throws Exception {
        glCtx = makeGlCtx(width, height);

        // 2회차때 터짐. 로그 확인해볼 것.
        // ctx를 render 시작시 얻고, 스샷찍은 후 release하는게 더 효율적일듯
        // 매번 ctx 생성하는건 어쩔 수 없나?

        final GL2 gl2 = glCtx.getGL().getGL2();
        GLU glu = new GLU();
        gl2.glViewport(0, 0, width, height);
        gl2.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        //glu.gluPerspective(90.0f, width / height, 1.0f, 2000.0f);
        glu.gluOrtho2D( 0.0f, width, 0.0f, height );
        
        gl2.glBegin( GL.GL_TRIANGLE_STRIP );
        gl2.glColor3f(0.0f, 0.0f, 1.0f);
        gl2.glVertex2f(0.0f, 0.0f);
        gl2.glColor3f(0.0f, 1.0f, 0.0f);
        gl2.glVertex2f(width, 0.0f);
        gl2.glColor3f(1.0f, 0.0f, 0.0f);
        gl2.glVertex2f(0.0f, height);
        gl2.glColor3f(0.0f, 0.0f, 0.0f);
        gl2.glVertex2f(width, height);
        gl2.glEnd();
    }

    public synchronized void screenShot() throws Exception {
        if (glCtx == null) {
            throw new Exception("OpenGL NOT initialized!");
        }

        final GLDrawable glDrawable = glCtx.getGLReadDrawable();
        final GL2 gl2 = glCtx.getGL().getGL2();
        final int width = glDrawable.getSurfaceWidth();
        final int height = glDrawable.getSurfaceHeight();
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufImg.getGraphics();
        ByteBuffer byteBuf = GLBuffers.newDirectByteBuffer(width * height * 3);

        gl2.glReadBuffer(GL2.GL_BACK);
        gl2.glReadPixels(0, 0, width, height, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, byteBuf);

        for (int h = 0; h < height; ++h) {
            for (int w = 0; w < width; ++w) {
                graphics.setColor(new Color((byteBuf.get() & 0xff), (byteBuf.get() & 0xff), (byteBuf.get() & 0xff)));
                graphics.drawRect(w, height - h, 1, 1);
            }
        }

        byteBuf.clear();
        
        File outImgFile = new File("/home/robi/desktop/Pictures/test.png");
        
        try {
            ImageIO.write(bufImg, "png", outImgFile);
        }
        catch (IOException e) {
            logger.error("Exception!", e);
        }
    }

    private synchronized GLContext makeGlCtx(int width, int height) {
        if (glCtx != null) {
            try {
                glCtx.release();
            }
            catch (GLException e) {
                logger.error("Exception!", e);
            }
            
            glCtx = null;
        }

        GLDrawableFactory glFactory = GLDrawableFactory.getDesktopFactory(); 
        GLDrawable glDrawable = glFactory.createOffscreenDrawable(null, glCaps, new DefaultGLCapabilitiesChooser(), width, height); 
        glDrawable.setRealized(true);
        glCtx = glDrawable.createContext(null);
        glCtx.setContextCreationFlags(0);

        try {
            glCtx.makeCurrent();
        }
        catch (GLException e) {
            logger.error("Exception!", e);
        }

        return glCtx;
    }
}