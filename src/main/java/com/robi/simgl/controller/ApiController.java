package com.robi.simgl.controller;

import com.robi.data.ApiResult;
import com.robi.simgl.service.OpenGlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private OpenGlService openGlSvc;

    @PostMapping("/gldraw/rect")
    public ApiResult glDraw() {
        return openGlSvc.drawRect(600, 480);
    }
}