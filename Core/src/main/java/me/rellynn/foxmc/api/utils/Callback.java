package me.rellynn.foxmc.api.utils;

/**
 * Created by gwennaelguich on 26/06/2017.
 * FoxMC Network.
 */
public interface Callback<T> {

    public void done(T value);
}
