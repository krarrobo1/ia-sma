/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;



/**
 *
 * @author richard
 */
public class FbAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("FbAgent iniciado...");
    }

    
    
    
   
    
    class Listen extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                // Add the SequentialBehaviour
            } else {
                block();
            }
        }

    }
}
