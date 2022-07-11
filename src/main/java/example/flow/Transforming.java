package example.flow;

import example.model.Employee;
import example.model.Freelancer;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

import java.util.logging.Logger;

import static example.model.Employee.employees;

public class Transforming {
    public static void main(String[] args) throws InterruptedException {

// Create End Publisher
        SubmissionPublisher<Employee> publisher = new SubmissionPublisher<>();

        // Create Processor
        MyProcessor transformProcessor = new MyProcessor(s -> new Freelancer(s.getId(), s.getId() + 100, s.getName()));

        // Create End Subscriber
        FreelancerSubscriber subs = new FreelancerSubscriber();

        // Create chain of publisher, processor and subscriber
        publisher.subscribe(transformProcessor); // publisher to processor
        transformProcessor.subscribe(subs); // processor to subscriber

        // Publish items
        System.out.println("Publishing Items to Subscriber");
        employees.forEach(publisher::submit);

        // Logic to wait for messages processing to finish
        while (employees.size() != subs.getCounter()) {
            Thread.sleep(10);
        }

        // Closing publishers
        publisher.close();
        transformProcessor.close();

        System.out.println("Exiting the app");
    }


    public static class FreelancerSubscriber implements Flow.Subscriber<Freelancer> {

        private Flow.Subscription subscription;

        private int counter = 0;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println("Subscribed for Freelancer");
            this.subscription = subscription;
            this.subscription.request(1); // requesting data from publisher
            System.out.println("onSubscribe requested 1 item for Freelancer");
        }

        @Override
        public void onNext(Freelancer item) {
            System.out.println("Processing Freelancer " + item);
            counter++;
            this.subscription.request(1);
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("Some error happened in MyFreelancerSubscriber");
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("All Processing Done for MyFreelancerSubscriber");
        }

        public int getCounter() {
            return counter;
        }

    }

    public static class MyProcessor extends SubmissionPublisher<Freelancer> implements Flow.Processor<Employee, Freelancer> {

        private Flow.Subscription subscription;
        private Function<Employee, Freelancer> function;

        public MyProcessor(Function<Employee, Freelancer> function) {
            super();
            this.function = function;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(Employee employee) {
            submit(function.apply(employee));
            subscription.request(1);
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("Done");
        }
    }
}