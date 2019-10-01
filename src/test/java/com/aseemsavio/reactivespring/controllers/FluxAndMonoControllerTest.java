package com.aseemsavio.reactivespring.controllers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@WebFluxTest  // won't scan the classes annotated with @Component, etc.
public class FluxAndMonoControllerTest {

    // To test a non blocking producer, we need a non blocking client.
    @Autowired
    WebTestClient webTestClient;

    @Test
    public void fluxTestApproachOne() {
        Flux<Integer> integerFlux = webTestClient.get().uri("/getFlux")
                .accept(MediaType.APPLICATION_JSON_UTF8)  // default
                .exchange() // actual call is made
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void fluxTestApproachTwo() {
        webTestClient.get().uri("/getFlux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void fluxTestApproachThree() {
        List<Integer> expectedList = Arrays.asList(1, 2, 3, 4);
        EntityExchangeResult<List<Integer>> listEntityExchangeResult = webTestClient.get().uri("/getFlux")
                .accept(MediaType.APPLICATION_JSON_UTF8)  // default
                .exchange() // actual call is made
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        Assert.assertEquals(expectedList, listEntityExchangeResult.getResponseBody());
    }

    @Test
    public void fluxTestApproachFour() {
        List<Integer> expectedList = Arrays.asList(1, 2, 3, 4);
        webTestClient.get().uri("/getFlux")
                .accept(MediaType.APPLICATION_JSON_UTF8)  // default
                .exchange() // actual call is made
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> {  // response is EntityExchangeResult. This is consumer functional interface.
                    Assert.assertEquals(response.getResponseBody(), expectedList);
                });
    }

    @Test
    public void FluxInfiniteStreamTest() {
        Flux<Long> longStreamFlux = webTestClient.get().uri("/getFluxInfiniteStreamJSON")
                .accept(MediaType.valueOf(MediaType.APPLICATION_STREAM_JSON_VALUE))  // default
                .exchange() // actual call is made
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longStreamFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L, 3L)
                .thenCancel()
                .verify();
    }
}