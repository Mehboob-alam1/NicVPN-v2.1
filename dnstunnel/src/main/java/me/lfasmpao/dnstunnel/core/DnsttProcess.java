package me.lfasmpao.dnstunnel.core;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class DnsttProcess extends Process {
    private static final String TAG = DnsttProcess.class.getSimpleName();
    private static final Map<Context, DnsttProcess> sInstances = new HashMap<>();
    private Context mContext;
    private Thread thread = null;
    private Process process = null;
    private Boolean isDestroyed = false;

    private DnsttProcess(Context context) {
        this.mContext = context;
    }

    public static synchronized DnsttProcess createTunnel(Context context) {
        if (context == null) {
            return null;
        }
        synchronized (sInstances) {
            final Context appContext = context.getApplicationContext();
            DnsttProcess instance;
            if (!sInstances.containsKey(appContext)) {
                instance = new DnsttProcess(appContext);
                sInstances.put(appContext, instance);
            } else {
                instance = sInstances.get(appContext);
            }
            return instance;
        }
    }

    public void start(String[] args) {
        final Semaphore semaphore = new Semaphore(1);

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread = new Thread() {
            @Override
            public void run() {
                try {

                    LinkedList<String> argvlist = new LinkedList<String>();
                    Collections.addAll(argvlist, args);

                    isDestroyed = false;
                    while (!isDestroyed) {
                        long startTime = System.currentTimeMillis();
                        process = new ProcessBuilder(argvlist)
                                .redirectErrorStream(true)
                                .start();
                        semaphore.release();
                        process.waitFor();
                        if (System.currentTimeMillis() - startTime < 1000) {
                            isDestroyed = true;
                        }
                    }
                } catch (InterruptedException e) {
                    process.destroy();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }
        };

        thread.start();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        isDestroyed = true;
        thread.interrupt();
        if (process != null) {
            process.destroy();
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            //Ignore
        }
    }

    @Override
    public int exitValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getErrorStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int waitFor() throws InterruptedException {
        thread.join();
        return 0;
    }
}
