package com.example.graalvmisolatedenginejsoptions;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.SandboxPolicy;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.ByteArrayOutputStream;

@SpringBootApplication
public class Application implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(final ApplicationArguments args) {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();

        final Context.Builder builder = Context.newBuilder("js")
            .sandbox(SandboxPolicy.ISOLATED)
            .out(out)
            .err(err)
            .option("js.console", "false"); // none of the "js.xyz" options are possible

        try (final Context context = builder.build()) {

            context.eval("js", """
                console.log('foobar');
                """);

        } catch (IllegalArgumentException illegalArgumentException) {
            System.err.println(illegalArgumentException);
        }
    }
}
