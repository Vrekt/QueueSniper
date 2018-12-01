package me.vrekt.queuesniper.result;

public interface ResultHandler {

    /**
     * Handle a failure (if there was one)
     *
     * @param consumer the consumer to call
     */
    void handleFailure(ResultConsumer consumer);

    /**
     * Handle a success
     *
     * @param consumer the consumer to call
     */
    void handleSuccess(ResultConsumer consumer);

}
