package example.flow;

import example.model.Employee;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import static example.model.Employee.employees;

public class Streaming {
    public static void main(String[] args) throws InterruptedException {

        // Create Publisher
        SubmissionPublisher<Employee> publisher = new SubmissionPublisher<>();

        // Register Subscriber
        EmployeeSubscriber subscriber = new EmployeeSubscriber();
        publisher.subscribe(subscriber);

        // Publish items
        System.out.println("Publishing Items to Subscriber");
        employees.forEach(publisher::submit);

        // logic to wait till processing of all messages are over
        while (employees.size() != subscriber.getCounter()) {
            Thread.sleep(10);
        }

        // close the Publisher
        publisher.close();

        System.out.println("Exiting the app");
    }

    public static class EmployeeSubscriber implements Flow.Subscriber<Employee> {
        private Flow.Subscription subscription;
        private int counter = 0;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println("Subscribed");
            this.subscription = subscription;
            this.subscription.request(1); // requesting data from publisher
            System.out.println("onSubscribe requested 1 item");
        }

        @Override
        public void onNext(Employee item) {
            System.out.println("Processing Employee " + item);
            counter++;
            this.subscription.request(1);
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("Some error happened");
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("All Processing Done");
        }

        public int getCounter() {
            return counter;
        }
    }
}
