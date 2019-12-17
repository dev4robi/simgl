package com.robi.simgl.service;

import com.robi.data.ApiResult;
import com.robi.util.opengl.GLUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OpenGlService {

    private static final Logger logger = LoggerFactory.getLogger(OpenGlService.class);
    private static final GLUtil glUtil = GLUtil.getInstance();

    public ApiResult drawRect(int width, int height) {
        try {
            glUtil.render(width, height);
            glUtil.screenShot();
        }
        catch (Exception e) {
            logger.error("Exception!", e);
            return ApiResult.make(false);
        }

        return ApiResult.make(true);
    }
}