package com.aseemsavio.reactivespring.playground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author Aseem Savio
 * I'm just playing around with Flux and Mono
 */
public class FluxAndMonoPlaygroundTest {
    /**
     * Only way of accessing a Flux is by subscribing to it.
     * When you subscribe to a Flux. it starts emitting the values it holds.
     */

    @Test
    public void textFlux() {
        // Just reading from a Flux and printing in console.
        // From the logs: request(unbounded) -> unbounded means MAX VALUE
        Flux<String> stringFlux = Flux.just("Aseem", "Savio", "Antigravity")
                .log();
        stringFlux.subscribe(System.out::println);

        System.out.println();

        // Handling errors
        // Concatenating exception to the flux
        Flux<String> stringFluxError = Flux.just("Aseem", "Savio", "Antigravity")
                .concatWith(Flux.error(new RuntimeException("Some Exception Occured")))
                .concatWith(Flux.just("After Error")) // Won't be received. After error, Flux won't send anymore data.
                .log(); // logs the events
        stringFluxError.subscribe(System.out::println, (error) -> {
            System.err.println(error);
        });

        System.out.println();

        // On Completed, taken as a parameter in the subscribe method.
        Flux.just("A", "E", "I", "O", "U")
                .subscribe(System.out::println, (error) -> {
                            System.out.println(error);
                        },
                        () -> {
                            // on Complete, come to this block
                            System.out.println("Completed!!!");
                        });
    }

    @Test
    public void testingFlux() {
        Flux<String> stringFlux = Flux.just("Jill", "Jung", "Juck")
                .log();
        // This won't log the steps as the subscribe method hasn't been called.
        // Text should fail if the ORDER isn't right.
        StepVerifier.create(stringFlux)
                .expectNext("Jill")
                .expectNext("Jung")
                .expectNext("Juck")
                .expectComplete();  // here, verifyComplete() is a subscriber
    }

    @Test
    public void expectErrors() {
        Flux<String> stringFlux = Flux.just("Hi", "my", "name", "is", "Aseem")
                .concatWith(Flux.error(new RuntimeException("Some Runtime Exception")))
                .log();

        // Should be in order
        StepVerifier.create(stringFlux)
                .expectNext("Hi")
                .expectNext("my")
                .expectNext("name")
                .expectNext("is")
                .expectNext("Aseem")
                .expectError(RuntimeException.class)
                .verify(); // here, verify() is the subscriber

        // Verifying error message
        StepVerifier.create(stringFlux)
                .expectNext("Hi", "my", "name", "is", "Aseem")
                .expectErrorMessage("Some Runtime Exception")
                .verify();
    }

    @Test
    public void fluxCount(){
        Flux<String> stringFlux = Flux.just("Hi", "my", "name", "is", "Aseem")
                .concatWith(Flux.error(new RuntimeException("Some Runtime Exception")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(5)
                .expectErrorMessage("Some Runtime Exception")
                .verify();
    }

    @Test
    public void testMono(){
        Mono<String> stringMono = Mono.just("Aseem")
                .log();

        // Same procedure
        StepVerifier.create(stringMono)
                .expectNext("Aseem")
                .verifyComplete();
    }

    @Test
    public void testMonoError(){
        StepVerifier.create(Mono.error(new RuntimeException("Hello, Exception")))
                .expectError(RuntimeException.class)
                .verify();
    }
}
