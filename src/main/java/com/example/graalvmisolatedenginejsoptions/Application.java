package com.example.graalvmisolatedenginejsoptions;

import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.SandboxPolicy;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.ByteArrayOutputStream;

@SpringBootApplication
@Slf4j
public class Application implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(final ApplicationArguments args) {

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ByteArrayOutputStream err = new ByteArrayOutputStream();

            final Context.Builder builder = Context.newBuilder("js")
                .sandbox(SandboxPolicy.ISOLATED)
                .err(err)
                .out(out)
                .allowHostAccess(HostAccess.newBuilder()
                    .allowAccessAnnotatedBy(HostAccess.Export.class)
                    .allowAllImplementations(false)
                    .allowAllClassImplementations(false)
                    .allowMapAccess(true)
                    .allowMutableTargetMappings()
                    .methodScoping(true)
                    .build())
                .option("sandbox.MaxStatements", "50000");

            final Engine engine = Engine.newBuilder("js")
                .sandbox(SandboxPolicy.ISOLATED)
                .err(err)
                .out(out)
                .option("engine.MaxIsolateMemory", "1GB")
                .option("sandbox.MaxCPUTime", "150ms")
                .option("sandbox.MaxHeapMemory", "50MB")
                .option("sandbox.MaxASTDepth", "50")
                .option("sandbox.MaxStackFrames", "5")
                .option("sandbox.MaxThreads", "1")
                .option("sandbox.MaxOutputStreamSize", "16KB")
                .option("sandbox.MaxErrorStreamSize", "16KB")
                .allowExperimentalOptions(true)
                .logHandler(new SLF4JBridgeHandler())
                .option("engine.WarnInterpreterOnly", "false")
//                .option("js.graal-builtin", "false")
//                .option("js.load", "false")
//                .option("js.console", "false")
//                .option("js.print", "false")
                .build();
            builder.engine(engine);

            try (final Context context = builder.build()) {

                context.eval("js", """
                            function main() {
                            console.log('Some random log statement via console.log');
                            console.error('An error log statement via console.err');
                            }
                            main()
                    """);

                log.info(out.toString());
                log.warn(err.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("First round done, now an exception will get thrown because the js.xyz options are not allowed in an ISOLATED context/engine");

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ByteArrayOutputStream err = new ByteArrayOutputStream();

            final Context.Builder builder = Context.newBuilder("js")
                .sandbox(SandboxPolicy.ISOLATED)
                .err(err)
                .out(out)
                .allowHostAccess(HostAccess.newBuilder()
                    .allowAccessAnnotatedBy(HostAccess.Export.class)
                    .allowAllImplementations(false)
                    .allowAllClassImplementations(false)
                    .allowMapAccess(true)
                    .allowMutableTargetMappings()
                    .methodScoping(true)
                    .build())
                .option("sandbox.MaxStatements", "50000");

            final Engine engine = Engine.newBuilder("js")
                .sandbox(SandboxPolicy.ISOLATED)
                .err(err)
                .out(out)
                .option("engine.MaxIsolateMemory", "1GB")
                .option("sandbox.MaxCPUTime", "150ms")
                .option("sandbox.MaxHeapMemory", "50MB")
                .option("sandbox.MaxASTDepth", "50")
                .option("sandbox.MaxStackFrames", "5")
                .option("sandbox.MaxThreads", "1")
                .option("sandbox.MaxOutputStreamSize", "16KB")
                .option("sandbox.MaxErrorStreamSize", "16KB")
                .allowExperimentalOptions(true)
                .logHandler(new SLF4JBridgeHandler())
                .option("engine.WarnInterpreterOnly", "false")
                .option("js.graal-builtin", "false")
                .option("js.load", "false")
                .option("js.console", "false")
                .option("js.print", "false")
                .build();
            builder.engine(engine);
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("One of the passed options was not allowed: ", illegalArgumentException);
        }

    }
}
