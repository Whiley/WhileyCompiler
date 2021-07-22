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
package wycli;

import wycc.lang.Filter;
import wycc.util.AbstractCompilationUnit;
import wycli.cfg.Configuration;
import wycc.lang.Path;

import java.util.regex.Pattern;

/**
 * Provides a single point of truth for all schemas used within this tool.
 */
public class Schemas {

    /**
     * Schema for system configuration (i.e. which applies to all users).
     */
    public static Configuration.Schema SYSTEM_CONFIG_SCHEMA = Configuration.fromArray(
            Configuration.UNBOUND_STRING(Filter.fromString("plugins/*"), "list of globally installed plugins", true));

    /**
     * Schema for global configuration (i.e. which applies to all projects for a given user).
     */
    public static Configuration.Schema GLOBAL_CONFIG_SCHEMA = Configuration.fromArray(
            Configuration.UNBOUND_STRING(Path.fromString("user/name"), "username", false),
            Configuration.UNBOUND_STRING(Path.fromString("user/email"), "email", false));

    /**
     * Schema for local configuration (i.e. which applies to a given workspace).
     */
    public static Configuration.Schema LOCAL_CONFIG_SCHEMA = Configuration.fromArray(
            Configuration.UNBOUND_STRING_ARRAY(Path.fromString("workspace/projects"), "list of projects", false));


    /**
     * This determines what files are included in a package be default (i.e. when
     * the build/includes attribute is not specified).
     */
    public static final AbstractCompilationUnit.Value.Array DEFAULT_BUILD_INCLUDES = new AbstractCompilationUnit.Value.Array(
            // Include package description by default
            new AbstractCompilationUnit.Value.UTF8("wy.toml"),
            // Include all wyil files by default
            new AbstractCompilationUnit.Value.UTF8("**/*.wyil"),
            // Include all whiley files by default
            new AbstractCompilationUnit.Value.UTF8("**/*.whiley")
    );

    /**
     * Schema for packages (i.e. which applies to a single project for a given user).
     */
    public static Configuration.Schema PACKAGE = Configuration.fromArray(
            // Required items
            Configuration.UNBOUND_STRING(Path.fromString("package/name"), "Name of this package", new AbstractCompilationUnit.Value.UTF8("main")),
            Configuration.UNBOUND_STRING_ARRAY(Path.fromString("package/authors"), "Author(s) of this package", false),
            Configuration.UNBOUND_STRING(Path.fromString("package/version"), "Semantic version of this package", false),
            // Build items
            Configuration.UNBOUND_STRING_ARRAY(Path.fromString("build/platforms"),
                    "Target platforms for this package (default just \"whiley\")",
                    new AbstractCompilationUnit.Value.Array(new AbstractCompilationUnit.Value.UTF8("whiley"))),
            Configuration.UNBOUND_STRING_ARRAY(Path.fromString("build/includes"), "Files to include in package",
                    DEFAULT_BUILD_INCLUDES),
            Configuration.UNBOUND_STRING(Path.fromString("build/main"), "Identify main method", false),
            // Optional items
            Configuration.REGEX_STRING(Filter.fromString("dependencies/*"), "Packages this package depends on", false,
                    Pattern.compile("\\d+.\\d+.\\d+"))
    );

}
