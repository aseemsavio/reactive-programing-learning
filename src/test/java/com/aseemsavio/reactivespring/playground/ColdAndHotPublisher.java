package com.aseemsavio.reactivespring.playground;

import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisher {

    /**
     * Can be thought of like a Http request.
     * For every Http request, Flux emits values from the beginning.
     * Called Cold publishing.
     * @throws InterruptedException
     */
    @Test
    public void coldPublisher() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F", "G")
                .delayElements(Duration.ofSeconds(1));
        stringFlux.subscribe((value) -> System.out.println("Subscriber 1: " + value)); // emits value from the beginning for every subscriber.
        Thread.sleep(2000);
        stringFlux.subscribe((value) -> System.out.println("Subscriber 2: " + value));
        Thread.sleep(3000);
        stringFlux.subscribe((value) -> System.out.println("Subscriber 3: " + value));
    }

    @Test
    public void hotPublisher() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F", "G")
                .delayElements(Duration.ofSeconds(1));
        ConnectableFlux<String> stringConnectableFlux = stringFlux.publish();
        stringConnectableFlux.connect();  // makes it a hot publisher

        stringConnectableFlux.subscribe(
                (value) -> System.out.println("Subscriber 1: " + value)
        );
        Thread.sleep(4000);
        stringConnectableFlux.subscribe(
                (value) -> System.out.println("Subscriber 2: " + value)  // doesn't emit values from the beginning.
        );
        Thread.sleep(5000);
    }
}
