package com.github.scratchpad

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Provides a basic state for working with Java modules:
 *  - enables the java plugin for the project
 *  - enables the errorprone compiler for the project
 *  - configures (and creates) the output directories for annotation processor generated classes
 *      - turns off warnings and errorprone for generated code
 *      - makes idea and eclipse know how to access the generated classes
 */
class BikeshedJavaPlugin implements Plugin<Project> {

    void apply(Project project) {

        project.plugins.apply("java")
        project.plugins.apply("net.ltgt.errorprone")

        project.dependencies {
            def errorprone_version = project.properties.get("errorprone_version")
            errorprone("com.google.errorprone:error_prone_core:${errorprone_version}")
        }

        project.sourceSets.configureEach { sourceSet ->
            def generatedSourcesDir = project.file("${project.buildDir}/generated/${sourceSet.name}/java")

            generatedSourcesDir.mkdirs()
            sourceSet.java { source ->
                if (!source.srcDirs.contains(generatedSourcesDir)) {
                    source.srcDir generatedSourcesDir
                }
            }

            def ideaModule = project.idea.module
            ideaModule.sourceDirs -= generatedSourcesDir
            ideaModule.testSourceDirs -= generatedSourcesDir
            if (!ideaModule.generatedSourceDirs.contains(generatedSourcesDir)) {
                ideaModule.generatedSourceDirs += generatedSourcesDir
            }

            project.tasks.named(sourceSet.getCompileTaskName("java")).configure {
                options.annotationProcessorGeneratedSourcesDirectory = generatedSourcesDir
                options.compilerArgs << "-Werror"
                options.deprecation = true
                options.errorprone.disableWarningsInGeneratedCode = true
                options.warnings = true
            }
        }
    }
}
