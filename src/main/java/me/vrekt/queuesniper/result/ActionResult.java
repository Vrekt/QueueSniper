package me.vrekt.queuesniper.result;

import net.dv8tion.jda.core.entities.Message;

public class ActionResult implements ResultHandler {

    private final boolean failed;
    private Message message;

    ActionResult(boolean failed) {
        this.failed = failed;
    }

    public boolean failed() {
        return failed;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public void handleFailure(ResultConsumer consumer) {
        if (failed) {
            consumer.handleResult(message);
        }
    }

    @Override
    public void handleSuccess(ResultConsumer consumer) {
        if (!failed) {
            consumer.handleResult(message);
        }
    }
}
