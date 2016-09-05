/**
 * Copyright © 2016 Mathias Kowalzik (Mathias.Kowalzik@leandreck.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.leandreck.endpoints.processor;

import javax.annotation.processing.Processor;
import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by kowalzik on 04.09.2016.
 */
class CompilerTestHelper {

    private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private static final Charset utf8 = Charset.forName("UTF-8");

    static final List<Diagnostic<? extends JavaFileObject>> compileTestCase(Iterable<? extends Processor> processors, File... compilationUnitFiles) throws IOException {

        final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

        try (
                final StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, Locale.GERMAN, utf8);
        ) {
            // If we don't specify the location of the new class files, they will be
            // placed at the project's root directory.
            fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(new File("target/generated-sources/annotations")));

            final Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(compilationUnitFiles));

            final CompilationTask task = compiler.getTask(null, fileManager, diagnosticCollector, Arrays.asList("-proc:only"), null, compilationUnits);
            task.setProcessors(processors);
            task.call();
        }

        return diagnosticCollector.getDiagnostics();
    }
}
