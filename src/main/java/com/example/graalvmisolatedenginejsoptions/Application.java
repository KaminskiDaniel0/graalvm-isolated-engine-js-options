package com.example.graalvmisolatedenginejsoptions;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.SandboxPolicy;

import java.io.ByteArrayOutputStream;

public class Application {

    public static void main(String[] args) {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();

        final Context context = Context.newBuilder("js")
            .sandbox(SandboxPolicy.ISOLATED)
            .out(out)
            .err(err)
            .option("engine.MaxIsolateMemory", "1GB")
            .option("sandbox.MaxCPUTime", "150ms")
            .allowExperimentalOptions(true)
            .option("js.console", "false")
            .option("js.graal-builtin", "false") // only in TRUSTED
            .option("js.print", "false") // only in TRUSTED
            .option("js.load", "false") // only in TRUSTED
            .build();

        context.eval("js", """
                          
                print(Graal.versionECMAScript);
                print(Graal.versionGraalVM);
                print(Graal.isGraalRuntime());
                load({name: 'index.js', script: '\\'loading this should not have been possible\\';'});
                                
                """);
    }
}
