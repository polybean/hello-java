module hello.java {
    requires java.logging;

    exports example.lambda;

    opens example.stream;
}
