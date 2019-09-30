package com.aseemsavio.reactivespring.playground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Aseem Savio
 * I'm playing around with the factory methods :p
 */
public class FluxAndMonoFactoryMethods {

    List<String> names = Arrays.asList("Aseem", "Savio", "Antigravity");

    @Test
    public void FluxFromIterable() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .log();
        stringFlux.subscribe(
                System.out::println,
                (error) -> {
                    System.err.println(error);
                },
                () -> {
                    System.out.println("Completed");
                }
        );

        StepVerifier.create(stringFlux)
                .expectNext("Aseem", "Savio", "Antigravity")
                .verifyComplete();
    }

    @Test
    public void fluxFromArray() {
        String[] names = new String[]{"Aseem", "Savio", "Antigravity"};
        StepVerifier.create(Flux.fromArray(names))
                .expectNext("Aseem", "Savio", "Antigravity")
                .verifyComplete();
    }

    @Test
    public void fromStream() {
        StepVerifier.create(Flux.fromStream(names.stream()))
                .expectNext("Aseem", "Savio", "Antigravity")
                .verifyComplete();
    }

    @Test
    public void monoJustOrEmpty() {
        // following both are null or empty monos.
        Mono<String> nullMono = Mono.empty();
        Mono<String> mono = Mono.justOrEmpty(null);

        // Can only verify complete
        StepVerifier.create(mono)
                .verifyComplete();

        StepVerifier.create(nullMono)
                .verifyComplete();
    }

    @Test
    public void monoWithSupplier(){
        Supplier<String> stringSupplier = () -> "Aseem";
        StepVerifier.create(Mono.fromSupplier(stringSupplier).log())
                .expectNext("Aseem")
                .verifyComplete();
    }

    @Test
    public void fluxWithRange(){
        Flux<Integer> integerFlux = Flux.range(1, 5)
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }
}
