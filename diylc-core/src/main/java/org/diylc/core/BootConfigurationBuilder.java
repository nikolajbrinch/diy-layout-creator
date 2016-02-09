package org.diylc.core;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builder for a BootConfiguation.
 * 
 * @author nikolajbrinch@gmail.com
 */
public class BootConfigurationBuilder {

    private String mainClassName;

    private final Map<String, String> inputArguments = new HashMap<>();

    private final Set<String> arguments;

    public BootConfigurationBuilder(String[] args) {
        this.arguments = new HashSet<String>(Arrays.asList(args));
    }

    public BootConfigurationBuilder setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;

        return this;
    }

    public BootConfigurationBuilder addInputArgument(String name, String value) {
        this.inputArguments.put(name, value);

        return this;
    }

    public BootConfigurationBuilder addInputArgument(String name) {
        this.inputArguments.put(name, null);

        return this;
    }

    public BootConfigurationBuilder addArgument(String argument) {
        this.arguments.add(argument);

        return this;
    }

    public BootConfiguration build() {
        String javaHome = System.getProperty("java.home");
        File workingDirectory = new File(System.getProperty("user.dir"));

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> classPath = Arrays.asList(runtimeMXBean.getClassPath().split(File.pathSeparator));
        List<String> inputArguments = runtimeMXBean.getInputArguments();
        
        for (String inputArgument : inputArguments) {
            String[] argument = inputArgument.split("=");
            if (argument.length == 2) {
                this.inputArguments.put(argument[0], argument[1]);
            } else {
                this.inputArguments.put(inputArgument, null);
            }
        }

        List<String> command = new ArrayList<>();

        String javaExecutable = BootUtils.findJavaExecutable(javaHome);

        command.add(javaExecutable);

        command.add("-classpath");
        command.add(String.join(File.pathSeparator, classPath));

        for (Map.Entry<String, String> inputArgument : this.inputArguments.entrySet()) {
            if (inputArgument.getValue() == null) {
                command.add(inputArgument.getKey());
            } else {
                command.add(inputArgument.getKey() + "=" + inputArgument.getValue());
            }
        }

        if (mainClassName == null || mainClassName.isEmpty()) {
            throw new IllegalStateException("No mainClass parameter defined!");
        }

        command.add(mainClassName);

        List<String> args = createArgs();

        command.addAll(args);

        return new BootConfiguration(workingDirectory, command);
    }

    private List<String> createArgs() {
        List<String> args = new ArrayList<>();

        for (String argument : arguments) {
            args.add("\"" + argument + "\"");
        }

        return args;
    }

}