package com.github.ruediste.lambdaInspector;

import java.io.Serializable;

import com.ea.agentloader.AgentLoader;

public class LambdaInspector {

    public static void setup() {
        AgentLoader.loadAgentClass(LambdaInspectorAgent.class.getName(), "");
    }

    public static void inspect(Serializable lambda) {
    }
}
