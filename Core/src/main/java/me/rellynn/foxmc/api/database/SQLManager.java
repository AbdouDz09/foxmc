package me.rellynn.foxmc.api.database;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.javalite.activejdbc.Base;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by gwennaelguich on 03/04/2017.
 * FoxMC Network.
 */
public class SQLManager {
    private String url;
    private String user;
    private String password;
    private ExecutorService executorService;

    public SQLManager(String url, String user, String password, int threads) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.executorService = Executors.newFixedThreadPool(threads, new ThreadFactoryBuilder().setNameFormat("FoxCore-SQL").build());
    }

    private void setupConnection() {
        if (Base.hasConnection()) {
            return;
        }
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("useSSL", "false");
        Base.open("com.mysql.jdbc.Driver", url, properties);
    }

    private void closeConnection() {
        Base.close();
    }

    /**
     * Execute an operation on the database thread and return a result.
     *
     * @param callable The operation
     * @param <T>      The return type
     * @return Future
     */
    public <T> Future<T> execute(Callable<T> callable) {
        return executorService.submit(() -> {
            T value;
            try {
                setupConnection();
                value = callable.call();
            } finally {
                closeConnection();
            }
            return value;
        });
    }

    /**
     * Execute an operation on the database thread.
     *
     * @param runnable The operation
     */
    public void execute(Runnable runnable) {
        executorService.execute(() -> {
            try {
                setupConnection();
                runnable.run();
            } finally {
                closeConnection();
            }
        });
    }
}
