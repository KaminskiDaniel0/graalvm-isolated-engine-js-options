package com.example.graalvmisolatedenginejsoptions;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.SandboxPolicy;
import org.graalvm.polyglot.Value;
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
            .allowHostAccess(HostAccess.newBuilder()
                .allowAccessAnnotatedBy(HostAccess.Export.class)
                .allowAllImplementations(false)
                .allowAllClassImplementations(false)
                .allowMapAccess(true)
                .allowMutableTargetMappings()
                .methodScoping(true)
                .build())
            .option("sandbox.MaxStatements", "50000")
            ;


        final Engine engine = Engine.newBuilder("js")
            .sandbox(SandboxPolicy.ISOLATED)
            .err(err)
            .out(out)
            .option("engine.MaxIsolateMemory", "1GB")
            .option("sandbox.MaxCPUTime", "150ms")
            .option("js.console", "false")
//            .option("js.graal-builtin", "false") // only in TRUSTED
//            .option("js.print", "false") // only in TRUSTED
//            .option("js.load", "false") // only in TRUSTED
            .build();

        builder.engine(engine);

        try (final Context context = builder.build()) {

            final Value result = context.eval("js", """
                print(Graal.versionECMAScript);
                print(Graal.versionGraalVM);
                print(Graal.isGraalRuntime());
                load({name: 'index.js', script: '\\'loading this should not have been possible\\';'});
                """);

            System.out.println(result.as(Object.class));

            System.out.println(out);
            System.err.println(err);

        } catch (IllegalArgumentException illegalArgumentException) {
            System.err.println(illegalArgumentException);
        } catch (PolyglotException polyglotException) {
            System.err.println(polyglotException);
        }
    }
}
