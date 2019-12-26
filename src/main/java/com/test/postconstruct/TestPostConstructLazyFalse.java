package com.test.postconstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;

@Component
@Lazy(false)
public class TestPostConstructLazyFalse {

    private static final Logger logger = LoggerFactory.getLogger(TestPostConstructLazyFalse.class);

    @PostConstruct
    public void postConstruct() {
        logger.info("###start@@@ bean.postConstruct");
    }

    @PreDestroy
    public void preDestroy() {
        new File("preDestroy-" + System.currentTimeMillis()).mkdir();
    }
}
