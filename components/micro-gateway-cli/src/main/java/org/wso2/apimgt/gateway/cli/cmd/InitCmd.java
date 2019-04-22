/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.apimgt.gateway.cli.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.apimgt.gateway.cli.constants.GatewayCliConstants;
import org.wso2.apimgt.gateway.cli.exception.CLIInternalException;
import org.wso2.apimgt.gateway.cli.exception.HashingException;
import org.wso2.apimgt.gateway.cli.hashing.LibHashUtils;
import org.wso2.apimgt.gateway.cli.utils.GatewayCmdUtils;
import org.wso2.apimgt.gateway.cli.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * This command is used to initialize a new microgateway project.
 * Command will create the basic directory structure for a microgateway project.
 */
@Parameters(commandNames = "init", commandDescription = "initialize a new project")
public class InitCmd implements GatewayLauncherCmd {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitCmd.class);
    private static final PrintStream OUT = System.out;

    @SuppressWarnings("unused")
    @Parameter(hidden = true, required = true)
    private List<String> mainArgs;

    @SuppressWarnings("unused")
    @Parameter(names = {"-f", "--force"}, hidden = true, arity = 0)
    private boolean isForceful;

    @SuppressWarnings("unused")
    @Parameter(names = {"-d", "--deployment-config"}, hidden = true)
    private String deploymentConfigPath;

    @Override
    public void execute() {
        String workspace = GatewayCmdUtils.getUserDir();
        String projectName = GatewayCmdUtils.getSingleArgument(mainArgs);
        Path projectLocation = Paths.get(workspace + File.separator + projectName);
        boolean isDirectory = Files.isDirectory(projectLocation);

        if (isDirectory && !isForceful) {
            throw GatewayCmdUtils.createUsageException("Project name `" + projectName
                    + "` already exist. use -f or --force to forcefully update the project directory.");
        }

        // This is a valid force init
        if (isDirectory) {
            GatewayCmdUtils.deleteProject(projectName);
        }

        // Extract the zipped ballerina platform and runtime
        extractPlatformAndRuntime();
        init(projectName, deploymentConfigPath);

        OUT.println("Project '" + projectName + "' is initialized successfully.");
    }

    @Override
    public String getName() {
        return GatewayCliCommands.INIT;
    }

    @Override
    public void setParentCmdParser(JCommander parentCmdParser) {
    }

    /**
     * Create project directory structure and initial deployment configuration.
     *
     * @param projectName          name of the project being initialized
     * @param deploymentConfigPath path to deployment config file (used in k8s scenarios)
     */
    private static void init(String projectName, String deploymentConfigPath) {
        try {
            GatewayCmdUtils.createProjectStructure(projectName);
            GatewayCmdUtils.createDeploymentConfig(projectName, deploymentConfigPath);
        } catch (IOException e) {
            LOGGER.error("Error occurred while generating project configurations", e);
            throw new CLIInternalException("Error occurred while loading configurations.");
        }
    }

    /**
     * Extracts the platform and runtime and copy related jars and balos to extracted runtime and platform.
     */
    private void extractPlatformAndRuntime() {
        try {
            String libPath = GatewayCmdUtils.getCLILibPath();
            String baloPath = GatewayCliConstants.CLI_GATEWAY + File.separator + GatewayCliConstants.CLI_BALO;
            String breLibPath = GatewayCliConstants.CLI_BRE + File.separator + GatewayCliConstants.CLI_LIB;
            String runtimeExtractedPath = libPath + File.separator + GatewayCliConstants.CLI_RUNTIME;
            String platformExtractedPath =
                    GatewayCmdUtils.getCLILibPath() + File.separator + GatewayCliConstants.CLI_PLATFORM;
            try {
                boolean isChangesDetected = LibHashUtils.detectChangesInLibraries();

                // Delete already extracted files if changes detected.
                if (isChangesDetected) {
                    Files.deleteIfExists(Paths.get(runtimeExtractedPath));
                    Files.deleteIfExists(Paths.get(platformExtractedPath));
                }
            } catch (HashingException e) {
                LOGGER.error("Error while detecting changes in gateway libraries", e);
            }

            extractBallerinaDist(platformExtractedPath, libPath, baloPath, breLibPath);
            extractBallerinaDist(runtimeExtractedPath, libPath, baloPath, breLibPath);

        } catch (IOException e) {
            String message = "Error while unzipping platform and runtime while project setup";
            LOGGER.error(message, e);
            throw new CLIInternalException(message);
        }
    }

    private void extractBallerinaDist(String destination, String libPath, String baloPath, String breLibPath) throws IOException {
        if (!Files.exists(Paths.get(destination))) {
            ZipUtils.unzip(destination + GatewayCliConstants.EXTENSION_ZIP, destination, true);

            // Copy balo to the platform
            GatewayCmdUtils.copyFolder(libPath + File.separator + baloPath,
                    destination + File.separator + GatewayCliConstants.CLI_LIB + File.separator
                            + GatewayCliConstants.CLI_REPO);

            // Copy gateway jars to platform
            GatewayCmdUtils.copyFolder(libPath + File.separator + GatewayCliConstants.CLI_GATEWAY + File.separator
                    + GatewayCliConstants.CLI_PLATFORM, destination + File.separator + breLibPath);
        }
    }
}