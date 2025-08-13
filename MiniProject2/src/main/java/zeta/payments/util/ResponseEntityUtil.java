package zeta.payments.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseEntityUtil {

    public static <T> ResponseEntity<T> getResultWithResponseEntity(T value) {
         return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(value);
    }

    private static BiConsumer<Object, Throwable> thenOnException(
            Consumer<Throwable> throwableConsumer) {
        return (o, throwable) -> {
            if (throwable != null) {
                throwableConsumer.accept(getUnwrappedException(throwable));
            }
        };
    }

    private static Throwable getUnwrappedException(Throwable throwable) {
        while (throwable instanceof CompletionException
                || throwable instanceof ExecutionException
                || throwable instanceof InvocationTargetException) {
            throwable = throwable.getCause();
        }
        return throwable;
    }
}
