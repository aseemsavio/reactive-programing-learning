package com.aseemsavio.reactivespring.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    /**
     * The browser is the subscriber here.
     * This returns a normal JSON
     * The browser is a blocking client and so it waits for the server to send the entire JSON
     */
    @GetMapping("/getFlux")
    public Flux<Integer> getFlux(){
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    /**
     * Now, the values stream into the browser.
     * Browser doesn't wait for the entire JSON to come. It becomes Non-blocking now.
     * Result will be rendered as it is available.
     */
    @GetMapping(value = "/getFluxStreamJSON", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> getFluxStream(){
        return Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    /**
     * If the application shuts down, a cancel() event is formed. Will be sent to the subscriber.
     * Thr sub handles it gracefully without breaking.
     * @return
     */
    @GetMapping(value = "/getFluxInfiniteStreamJSON", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> getFluxInfiniteStream(){
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }



}
