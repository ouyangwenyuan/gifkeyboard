package com.fotoable.gifkeyboard.listener;

public interface FutureCallback<T> {
    /**
     * onCompleted is called by the Future with the result or exception of the asynchronous operation.
     *
     * @param e      Exception encountered by the operation
     * @param result Result returned from the operation
     */
    public void onCompleted(Exception e, T result);
}