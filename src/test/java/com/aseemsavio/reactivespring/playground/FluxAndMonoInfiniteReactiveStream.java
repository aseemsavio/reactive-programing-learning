package com.aseemsavio.reactivespring.playground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * How Flux and Mono works with time
 */
public class FluxAndMonoInfiniteReactiveStream {

    @Test
    public void infiniteSequence() {
        /**
         * This testcase executes in the main thread. But the actual emission of values happen in a parallel thread.
         */

        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .log(); // emits a long value. Starts from 0 to infinity.
        infiniteFlux.subscribe((element) -> System.out.println(element));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void finiteFlux(){
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3) // emits 3 elements
                .log();

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

}
