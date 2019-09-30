package com.aseemsavio.reactivespring.playground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoCombine {

    @Test
    public void combineUsingMerge(){

        // Merge won't merge in order in case of delay.
        // Imagine these are fluxes from 2 diff external services/ DBs.
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()  // expecting subscription
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMergeWithDelay(){

        // Imagine these are fluxes from 2 diff external services/ DBs.
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        /**
         * merge will not wait until the flux 1 finishes its waiting.
         * It goes ahead and does the other operations. Order gets f'd up.
         * So, expectNext() fails.
         *
         * Flux 2 won't wait until all the elements of flux 1 gets emitted.
         */

        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()  // expecting subscription
                //.expectNext("A", "B", "C", "D", "E", "F")
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat(){
        // Maintains order
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> concatnatedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(concatnatedFlux.log())
                .expectSubscription()  // expecting subscription
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcatWithDelay(){
        // Slower than merge().Maintains order
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> concatnatedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(concatnatedFlux.log())
                .expectSubscription()  // expecting subscription
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingZip(){
        // Maintains order
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> zippedFlux = Flux.zip(flux1, flux2, (t1, t2) -> {  // t1, t2 are first elements of both the fluxes.
            return t1.concat(t2);  // returns AD, BE, CF
        });

        StepVerifier.create(zippedFlux.log())
                .expectSubscription()  // expecting subscription
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
