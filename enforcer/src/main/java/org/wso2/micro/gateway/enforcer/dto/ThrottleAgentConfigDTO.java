/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.micro.gateway.enforcer.dto;

import org.wso2.micro.gateway.enforcer.globalthrottle.databridge.agent.conf.AgentConfiguration;
import org.wso2.micro.gateway.enforcer.globalthrottle.databridge.publisher.PublisherConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * This contains throttle configurations.
 */
public class ThrottleAgentConfigDTO {
    boolean enabled = false;
    String username;
    String password;
    List<ThrottleURLGroupDTO> urlGroup = new ArrayList<>();
    PublisherConfiguration publisher;
    AgentConfiguration agent;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ThrottleURLGroupDTO> getUrlGroup() {
        return urlGroup;
    }

    public void setUrlGroup(List<ThrottleURLGroupDTO> urlGroup) {
        this.urlGroup = urlGroup;
    }

    public PublisherConfiguration getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherConfiguration publisher) {
        this.publisher = publisher;
    }

    public AgentConfiguration getAgent() {
        return agent;
    }

    public void setAgent(AgentConfiguration agent) {
        this.agent = agent;
    }
}
