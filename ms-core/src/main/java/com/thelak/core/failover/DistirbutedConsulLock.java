package com.thelak.core.failover;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.ecwid.consul.v1.session.model.NewSession;

import java.time.LocalDateTime;

public class DistirbutedConsulLock {

    private static final String prefix = "lock/";

    protected ConsulClient consulClient;
    protected String sessionId = null;
    protected String keyPath;


    public DistirbutedConsulLock(ConsulClient consulClient, String lockKey) {
        this.consulClient = consulClient;
        this.keyPath = prefix + lockKey;
    }

    public Boolean lock(boolean block) throws InterruptedException {
        return lock(block, 500L, null);
    }

    public Boolean lock(boolean block, long timeInterval, Integer maxTimes) throws InterruptedException {
        if (sessionId != null) {
            return true;
        }
        sessionId = createSession("lock-" + this.keyPath);
        int count = 1;
        while (true) {
            PutParams putParams = new PutParams();
            putParams.setAcquireSession(sessionId);

            if (consulClient.setKVValue(keyPath, "lock:" + LocalDateTime.now(), putParams).getValue()) {
                return true;
            }

            if (!block) {
                return false;
            }

            if (maxTimes != null && count >= maxTimes) {
                return false;
            }

            count++;
            Thread.sleep(timeInterval);
        }
    }

    public Boolean unlock() {
        PutParams putParams = new PutParams();
        putParams.setReleaseSession(sessionId);
        boolean result = consulClient.setKVValue(keyPath, "unlock:" + LocalDateTime.now(), putParams).getValue();

        destroySession();
        return result;
    }

    private String createSession(String sessionName) {
        NewSession newSession = new NewSession();
        newSession.setName(sessionName);
        return consulClient.sessionCreate(newSession, null).getValue();
    }

    private void destroySession() {
        if (sessionId != null) {
            consulClient.sessionDestroy(sessionId, null);
            sessionId = null;
        }
    }

}
