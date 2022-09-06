package com.ivo.my.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AsyncComponent {

    @Async
    public void asyncMethod() {
        log.info("Running async method");
    }
}
