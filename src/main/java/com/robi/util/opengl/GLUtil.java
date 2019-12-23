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
import com.robi.util.RandomUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLUtil {

    private static final Logger logger = LoggerFactory.getLogger(GLUtil.class);

    private static GLUtil glUtil;
    private static GLProfile glProfile;
    private static GLCapabilities glCaps;

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

    private static synchronized GLContext makeGlCtx(int width, int height) {
        GLDrawableFactory glFactory = GLDrawableFactory.getDesktopFactory(); 
        GLDrawable glDrawable = glFactory.createOffscreenDrawable(null, glCaps, new DefaultGLCapabilitiesChooser(), width, height);
        glDrawable.setRealized(true);

        GLContext rtGlCtx = glDrawable.createContext(null);
        rtGlCtx.setContextCreationFlags(0);

        try {
            rtGlCtx.makeCurrent();
        }
        catch (GLException e) {
            logger.error("Exception!", e);
        }

        return rtGlCtx;
    }

    public synchronized void render(File outFile, int width, int height) {
        // init
        final GLContext glCtx = makeGlCtx(width, height);
        final GL2 gl2 = glCtx.getGL().getGL2();
        GLU glu = new GLU();
        
        gl2.glViewport(0, 0, width, height);
        gl2.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        
        // camera
        //glu.gluPerspective(90.0f, width / height, 1.0f, 2000.0f);
        glu.gluOrtho2D((float)0.0f, width, 0.0f, (float)height);
        gl2.glTranslatef(0.0f, 0.0f, 0.0f);
        
        // draw object
        gl2.glPushMatrix();
        gl2.glBegin(GL.GL_TRIANGLE_STRIP);
        gl2.glColor3f(1.0f, 1.0f, 1.0f);
        gl2.glVertex2f(0.0f, 0.0f);
        gl2.glColor3f(RandomUtil.genRandomFloat(), RandomUtil.genRandomFloat(), RandomUtil.genRandomFloat());
        gl2.glVertex2f(width, 0.0f);
        gl2.glColor3f(RandomUtil.genRandomFloat(), RandomUtil.genRandomFloat(), RandomUtil.genRandomFloat());
        gl2.glVertex2f(0.0f, height);
        gl2.glColor3f(0.0f, 0.0f, 0.0f);
        gl2.glVertex2f(width, height);
        gl2.glPopMatrix();
        gl2.glEnd();

        // make screenshot
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
        
        String outFileExt = null;

        try {
            String outFileName = outFile.getName();
            outFileExt = outFileName.substring(outFileName.lastIndexOf(".") + 1, outFileName.length());
        }
        catch (IndexOutOfBoundsException e) {
            logger.warn("Exception! 'outFileExt' will chagned to 'png' default.", e);
            outFileExt = "png";
        }

        try {
            ImageIO.write(bufImg, outFileExt, outFile);
        }
        catch (IOException e) {
            logger.error("Exception!", e);
        }

        // release
        glCtx.release();
    }
}