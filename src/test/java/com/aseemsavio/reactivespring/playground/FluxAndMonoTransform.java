package com.aseemsavio.reactivespring.playground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransform {

    List<String> names = Arrays.asList("aseem", "savio", "antigravity");

    @Test
    public void transformWithMap(){
        Flux<String> stringFlux = Flux.fromIterable(names)
                .map(name -> name.toUpperCase())
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("ASEEM", "SAVIO", "ANTIGRAVITY")
                .verifyComplete();
    }

    @Test
    public void transformWithMapLength(){
        Flux<Integer> integerFlux = Flux.fromIterable(names)
                .map(name -> name.length())
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(5, 5, 11)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap(){
        Flux<String> fluxIterable = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"))
                .flatMap(s -> {
                    // Each element will be returned as a Flux (Producer/Emitter)
                    return Flux.fromIterable(dummyDBCall(s));
                })  // Use case of a flatmap is - if you need to make a DB/external service calls for each element
                .log();

        StepVerifier.create(fluxIterable)
                .expectNextCount(10)
                .verifyComplete();
    }

    protected List<String> dummyDBCall(String s){
        try {
            Thread.sleep(1000);  // simulating a db call time.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "New Element");
    }

    // Order will be changed
    @Test
    public void transformUsingFlatMapParallelProcessing(){
        Flux<String> fluxIterable = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"))
                .window(2) // Passes 2 at a time instead of the default 1.
                .flatMap((s) -> {                                            // Order won't be maintained
                    return s.map(this::dummyDBCall).subscribeOn(parallel()); // returns Flux<List<String>> Ensures it runs in parallel.
                })
                .flatMap(s -> Flux.fromIterable(s)) // returns Flux<String>
                .log();

        StepVerifier.create(fluxIterable)
                .expectNextCount(10)
                .verifyComplete();
    }

    // Use ConcatMap to preserve order. But slow.
    @Test
    public void transformUsingFlatMapParallelProcessingSlowPreserveOrder(){
        Flux<String> fluxIterable = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"))
                .window(2) // Passes 2 at a time instead of the default 1.
                .flatMap((s) -> {                                            // Order won't be maintained
                    return s.map(this::dummyDBCall).subscribeOn(parallel()); // returns Flux<List<String>> Ensures it runs in parallel.
                })
                .flatMap(s -> Flux.fromIterable(s)) // returns Flux<String>
                .log();

        StepVerifier.create(fluxIterable)
                .expectNextCount(10)
                .verifyComplete();
    }

    // To maintain order and speed, use flatMapSequential
    @Test
    public void transformUsingFlatMapParallelProcessingMaintianOrderFast(){
        Flux<String> fluxIterable = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"))
                .window(2) // Passes 2 at a time instead of the default 1.
                .flatMapSequential((s) -> {                                   // Order won't be maintained
                    return s.map(this::dummyDBCall).subscribeOn(parallel()); // returns Flux<List<String>> Ensures it runs in parallel.
                })
                .flatMap(s -> Flux.fromIterable(s)) // returns Flux<String>
                .log();

        StepVerifier.create(fluxIterable)
                .expectNextCount(10)
                .verifyComplete();
    }


}
