package myspringboot.reactive.r2dbc;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class BackPressureTest {
    @Test
    public void thenRequestTest() {
        Flux<Integer> flux = Flux.just(101, 201, 301).log();

        StepVerifier.create(flux)
                .thenRequest(1)
                .expectNext(101)
                .thenRequest(2)
                .expectNext(201, 301)
                .verifyComplete();
    }

    @Test
    public void subscriptionTest() {
        Flux<Integer> flux = Flux.range(1, 100).log();

        flux.subscribe(
                value -> System.out.println("OnNext = " + value),
                error -> error.printStackTrace(),
                () -> System.out.println("OnComplete"),
                subscription -> subscription.request(10)
        );

        StepVerifier.create(flux)
                .expectNextCount(10)
                .expectComplete();
                //java.lang.AssertionError: expectation "expectComplete" failed (expected: onComplete(); actual: onNext(11))
                //.verify();

        StepVerifier.create(flux)
                .expectNext(1,2,3,4,5,6,7,8,9,10)
                .expectComplete();
    }

    @Test
    public void cancelCallbackTest() {
        Flux<Integer> flux = Flux.range(1, 100).log();
        flux.doOnCancel(() -> System.out.println("Cancel Method Invoked.."))
                .doOnComplete(() -> System.out.println("Completed "))
                .subscribe(new BaseSubscriber<Integer>() {
                               @Override
                               protected void hookOnNext(Integer value) {
                                   try {
                                       Thread.sleep(500);
                                       request(1);
                                       System.out.println("value = " + value);
                                       if (value == 5) {
                                           cancel();
                                       }
                                   } catch (InterruptedException e) {
                                       e.printStackTrace();
                                   }
                               }
                           }
                );

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .thenCancel()
                .verify();


    }

    @Test
    public void baseSubscribe() {
        Flux<Integer> integerFlux = Flux.range(1, 30).log();

        integerFlux.subscribe(new BaseSubscriber<Integer>() {
            int consumed = 0;
            final int limit = 5;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("hookOnSubscribe called..");
                request(limit);
            }

            @Override
            protected void hookOnNext(Integer value) {
                consumed++;
                if(limit == consumed){
                    consumed = 0;
                    System.out.println("hookOnNext called");
                    request(limit);
                }
            }
        });
    }


}
