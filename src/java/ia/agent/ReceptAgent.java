/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.agent;



import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.core.AID;

public class ReceptAgent extends Agent {
	
	int conversationId = 0;
        String [] keyWords;
	
	protected void setup(){
		System.out.println(this.getAID());
		this.addBehaviour(new ReceptBahaviour());
	}

	class ReceptBahaviour extends Behaviour {

		@Override
		public void action() {		
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM); 
			ACLMessage message = receive(mt);
			if(message != null){
				String s = message.getContent();
				keyWords = s.split(",");
                                
                                // Enviar a FB Bot Agent
                                
                                AID fbAgent = new AID("FbAgent", false);
                                 
				ACLMessage reply = message.createReply();
				reply.setContent(s + " received!");
				send(reply);
			}
		}

		@Override
		public boolean done() {
			return false;
		}
	}
	
}