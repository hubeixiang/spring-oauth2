package org.springframework.security.oauth.cache.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth.cache.commons.api.IStatusHolderService;

public class ThreadCacheHolderClean implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ThreadCacheHolderClean.class);
    private final IStatusHolderService iStatusHolderService;
    private final String threadName;

    public ThreadCacheHolderClean(IStatusHolderService iStatusHolderService, String threadName) {
        this.iStatusHolderService = iStatusHolderService;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5 * 60 * 1000);
                if (iStatusHolderService != null) {
                    iStatusHolderService.clean();
                }
            } catch (InterruptedException e) {
                logger.debug(String.format("threadName = %s clean cache holder Exception:%s", threadName, e.getMessage()));
                break;
            } finally {
                logger.debug(String.format("threadName = %s clean cache holder over", threadName));
            }
        }
    }
}
