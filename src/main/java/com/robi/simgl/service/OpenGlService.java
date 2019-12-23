package com.robi.simgl.service;

import java.io.File;

import com.robi.data.ApiResult;
import com.robi.util.MapUtil;
import com.robi.util.RandomUtil;
import com.robi.util.opengl.GLUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:config.properties")
public class OpenGlService implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(OpenGlService.class);
    private static final GLUtil glUtil = GLUtil.getInstance();
    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    public ApiResult drawRect(int width, int height) {
        String outImgName = null;

        try {
            String outDirName = env.getProperty("simgl.imgOutUrl");
            String outFileName = "test_" + RandomUtil.genRandomStr(5, RandomUtil.NUMERIC) + ".png";
            File outImgDir = new File(outDirName);
            File outImgFile = new File(outDirName + "/" + outFileName);
            
            File[] fileList = outImgDir.listFiles();
            int maxImgFileCnt = env.getProperty("simgl.maxImgStore", Integer.class);
            
            if (fileList != null && fileList.length > maxImgFileCnt) {
                logger.info("File overloaded... cleaning start! (" + fileList.length + " > " + maxImgFileCnt + ")");

                for (File file : fileList) {
                    if (file != outImgFile) {
                        file.delete();
                    }
                }

                logger.info("File cleaning done!");
            }

            glUtil.render(outImgFile, width, height);
            outImgName = outImgFile.getCanonicalPath();
            logger.info("Rendering done! (File: " + outImgName + ")");
        }
        catch (Exception e) {
            logger.error("Exception!", e);
            return ApiResult.make(false);
        }

        return ApiResult.make(true, MapUtil.toMap("imgUrl", outImgName));
    }
}