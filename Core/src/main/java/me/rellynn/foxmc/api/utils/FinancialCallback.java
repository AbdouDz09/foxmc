package me.rellynn.foxmc.api.utils;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public interface FinancialCallback<T> {

    public void done(T balance, T diff, Throwable err);
}
