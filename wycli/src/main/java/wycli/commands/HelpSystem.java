// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wycli.commands;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import wycli.cfg.Configuration;
import wycli.lang.Command;
import wycc.lang.Path;

public class HelpSystem implements Command {

    public static final Configuration.Schema SCHEMA = Configuration
            .fromArray(Configuration.BOUND_INTEGER(Path.fromString("width"), "fix display width", false, 0));

    public static final List<Option.Descriptor> OPTIONS = Arrays
            .asList(Command.OPTION_NONNEGATIVE_INTEGER("width", "fix display width", 80));

    /**
     * The descriptor for this command.
     */
    public static final Command.Descriptor DESCRIPTOR = new Command.Descriptor() {
        @Override
        public String getName() {
            return "help";
        }

        @Override
        public String getDescription() {
            return "Display help information";
        }

        @Override
        public List<Option.Descriptor> getOptionDescriptors() {
            return OPTIONS;
        }

        @Override
        public Configuration.Schema getConfigurationSchema() {
            return SCHEMA;
        }

        @Override
        public List<Descriptor> getCommands() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public Command initialise(Command.Environment environment) {
            return new HelpSystem(System.out, environment);
        }
    };
    //
    private final PrintStream out;
    private final Command.Environment environment;

    public HelpSystem(PrintStream out, Command.Environment environment) {
        this.environment = environment;
        this.out = out;
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public void initialise() {
    }

    @Override
    public void finalise() {
    }

    @Override
    public boolean execute(Path path, Template template) throws Exception {
        // Extract arguments
        List<String> args = template.getArguments();
        //
        if (args.size() == 0) {
            printUsage();
        } else {
            // Search for the command
            List<Command.Descriptor> descriptors = environment.getCommandDescriptors();
            //
            Command.Descriptor command = null;
            for (Command.Descriptor c : descriptors) {
                if (c.getName().equals(args.get(0))) {
                    command = c;
                    break;
                }
            }
            //
            if (command == null) {
                out.println("No entry for " + args.get(0));
            } else {
                print(out, command);
            }
        }
        //
        return true;
    }

    public static void print(PrintStream out, Command.Descriptor descriptor) {
        out.println("NAME");
        out.println("\t" + descriptor.getName());
        out.println();
        out.println("DESCRIPTION");
        out.println("\t" + descriptor.getDescription());
        out.println();
        out.println("OPTIONS");
        List<Option.Descriptor> options = descriptor.getOptionDescriptors();
        for (int i = 0; i != options.size(); ++i) {
            Option.Descriptor option = options.get(i);
            String argument = option.getArgumentDescription();
            out.print("\t--" + option.getName());
            if(argument != null && !argument.equals("")) {
                out.print("=" + argument);
            }
            out.println();
            out.println("\t\t" + option.getDescription());
        }
        out.println();
        out.println("SUBCOMMANDS");
        List<Command.Descriptor> commands = descriptor.getCommands();
        for (int i = 0; i != commands.size(); ++i) {
            Command.Descriptor d = commands.get(i);
            out.println("\t" + d.getName());
            out.println("\t\t" + d.getDescription());
        }
        out.println();
        out.println("CONFIGURATION");
        Configuration.Schema schema = descriptor.getConfigurationSchema();
        List<Configuration.KeyValueDescriptor<?>> descriptors = schema.getDescriptors();
        for (int i = 0; i != descriptors.size(); ++i) {
            Configuration.KeyValueDescriptor<?> option = descriptors.get(i);
            out.println("\t" + option.getFilter());
            out.println("\t\t" + option.getDescription());
        }
    }

    /**
     * Print usage information to the console.
     */
    protected void printUsage() {
        List<Command.Descriptor> descriptors = environment.getCommandDescriptors();
        //
        out.println("usage: wy [--verbose] command [<options>] [<args>]");
        out.println();
        int maxWidth = determineCommandNameWidth(descriptors);
        out.println("Commands:");
        for (Command.Descriptor d : descriptors) {
            out.print("  ");
            out.print(rightPad(d.getName(), maxWidth));
            out.println("   " + d.getDescription());
        }
        out.println();
        out.println("Run `wy help COMMAND` for more information on a command");
    }

    /**
     * Right pad a given string with spaces to ensure the resulting string is
     * exactly n characters wide. This assumes the given string has at most n
     * characters already.
     *
     * @param str
     *            String to right-pad
     * @param n
     *            Width of resulting string
     * @return
     */
    public static String rightPad(String str, int n) {
        return String.format("%1$-" + n + "s", str);
    }

    /**
     * Left pad a given string with spaces to ensure the resulting string is
     * exactly n characters wide. This assumes the given string has at most n
     * characters already.  No, this is not its own library.
     *
     * @param str
     *            String to left-pad
     * @param n
     *            Width of resulting string
     * @return
     */
    public static String leftPad(String str, int n) {
        return String.format("%1$" + n + "s", str);
    }

    /**
     * Determine the maximum width of any configured command name
     *
     * @param descriptors
     * @return
     */
    private static int determineCommandNameWidth(List<Command.Descriptor> descriptors) {
        int max = 0;
        for (Command.Descriptor d : descriptors) {
            max = Math.max(max, d.getName().length());
        }
        return max;
    }
}
