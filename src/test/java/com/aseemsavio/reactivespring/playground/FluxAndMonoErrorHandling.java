package com.aseemsavio.reactivespring.playground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorHandling {

    // Returns a Flux value in case of an exception
    @Test
    public void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorResume((error) -> { // on error, this block gets executes.
                    System.out.println(error);
                    return Flux.just("default", "default1");
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                .expectNext("default", "default1")// D won't be emitted as error occured.
                .verifyComplete();
    }

    // Returns a fallback value which is not a flux.
    @Test
    public void fluxErrorHandlingFallBackValue() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("fallback value")
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                .expectNext("fallback value")// D won't be emitted as error occured.
                .verifyComplete();
    }

    // Custom Exception
    @Test
    public void fluxErrorHandlingOnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap((exception) -> new AseemException(exception))
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                .expectError(AseemException.class)
                .verify();
    }


    /**
     * Retry
     *
     * Retries twice in case of exception.
     * Then throws the exception.
     */
    @Test
    public void fluxErrorHandlingWithRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap((exception) -> new AseemException(exception))
                .retry(2) // will retry twice
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C") // will be retried twice
                //.expectError(RuntimeException.class)
                .expectError(AseemException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingWithRetryBackOff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap((exception) -> new AseemException(exception))
                .retryBackoff(2, Duration.ofSeconds(5)) // will retry twice with a 5 sec back off
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C") // will be retried twice
                //.expectError(RuntimeException.class)
                .expectError(IllegalStateException.class)
                .verify();
    }
}

