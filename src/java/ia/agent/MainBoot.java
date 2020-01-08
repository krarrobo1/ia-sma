/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.agent;
import ia.controller.PostController;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

/**
 *
 * @author richard
 */
public class MainBoot {
    public static String MAIN_PROPERTIES_FILE = "properties/main";
	 
	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		try {
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			AgentContainer mc = rt.createMainContainer(p);
			AgentController ac = mc.createNewAgent("Reception", "ia.agent.ReceptAgent", null);
			ac.start();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
