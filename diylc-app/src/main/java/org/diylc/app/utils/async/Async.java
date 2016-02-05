package org.diylc.app.utils.async;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Async {

    private static final Logger LOG = LoggerFactory.getLogger(Async.class);
    private BeforeAfter before;
    private BeforeAfter after;

    public Async() {
        this(() -> {
        }, () -> {
        });
    }

    public Async(BeforeAfter before, BeforeAfter after) {
        this.before = before;
        this.after = after;
    }

    public <T> void execute(final AsyncExecuter<T> executer) {
        execute(executer, (T value) -> {
        }, (Exception e) -> {
        });
    }

    public <T> void execute(final AsyncExecuter<T> executer, AsyncErrorHandler errorHandler) {
        execute(executer, (T value) -> {
        }, errorHandler);
    }

    public <T> void execute(final AsyncExecuter<T> executer, AsyncSuccessHandler<T> successHandler) {
        execute(executer, successHandler, (Exception e) -> {
        });
    }

    public <T> void execute(final AsyncExecuter<T> executer, final AsyncSuccessHandler<T> succesHandler,
            final AsyncErrorHandler errorHandler) {
        before.execute();

        SwingWorker<T, Void> worker = new SwingWorker<T, Void>() {

            @Override
            protected T doInBackground() throws Exception {
                return executer.execute();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    succesHandler.success(result);
                } catch (Exception e) {
                    LOG.error("Task failed", e);
                    errorHandler.failure(e);
                } finally {
                    after.execute();
                }
            }
        };

        worker.execute();
    }

    public static AsyncErrorHandler onError(AsyncErrorHandler errorHandler) {
        return errorHandler;
    }

    public static <T> AsyncSuccessHandler<T> onSuccess(AsyncSuccessHandler<T> asyncSuccessHandler) {
        return asyncSuccessHandler;
    }
}
