package myspringboot.reactive.r2dbc;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxMapFlatMapTest {

    List<Customer> customerList = List.of(new Customer("gildong", "gildong@gmail.com"),
            new Customer("dooly", "dooly@gmail.com"));

    @Test
    public void transformUsingMap() {
        //public final <V> Flux<V> map(Function<? super T,? extends V> mapper)
        //Transform the items emitted by this Flux by applying a synchronous function to each item.
        Flux<Customer> customerFlux = Flux.fromIterable(customerList)
                .map(customer -> new Customer(customer.getName().toUpperCase(), customer.getEmail().toUpperCase()))
                .log();

        customerFlux.subscribe(System.out::println);

        StepVerifier.create(customerFlux)
                .expectNext(new Customer("GILDONG","GILDONG@GMAIL.COM"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        //
    }


}
