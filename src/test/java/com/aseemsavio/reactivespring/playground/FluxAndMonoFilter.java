package com.aseemsavio.reactivespring.playground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilter {

    List<String> names = Arrays.asList("Aseem", "Savio", "Antigravity");

    @Test
    public void filterTest() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(name -> name.startsWith("A"))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Aseem", "Antigravity")
                .verifyComplete();
    }

    @Test
    public void filterTestLength() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(name -> name.length() > 6)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Antigravity")
                .verifyComplete();
    }
}
