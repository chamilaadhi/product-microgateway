// Copyright (c)  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

public type TierConfiguration record {
    string policy;

};

public annotation TierConfiguration RateLimit on resource function;

public type APIConfiguration record {
    string apiVersion;
    string name;
    string publisher;
    string authorizationHeader?;
    json security;
    string apiTier;
    string[] authProviders = [];
};

public annotation APIConfiguration API on service;

public type ResourceConfiguration record {
    json security;
    string[] authProviders = [];
};

public annotation ResourceConfiguration Resource on resource function;

public type FilterConfiguration record {
    boolean skipAll = false;
};

public annotation FilterConfiguration Filters on service;


