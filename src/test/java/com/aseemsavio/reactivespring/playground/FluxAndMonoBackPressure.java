package com.aseemsavio.reactivespring.playground;

import org.junit.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Notes:
 * Project reactor is a Pull based model.
 * Subscriber gets full control over the data.
 * Subscriber pulls required data from the publisher. Based on that the less powerful publisher pushes the data.
 */
public class FluxAndMonoBackPressure {

    @Test
    public void backPressure() {
        /**
         * Subscriber gets a lot of control.
         * Subscriber asks the publisher ()external service/db to give how much ever data it wants in batches.
         * then cancel when it thinks it's okay with it.
         */
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        // While testing StepVerifier acts as a subscriber.
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void subscribeBackPressure(){
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();
        integerFlux.subscribe(
                element -> System.out.println("Element: " + element),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("EmissionComplete"), // won't be emitted as onComplete isn't happening
                subscription -> subscription.request(2)  // will emit only two elements.
        );
    }

    @Test
    public void subscribeBackPressureCancel(){
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();
        integerFlux.subscribe(
                (element) -> System.out.println(element),
                (exception) -> System.err.println(exception),
                () -> System.out.println("Emission Done"),
                (subscription) -> subscription.cancel()
        );
    }

    /**
     * Gives more control to the subscriber.
     * Requests values one by one until the value is a certain number
     * onComplete() doesn't happen
     */
    @Test
    public void customizedBackPressure(){
        Flux<Integer> integerFlux = Flux.range(1, 10);
        integerFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);  // emit one by one
                System.out.println("Value is: " + value);
                if(value == 4)
                    cancel();
            }
        });
    }

}
